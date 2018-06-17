package cn.wolfcode.shop.controller;

import cn.wolfcode.shop.domain.SkuPropertyValue;
import cn.wolfcode.shop.service.ISkuPropertyService;
import cn.wolfcode.shop.service.ISkuPropertyValueService;
import cn.wolfcode.shop.vo.JSONResultVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("skuPropertyValue")
public class SkuPropertyValueController {

    @Reference
    private ISkuPropertyValueService propertyValueService;

    @Reference
    private ISkuPropertyService skuPropertyService;


    @RequestMapping(value = "get/{propertyId}",method = RequestMethod.GET)
    public String getByPropertyId(@PathVariable("propertyId") Long propertyId, Model model){
        model.addAttribute("propertyValueList",propertyValueService.getByPropertyId(propertyId));
        return "sku_property/sku_property_value_list";
    }

    @RequestMapping(value = "save",method = RequestMethod.POST,consumes="application/json")
    @ResponseBody
    public Object saveOrUpdate(@RequestBody List<SkuPropertyValue> propertyValueArray){
        JSONResultVo vo = new JSONResultVo();
        try{
            propertyValueService.saveOrUpdate(propertyValueArray);
        }catch (Exception e){
            e.printStackTrace();
            vo.setErrorMsg(e.getMessage());
        }

        return vo;
    }

    @RequestMapping(value = "delete/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Object delete(@PathVariable("id") Long id){
        JSONResultVo result = new JSONResultVo();
        try{
            propertyValueService.delete(id);
        }catch (Exception e){
            e.printStackTrace();
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }
}
