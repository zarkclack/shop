package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.domain.PropertyValue;
import cn.wolfcode.shop.mapper.PropertyValueMapper;
import cn.wolfcode.shop.service.IPropertyValueService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PropertyValueServiceImpl implements IPropertyValueService {

    @Autowired
    private PropertyValueMapper propertyValueMapper;

    @Override
    public void saveOrUpdate(List<PropertyValue> propertyValueArray) {
       //获取到集合中每一个属性值对象
        propertyValueArray.forEach(propertyValue -> {
            //判断该遍历对象值得主键id是否存在
            if (propertyValue.getId() == null) {
                //如果存在,执行更新的操作
                propertyValueMapper.insert(propertyValue);
            }else {
                //否,执行新增的操作
                propertyValueMapper.updateByPrimaryKey(propertyValue);
            }
        });
    }

    @Override
    public int delete(Long id) {
        return propertyValueMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PropertyValue get(Long id) {
        return propertyValueMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PropertyValue> getByPropertyId(Long propertyId) {
        return propertyValueMapper.getByPropertyId(propertyId);
    }

    @Override
    public List<PropertyValue> selectAll() {
        return propertyValueMapper.selectAll();
    }

}
