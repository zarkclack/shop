package cn.wolfcode.shop.controller;

import cn.wolfcode.shop.activemq.GenerateOrderProducer;
import cn.wolfcode.shop.domain.UserLogin;
import cn.wolfcode.shop.service.IOrderService;
import cn.wolfcode.shop.util.RedisConstant;
import cn.wolfcode.shop.vo.JSONResultVo;
import cn.wolfcode.shop.vo.OrderStatusChangeVo;
import cn.wolfcode.shop.vo.OrderVo;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    @Reference
    private IOrderService orderService;

    @Autowired
    private GenerateOrderProducer generateOrderProducer;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 生成订单接口
     * 方法:POST
     * url:/api/orders
     * @param orderVo
     * @param request
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    public Object generateOrder(@RequestBody OrderVo orderVo, HttpServletRequest request){
        JSONResultVo result = new JSONResultVo();
        try{
            String token = request.getHeader("head_token");
            orderVo.setToken(token);
            //orderService.generateOrder(orderVo);
            generateOrderProducer.sendMessage(JSON.toJSONString(orderVo));
            result.setResult("生成订单成功");
        }catch (Exception e){
            e.printStackTrace();
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 修改订单状态接口
     * 方法:PATCH
     * url:/api/orders
     * @param vo
     * @param request
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.PATCH)
    public Object changeOrderStatus(@RequestBody OrderStatusChangeVo vo, HttpServletRequest request){
        String token = request.getHeader("head_token");
        //通过token获取当前登录用户
        if (token == null || token.trim().length() == 0) {
            throw new RuntimeException("登陆用户信息异常");
        }
        UserLogin userLogin = (UserLogin) redisTemplate.opsForValue().get(RedisConstant.CURRENT_USER + token);
        if (userLogin  == null) {
            throw new RuntimeException("登陆超时");
        }

        JSONResultVo result = new JSONResultVo();
        try{
            vo.setUserName(userLogin.getUserName());
            vo.setPlace("前台");
            orderService.changeStatus(vo);
        }catch (Exception e){
            e.printStackTrace();
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }

}
