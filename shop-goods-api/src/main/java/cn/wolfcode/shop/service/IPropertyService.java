package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.Property;

import java.util.List;

public interface IPropertyService {
    void saveOrUpdate(Property record);

    int delete(Long id);

    Property get(Long id);

    List<Property> selectAll();

    List<Property> getByCatalogId(Long catalogId);
}
