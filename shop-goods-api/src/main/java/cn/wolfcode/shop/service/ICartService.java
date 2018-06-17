package cn.wolfcode.shop.service;

public interface ICartService {
    /**
     * 添加商品到购物车
     */
    void add(String token,Long skuId,Integer productNumber);

    /**
     * 删除购物车中商品
     */
    void delete(Long id);
}
