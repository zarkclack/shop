package cn.wolfcode.shop.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cart extends BaseDomain{
    private Long userId;

    private Long skuId;

    private String productName;

    private Integer productNumber;

    private String productImg;
}