package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.domain.*;
import cn.wolfcode.shop.mapper.InvoiceMapper;
import cn.wolfcode.shop.mapper.OrderInfoMapper;
import cn.wolfcode.shop.mapper.OrderProductMapper;
import cn.wolfcode.shop.mapper.OrderProductPropertyMapper;
import cn.wolfcode.shop.qo.OrderQueryObject;
import cn.wolfcode.shop.qo.PageResult;
import cn.wolfcode.shop.service.*;
import cn.wolfcode.shop.util.IdGenerateUtil;
import cn.wolfcode.shop.util.RedisConstant;
import cn.wolfcode.shop.vo.OrderStatusChangeVo;
import cn.wolfcode.shop.vo.OrderVo;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService{

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private OrderProductPropertyMapper orderProductPropertyMapper;

    @Autowired
    private IOrderActionService orderActionService;


    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private IUserAddressService userAddressService;

    @Reference
    private IProductSkuService productSkuService;

    @Reference
    private ICartService cartService;

    @Override
    public void generateOrder(OrderVo vo) {
        //通过token获取当前登录用户
        String token = vo.getToken();
        if (token == null || token.trim().length() == 0) {
            throw new RuntimeException("登陆用户信息异常");
        }
        UserLogin userLogin = (UserLogin) redisTemplate.opsForValue().get(RedisConstant.CURRENT_USER + token);
        if (userLogin  == null) {
            throw new RuntimeException("登陆超时");
        }
        //创建订单对象，设置用户id
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userLogin.getId());
        //生成订单编号
        long orderSn = IdGenerateUtil.get().nextId();
        orderInfo.setOrderSn(orderSn + "");
        //通过用户收货地址id获取用户收货地址对象，并设置到订单信息中
        UserAddress userAddress = userAddressService.get(vo.getUserAddressId());
        orderInfo.setPhone(userAddress.getPhone());
        orderInfo.setConsignee(userAddress.getConsignee());
        orderInfo.setCountry(userAddress.getCountry());
        orderInfo.setProvince(userAddress.getProvince());
        orderInfo.setCity(userAddress.getCity());
        orderInfo.setDistrict(userAddress.getDistrict());
        orderInfo.setAddress(userAddress.getAddress());
        orderInfo.setZipcode(userAddress.getZipcode());
        //设置订单的其他信息，比如订单状态，收货状态，物流状态，下单时间等
        orderInfo.setPayType(vo.getPayVo().getPayType());
        orderInfo.setOrderStatus(new Byte("0"));
        orderInfo.setFlowStatus(new Byte("0"));
        orderInfo.setPayStatus(new Byte("0"));
        orderInfo.setOrderTime(new Date());
        //设置订单总金额
        BigDecimal orderAmount = BigDecimal.ZERO;
        orderInfo.setOrderAmount(orderAmount);
        //插入该订单
        orderInfoMapper.insert(orderInfo);

        //获取购物车商品列表遍历，并生成订单商品明细
        for (Cart cart : vo.getCartList()) {
            //car的信息转移到订单商品明细中
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrderId(orderInfo.getId());
            orderProduct.setSkuId(cart.getSkuId());
            orderProduct.setProductName(cart.getProductName());
            orderProduct.setProductNumber(cart.getProductNumber());
            orderProduct.setProductImg(cart.getProductImg());

            //通过skuId获取sku对象，并设置价格
            ProductSku productSku = productSkuService.get(orderProduct.getSkuId());
            BigDecimal price = productSku.getPrice();
            orderProduct.setProductPrice(price);
            //计算订单商品明细中的商品小计
            BigDecimal totalPrice = price.multiply(new BigDecimal(cart.getProductNumber()));
            orderProduct.setTotalPrice(totalPrice);

            //把订单商品明细中的商品小计累加到订单总额中
            orderAmount = orderAmount.add(totalPrice);

            //插入该订单商品明细
            orderProductMapper.insert(orderProduct);

            //遍历sku属性集合，并设置到订单商品明细属性对象中，插入该订单商品明细属性数据
            for (ProductSkuProperty productSkuProperty : productSku.getProductSkuPropertyList()) {
                OrderProductProperty orderProductProperty = new OrderProductProperty();
                orderProductProperty.setOrderProductId(orderProduct.getId());
                orderProductProperty.setName(productSkuProperty.getSkuProperty().getName());
                orderProductProperty.setValue(productSkuProperty.getValue());
                orderProductPropertyMapper.insert(orderProductProperty);
            }
            //判断有无购物车id，如果有则删除购物车信息
            if (cart.getId() != null) {
                cartService.delete(cart.getId());
            }
        }
        //从新设置订单总额
        orderInfo.setOrderAmount(orderAmount);
        //更新该订单
        orderInfoMapper.updateByPrimaryKey(orderInfo);

        //判断是否需要开发票，如果要则,设置发票人和发票对应的订单，并插入发票信息
        Invoice invoice = vo.getInvoice();
        if (invoice != null) {
            invoice.setUserId(userLogin.getId());
            invoice.setOrderId(orderInfo.getId());
            invoiceMapper.insert(invoice);
        }

        //添加订单改变日志
        orderActionService.addOrderAction(orderInfo,"admin","后台","生成订单");

    }

    @Override
    public PageResult query(OrderQueryObject qo) {
        int count = orderInfoMapper.queryCount(qo);
        if(count == 0){
            return PageResult.empty();
        }
        List<OrderInfo> orderInfoList = orderInfoMapper.queryList(qo);

        return new PageResult(orderInfoList,count,qo.getCurrentPage(),qo.getPageSize());
    }

    @Override
    public void changeStatus(OrderStatusChangeVo vo) {
        //创建一个用户接收订单状态改变的vo对象，包含属性有订单id，
        //备注信息，订单修改的类型changeType（1为确认订单操作，2为发货，3为订单完成，4为售后）
        //获取订单对象
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(vo.getOrderId());
        //判断提交上来的订单修改类型
        switch (vo.getChangeType()){
            //如果是1的话，把订单的orderStatus改为1
            case 1:
                orderInfo.setOrderStatus(new Byte("1"));
                break;
            //如果是2的话，把订单的flowStatus改为1
            case 2:
                orderInfo.setFlowStatus(new Byte("1"));
                break;
            //如果是3的话，把订单的flowStatus改为4
            case 3:
                orderInfo.setFlowStatus(new Byte("4"));
                break;
            //如果是4的话，不需要修改订单状态
            case 4:
                System.out.println("订单完成");
                break;
            //如果是5的话，把订单的orderStatus改为2,订单的flowStatus改为2
            case 5:
                orderInfo.setOrderStatus(new Byte("2"));
                orderInfo.setFlowStatus(new Byte("2"));
                break;
            default:
                System.out.println("订单完成");
                break;
        }
        //更新该订单
        orderInfoMapper.updateByPrimaryKey(orderInfo);

        //添加订单改变日志
        orderActionService.addOrderAction(orderInfo,vo.getUserName(),vo.getPlace(),vo.getNote());

    }

    @Override
    public OrderInfo getById(Long id) {
        return orderInfoMapper.selectByPrimaryKey(id);
    }
}
