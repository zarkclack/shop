package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.domain.Cart;
import cn.wolfcode.shop.domain.Product;
import cn.wolfcode.shop.domain.UserLogin;
import cn.wolfcode.shop.mapper.CartMapper;
import cn.wolfcode.shop.mapper.ProductMapper;
import cn.wolfcode.shop.mapper.ProductSkuMapper;
import cn.wolfcode.shop.service.ICartService;
import cn.wolfcode.shop.util.RedisConstant;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartServiceImpl implements ICartService{

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Override
    public void delete(Long id) {
        cartMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void add(String token, Long skuId, Integer productNumber) {
        //通过token获取当前的登录用户
        if (token == null || token.trim().length() == 0) {
            throw new RuntimeException("登陆用户信息异常");
        }
        UserLogin userLogin = (UserLogin) redisTemplate.opsForValue().get(RedisConstant.CURRENT_USER + token);

        if (userLogin  == null) {
            throw new RuntimeException("登陆超时");
        }

        //判断当前添加到购物车的商品,之前是否添加过(通过userId和skuId去查询,如果查询到,就说明之前添加过该商品
        Cart cart = cartMapper.find(userLogin.getId(),skuId);

        //如果是之前添加过的,就更新该数据,在原有的基础上,加上当前购买的数量
        if (cart != null) {
            cart.setProductNumber(cart.getProductNumber() + productNumber);
            cartMapper.updateByPrimaryKey(cart);
        }else {
            //否则,插入一条新的购物车数据
            cart = new Cart();
            cart.setUserId(userLogin.getId());
            cart.setSkuId(skuId);
            cart.setProductNumber(productNumber);

            //到数据库查询商品名称和图片
            Product product = productMapper.selectByPrimaryKey(productSkuMapper.selectByPrimaryKey(skuId).getProductId());
            cart.setProductName(product.getName());
            cart.setProductImg(product.getImage());
            cartMapper.insert(cart);
        }
    }
}
