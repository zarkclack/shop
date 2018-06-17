package cn.wolfcode.shop.vo;

import cn.wolfcode.shop.domain.Cart;
import cn.wolfcode.shop.domain.Invoice;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class OrderVo implements Serializable{
    private String token;
    private Long userAddressId;
    private List<Cart> cartList;
    private Invoice invoice;
    private PayVo payVo;
}
