package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.domain.SkuPropertyValue;
import cn.wolfcode.shop.mapper.SkuPropertyValueMapper;
import cn.wolfcode.shop.service.ISkuPropertyValueService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SkuPropertyValueServiceImpl implements ISkuPropertyValueService{

    @Autowired
    private SkuPropertyValueMapper skuPropertyValueMapper;


    @Override
    public void saveOrUpdate(List<SkuPropertyValue> propertyValueArray) {
        //获取到集合中每一个属性值对象
        propertyValueArray.forEach(propertyValue -> {
            //判断该遍历对象值得主键id是否存在
            if (propertyValue.getId() == null) {
                //如果存在,执行更新的操作
                skuPropertyValueMapper.insert(propertyValue);
            }else {
                //否,执行新增的操作
                skuPropertyValueMapper.updateByPrimaryKey(propertyValue);
            }
        });
    }

    @Override
    public int delete(Long id) {
        return skuPropertyValueMapper.deleteByPrimaryKey(id);
    }

    @Override
    public SkuPropertyValue get(Long id) {
        return skuPropertyValueMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SkuPropertyValue> getByPropertyId(Long propertyId) {
        return skuPropertyValueMapper.getByPropertyId(propertyId);
    }

    @Override
    public List<SkuPropertyValue> selectAll() {
        return skuPropertyValueMapper.selectAll();
    }
}
