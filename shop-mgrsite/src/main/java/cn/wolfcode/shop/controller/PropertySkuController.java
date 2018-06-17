package cn.wolfcode.shop.controller;

import cn.wolfcode.shop.domain.Product;
import cn.wolfcode.shop.domain.ProductSku;
import cn.wolfcode.shop.service.IProductService;
import cn.wolfcode.shop.service.IProductSkuService;
import cn.wolfcode.shop.service.ISkuPropertyService;
import cn.wolfcode.shop.service.ISkuPropertyValueService;
import cn.wolfcode.shop.vo.GenerateSkuVo;
import cn.wolfcode.shop.vo.JSONResultVo;
import cn.wolfcode.shop.vo.ProductVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("productSku")
public class PropertySkuController {
    @Reference
    private ISkuPropertyService propertyService;

    @Reference
    private ISkuPropertyValueService skuPropertyValueService;

    @Reference
    private IProductService productService;

    @Reference
    private IProductSkuService productSkuService;


    @RequestMapping(value = "",method = RequestMethod.GET)
    public String property(Model model,Long productId){
        Product product = productService.getProductById(productId);
        model.addAttribute("product",product);

        //判断商品是否生成过sku
        List<ProductSku> productSkuList = productSkuService.getSkuByProductId(productId);

        if (!productSkuList.isEmpty()) {
            ProductSku productSku = productSkuList.get(17);
            ArrayList<String> skuPropertyList = new ArrayList<>();
            productSku.getProductSkuPropertyList().forEach(skuPro -> {
                skuPropertyList.add(skuPro.getSkuProperty().getName());
            });
            model.addAttribute("skuList",productSkuList);
            model.addAttribute("skuPropertyList",skuPropertyList);
            return "product_sku/sku_list";
        }

        model.addAttribute("skuPropertyList",propertyService.getByCatalogId(product.getCatalog().getId()));
        return "product_sku/generate_sku";
    }

    @RequestMapping(value = "getSkuPropertyValue",method = RequestMethod.GET)
    public String getByCatalogId(Long skuPropertyId, Model model){
        model.addAttribute("skuProperty",propertyService.get(skuPropertyId));
        model.addAttribute("skuPropertyValueList",skuPropertyValueService.getByPropertyId(skuPropertyId));
        return "product_sku/sku_property_value_table";
    }

    @RequestMapping(value = "generateSku",method = RequestMethod.POST)
    public String generateSku(@RequestBody GenerateSkuVo generateSkuVo,Model model){
        model.addAttribute("skuPropertyList",generateSkuVo.getSkuPropertyList());
        model.addAttribute("skuList",productSkuService.generateSku(generateSkuVo));
        return "product_sku/sku_form";
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    @ResponseBody
    public Object save(ProductVo productVo){
        JSONResultVo result = new JSONResultVo();
        try{
            productSkuService.save(productVo);
        }catch (Exception e){
            e.printStackTrace();
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }



}
