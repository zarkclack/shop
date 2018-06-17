package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.domain.Property;
import cn.wolfcode.shop.mapper.PropertyMapper;
import cn.wolfcode.shop.service.IPropertyService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PropertyServiceImpl implements IPropertyService{

    @Autowired
    private PropertyMapper propertyMapper;

    @Override
    public void saveOrUpdate(Property record) {
        if (record.getId() == null) {
            propertyMapper.insert(record);
        }else {
            propertyMapper.updateByPrimaryKey(record);
        }
    }

    @Override
    public int delete(Long id) {
        return propertyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Property> getByCatalogId(Long catalogId) {
        return propertyMapper.getByCatalogId(catalogId);
    }

    @Override
    public Property get(Long id) {
        return propertyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Property> selectAll() {
        return propertyMapper.selectAll();
    }
}
