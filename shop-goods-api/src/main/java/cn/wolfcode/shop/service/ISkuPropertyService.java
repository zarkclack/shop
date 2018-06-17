package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.SkuProperty;
import cn.wolfcode.shop.vo.GenerateSkuVo;
import cn.wolfcode.shop.vo.ProductVo;

import java.util.List;

public interface ISkuPropertyService {
    void saveOrUpdate(SkuProperty record);

    int delete(Long id);

    SkuProperty get(Long id);

    List<SkuProperty> selectAll();

    List<SkuProperty> getByCatalogId(Long catalogId);
}
