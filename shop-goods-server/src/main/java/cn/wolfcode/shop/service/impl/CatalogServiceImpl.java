package cn.wolfcode.shop.service.impl;


import cn.wolfcode.shop.mapper.CatalogMapper;
import cn.wolfcode.shop.service.ICatalogService;
import cn.wolfcode.shop.domain.Catalog;
import cn.wolfcode.shop.util.RedisConstant;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class CatalogServiceImpl implements ICatalogService {
    @Autowired
    private CatalogMapper catalogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void delete(Long id) {
        //通过当前分类的id获取分类对象
        Catalog catalog = get(id);

        catalogMapper.deleteByPrimaryKey(id);

        Catalog parentCatalog = get(catalog.getPId());
        //判断当前删除的分类的父分类还有没有子分类
        if (getChildCatalog(parentCatalog.getId()).isEmpty()) {
            //如果没有,把当前删除的分类的父分类的isParent属性改为0
            parentCatalog.setIsParent(new Byte("0"));
            saveOrUpdate(parentCatalog);
        }

        //删除当前分类的子分类
        List<Catalog> childCatalog = getChildCatalog(id);
        if (!childCatalog.isEmpty()) {
            for (Catalog child : childCatalog) {
                catalogMapper.deleteByPrimaryKey(child.getId());
            }
        }

        refreshCache();
    }

    @Override
    public void catalogSort(List<Long> ids) {
        //遍历ids
        for (int i = 0; i < ids.size(); i++) {
            //分类的id获取分类对象
            Long id = ids.get(i);
            Catalog catalog = catalogMapper.selectByPrimaryKey(id);
            //更新分类的sort字段
            catalog.setSort(i+1);
            catalogMapper.updateByPrimaryKey(catalog);
        }
        refreshCache();
    }

    /**
     * 刷新缓存
     */
     private void refreshCache(){
         List<Catalog> catalogList = catalogMapper.selectAll();
         redisTemplate.opsForValue().set(RedisConstant.CATALOG_ALL,JSON.toJSONString(catalogList));
     }

    /**
     * 从缓存中获取所有分类
     * @return
     */
    @Override
    public String getAllCatalogByCache() {
        //从Redis中获取所有分类(json)
        String catalogAllJson = (String) redisTemplate.opsForValue().get(RedisConstant.CATALOG_ALL);
        //如果获取的数据是null,从数据库中重新查询出
        //所有的分类,并且缓存到Redis中
        if (catalogAllJson == null) {
            refreshCache();
        }
        return catalogAllJson;
    }

    @Override
    public List<Catalog> getChildCatalog(Long id) {
        List<Catalog> childCatalog = catalogMapper.getChildCatalog(id);
        childCatalog.forEach(catalog -> {
            //通过遍历的该分类去Redis中查询出该分类的属性个数统计
            Integer propertyCount = (Integer) redisTemplate.opsForValue().get(RedisConstant.CATALOG_PROPERTY_COUNT.replace("ID",catalog.getId().toString()));
            //通过遍历的该分类去Redis中查询出该分类的商品个数统计
            Integer productCount = (Integer) redisTemplate.opsForValue().get(RedisConstant.CATALOG_PRODUCT_COUNT.replace("ID",catalog.getId().toString()));
            //如果查询出的propertyCount是null,去数据库中统计,并且缓存到Redis中
            if (propertyCount == null) {
                propertyCount = catalogMapper.propertyCount(catalog.getId());
                redisTemplate.opsForValue().set(RedisConstant.CATALOG_PROPERTY_COUNT.replace("ID",catalog.getId().toString()),propertyCount);
            }
            //如果查询出的productCount是null,去数据库中统计,并且缓存到Redis中
            if (productCount == null) {
                productCount = catalogMapper.productCount(catalog.getId());
                redisTemplate.opsForValue().set(RedisConstant.CATALOG_PRODUCT_COUNT.replace("ID",catalog.getId().toString()),productCount);
            }
            //查询出后设置到该分类中的propertyCount属性中
            catalog.setPropertyCount(propertyCount);

            //查询出后设置到该分类中的productCount属性中
            catalog.setProductCount(productCount);
        });
        return childCatalog;
    }

    @Override
    public void saveOrUpdate(Catalog record) {
        //如果是新增分类,需要获取当前新增分类的父分类
        if (record.getId() == null) {
            //判断当前新增分类父分类的isParent属性
            Catalog catalog = get(record.getPId());
            //如果isParent属性是0,就给该属性更新为1
            if (catalog.getIsParent() == 0) {
                catalog.setIsParent(new Byte("1"));
                catalogMapper.updateByPrimaryKey(catalog);
            }
            catalogMapper.insert(record);
        }else {
            catalogMapper.updateByPrimaryKey(record);
        }
        refreshCache();
    }

    @Override
    public Catalog get(Long id) {
        return catalogMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Catalog> selectAll() {
        return catalogMapper.selectAll();
    }


}
