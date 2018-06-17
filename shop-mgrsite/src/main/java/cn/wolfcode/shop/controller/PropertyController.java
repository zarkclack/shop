package cn.wolfcode.shop.controller;

import cn.wolfcode.shop.domain.Property;
import cn.wolfcode.shop.service.ICatalogService;
import cn.wolfcode.shop.service.IPropertyService;
import cn.wolfcode.shop.vo.JSONResultVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("property")
public class PropertyController {
    @Reference
    private IPropertyService propertyService;

    @Reference
    private ICatalogService catalogService;

    @RequestMapping(value = "",method = RequestMethod.GET)
    public String property(Model model){
        model.addAttribute("allCatalog",catalogService.getAllCatalogByCache());
        return "property/property";
    }

    @RequestMapping(value = "get/{catalogId}",method = RequestMethod.GET)
    public String getByCatalogId(@PathVariable("catalogId") Long catalogId, Model model){
        model.addAttribute("propertyList",propertyService.getByCatalogId(catalogId));
        return "property/property_list";
    }

    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public Object saveOrUpdate(Property property){
        JSONResultVo result = new JSONResultVo();
        try{
            propertyService.saveOrUpdate(property);
        }catch (Exception e){
            e.printStackTrace();
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "delete/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Object delete(@PathVariable("id") Long id){
        JSONResultVo result = new JSONResultVo();
        try{
            propertyService.delete(id);
        }catch (Exception e){
            e.printStackTrace();
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }



}
