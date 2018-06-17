package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.domain.SkuProperty;
import cn.wolfcode.shop.mapper.ProductSkuMapper;
import cn.wolfcode.shop.mapper.SkuPropertyMapper;
import cn.wolfcode.shop.service.ISkuPropertyService;
import cn.wolfcode.shop.vo.ProductVo;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SkuPropertyServiceImpl implements ISkuPropertyService{

    @Autowired
    private SkuPropertyMapper skuPropertyMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Override
    public void saveOrUpdate(SkuProperty record) {
        if (record.getId() == null) {
            skuPropertyMapper.insert(record);
        }else {
            skuPropertyMapper.updateByPrimaryKey(record);
        }
    }

    @Override
    public int delete(Long id) {
        return skuPropertyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<SkuProperty> getByCatalogId(Long catalogId) {
        return skuPropertyMapper.getByCatalogId(catalogId);
    }


    @Override
    public SkuProperty get(Long id) {
        return skuPropertyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SkuProperty> selectAll() {
        return skuPropertyMapper.selectAll();
    }
}
