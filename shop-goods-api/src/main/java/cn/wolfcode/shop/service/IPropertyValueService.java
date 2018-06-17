package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.Property;
import cn.wolfcode.shop.domain.PropertyValue;

import java.util.List;

public interface IPropertyValueService {
    void saveOrUpdate(List<PropertyValue> propertyValueArray);

    int delete(Long id);

    PropertyValue get(Long id);

    List<PropertyValue> selectAll();

    List<PropertyValue> getByPropertyId(Long propertyId);
}
