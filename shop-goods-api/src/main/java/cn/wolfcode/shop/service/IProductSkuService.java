package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.ProductSku;
import cn.wolfcode.shop.vo.GenerateSkuVo;
import cn.wolfcode.shop.vo.ProductVo;

import java.util.List;
import java.util.Map;

public interface IProductSkuService {
    List<Map<String,Object>> generateSku(GenerateSkuVo vo);

    void save(ProductVo productVo);

    List<ProductSku> getSkuByProductId(Long productId);

    ProductSku get(Long id);
}
