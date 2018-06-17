package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.SkuPropertyValue;

import java.util.List;

public interface ISkuPropertyValueService {
    void saveOrUpdate(List<SkuPropertyValue> propertyValueArray);

    int delete(Long id);

    SkuPropertyValue get(Long id);

    List<SkuPropertyValue> selectAll();

    List<SkuPropertyValue> getByPropertyId(Long propertyId);
}
