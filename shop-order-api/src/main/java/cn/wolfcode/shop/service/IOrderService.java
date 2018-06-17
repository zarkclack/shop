package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.OrderInfo;
import cn.wolfcode.shop.qo.OrderQueryObject;
import cn.wolfcode.shop.qo.PageResult;
import cn.wolfcode.shop.vo.OrderStatusChangeVo;
import cn.wolfcode.shop.vo.OrderVo;

public interface IOrderService {

    /**
     * 生成订单
     * @param vo
     */
    void generateOrder(OrderVo vo);

    PageResult query(OrderQueryObject qo);

    OrderInfo getById(Long id);

    /**
     * 修改订单状态
     * @param vo
     */
    void changeStatus(OrderStatusChangeVo vo);
}
