package cn.wolfcode.shop.vo;

import cn.wolfcode.shop.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luohaipeng on 2018/2/14.
 */
@Setter@Getter
public class GenerateSkuVo implements Serializable{

    private Product product;

    private List<SkuProperty> skuPropertyList;
    
    private List<SkuPropertyValue> skuPropertyValueList;

}
