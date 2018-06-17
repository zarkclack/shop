package cn.wolfcode.shop.controller;

import cn.wolfcode.shop.domain.OrderAction;
import cn.wolfcode.shop.domain.OrderInfo;
import cn.wolfcode.shop.domain.UserInfo;
import cn.wolfcode.shop.qo.OrderQueryObject;
import cn.wolfcode.shop.qo.PageResult;
import cn.wolfcode.shop.service.IOrderActionService;
import cn.wolfcode.shop.service.IOrderService;
import cn.wolfcode.shop.service.IUserService;
import cn.wolfcode.shop.vo.JSONResultVo;
import cn.wolfcode.shop.vo.OrderStatusChangeVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by luohaipeng on 2018/3/23.
 */
@Controller
@RequestMapping(value = "/order")
public class OrderInfoController {

    @Reference
    IOrderService orderInfoService;
    @Reference
    IUserService userInfoService;
    @Reference
    IOrderActionService orderActionService;

    @RequestMapping(value = "",method = RequestMethod.GET)
    public String orderInfo(){

        return "order/order_info";
    }

    @RequestMapping(value = "/page",method = RequestMethod.GET)
    public String page(Map map, OrderQueryObject qo){
        PageResult page = orderInfoService.query(qo);
        map.put("page",page);
        return "order/order_list";
    }

    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    public String detail(Long id,Map map){
        OrderInfo orderInfo = orderInfoService.getById(id);
        UserInfo userInfo = userInfoService.getById(orderInfo.getUserId());
        List<OrderAction> orderActionList =  orderActionService.getOrderAction(id);
        map.put("userInfo",userInfo);
        map.put("orderInfo",orderInfo);
        map.put("orderActionList",orderActionList);
        return "order/order_detail";
    }

    @RequestMapping(value = "/changeStatus",method = RequestMethod.POST)
    @ResponseBody
    public Object changeStatus(OrderStatusChangeVo vo){
        JSONResultVo resultVo = new JSONResultVo();
        try{
            vo.setUserName("admin");
            vo.setPlace("后台");
            orderInfoService.changeStatus(vo);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setErrorMsg(e.getMessage());
        }
        return resultVo;
    }
    
}






















