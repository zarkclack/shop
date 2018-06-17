package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.domain.Catalog;
import cn.wolfcode.shop.domain.ProductSku;
import cn.wolfcode.shop.domain.SkuProperty;
import cn.wolfcode.shop.domain.SkuPropertyValue;
import cn.wolfcode.shop.mapper.CatalogMapper;
import cn.wolfcode.shop.mapper.ProductSkuMapper;
import cn.wolfcode.shop.mapper.ProductSkuPropertyMapper;
import cn.wolfcode.shop.service.IProductSkuService;
import cn.wolfcode.shop.vo.GenerateSkuVo;
import cn.wolfcode.shop.vo.ProductVo;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductSkuServiceImpl implements IProductSkuService{
    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private CatalogMapper catalogMapper;

    @Autowired
    private ProductSkuPropertyMapper productSkuPropertyMapper;


    @Override
    public ProductSku get(Long id) {
        return productSkuMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ProductSku> getSkuByProductId(Long productId) {
        return productSkuMapper.getSkuByProductId(productId);
    }

    @Override
    public void save(ProductVo productVo) {
        productVo.getProductSkuList().forEach(sku ->{
            sku.setProductId(productVo.getProduct().getId());
            productSkuMapper.insert(sku);
            sku.getProductSkuPropertyList().forEach(skuPro -> {
                skuPro.setProductSkuId(sku.getId());
                productSkuPropertyMapper.insert(skuPro);
            });
        });
    }

    @Override
    public List<Map<String, Object>> generateSku(GenerateSkuVo vo) {
        List<SkuProperty> skuPropertyList = vo.getSkuPropertyList();
        List<SkuPropertyValue> skuPropertyValueList = vo.getSkuPropertyValueList();
        //生成sku编码前缀
        List<Catalog> parentCatalog = catalogMapper.getParentCatalog(vo.getProduct().getCatalog().getId());
        String codePix = generateCode(parentCatalog) + vo.getProduct().getId();
        System.out.println(codePix);

        //用map对数据进行分类，key为属性的id，value为该属性下的属性值：Map<String,List<String>>
        Map<String,List<String>> mapList = getMapList(skuPropertyList,skuPropertyValueList);

        System.out.println(mapList);
        //把map数据转成List<List<String>>,准备递归
        List<List<String>> resiveList = new ArrayList<>();
        skuPropertyList.forEach(skuProperty -> {
            List<String> list = mapList.get(skuProperty.getId() + "");
            resiveList.add(list);
        });

        //进行递归算法,最后递归出来的结果为List<List<String>>
        List<List<String>> resultList = new ArrayList<>();

        resive(resiveList,resultList,0,new ArrayList<String>());


        //最后把list数据转成map到前台显示List<Map<String,Object>>
        List<Map<String,Object>> skuList = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            Map<String,Object> sku = new HashMap<>();
            sku.put("code",codePix);
            sku.put("price",vo.getProduct().getBasePrice());

            for (int y = 0; y < skuPropertyList.size(); y++) {
                sku.put(skuPropertyList.get(y).getId() + "",
                        resultList.get(i).get(y));
            }
            skuList.add(sku);
        }

        return skuList;
    }

    /**
     * 递归的方法
     * @param resiveList 用于递归的基础数据
     * @param resultList 最终的sku结果
     * @param layer 递归的层级
     * @param data  存放每个sku规格组合
     */
    private void resive(List<List<String>> resiveList, List<List<String>> resultList, int layer, List<String> data) {
        //判断递归的层级是否是最后一层,如果不是,就需要递归操作
        if (layer < resiveList.size() -1){
            List<String> list = resiveList.get(layer);
            list.forEach(val -> {
                List<String> temp = new ArrayList<>(data);
                temp.add(val);
                //递归
                resive(resiveList,resultList,layer + 1,temp);
            });
        } else {
            //否则,需要把sku规格组合添加到resultList中,结束当前此次递归
            List<String> list = resiveList.get(layer);
            list.forEach(val -> {
                List<String> temp = new ArrayList<>(data);
                temp.add(val);
                //把sku规格组合添加到resultList中
                resultList.add(temp);
            });
        }
    }

    private Map<String,List<String>> getMapList(List<SkuProperty> skuPropertyList, List<SkuPropertyValue> skuPropertyValueList) {
        Map<String,List<String>> mapList = new HashMap<>();

        //遍历skuPropertyList,给mapList设置元素,key为skuPropertyId,value先给一个空元素的List
        skuPropertyList.forEach(skuProperty -> {
            mapList.put(skuProperty.getId()+ "",new ArrayList<String>());
        });

        //遍历skuPropertyValueList,给mapList设置元素,key为skuPropertyId,value为SkuPropertyValue的value
        skuPropertyValueList.forEach(skuPropertyValue -> {
            List<String> list = mapList.get(skuPropertyValue.getSkuPropertyId() + "");
            list.add(skuPropertyValue.getValue());
        });

        return mapList;
    }

    /**
     * 生成sku编码前缀
     */
    private String generateCode(List<Catalog> parentCatalog){
        StringBuilder sb = new StringBuilder();
        parentCatalog.remove(0);
        for (int i = 0; i < parentCatalog.size(); i++) {
            //获取遍历出来的分类
            Catalog catalog = parentCatalog.get(i);
            //判断该分类是否为一级分类,如果是一级分类,获取的是分类的编码前两位数
            //否则,获取该分类的前两位顺序
            if (i == 0) {
                sb.append(catalog.getCode().length() > 2 ?
                catalog.getCode().substring(0,2) : catalog.getCode());
            }else {
                sb.append(catalog.getSort() > 9 ? (catalog.getSort()+"")
                        .substring(0,2) : catalog.getSort() + "");
            }
        }
        return sb.toString();
    }
}
