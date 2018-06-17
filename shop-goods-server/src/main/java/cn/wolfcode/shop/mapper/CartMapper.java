package cn.wolfcode.shop.mapper;

import cn.wolfcode.shop.domain.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Cart record);

    Cart selectByPrimaryKey(Long id);

    List<Cart> selectAll();

    int updateByPrimaryKey(Cart record);

    Cart find(@Param("userId") Long userId, @Param("skuId") Long skuId);
}