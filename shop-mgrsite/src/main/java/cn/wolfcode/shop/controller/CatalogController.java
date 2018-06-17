package cn.wolfcode.shop.controller;

import cn.wolfcode.shop.domain.Catalog;
import cn.wolfcode.shop.service.ICatalogService;
import cn.wolfcode.shop.vo.JSONResultVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("catalog")
public class CatalogController {

    @Reference
    private ICatalogService catalogService;

    @ResponseBody
    @RequestMapping(value = "getAllCatalog",method = RequestMethod.GET)
    public List<Catalog> catalogList(){
        return catalogService.selectAll();
    }


    /**
     * 分类管理界面
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    public String catalog(Model model){
        //数据库查询所有
        //model.addAttribute("allCatalogJson", JSON.toJSONString(catalogService.selectAll()));
        //redis查询所有
        model.addAttribute("allCatalogJson", catalogService.getAllCatalogByCache());
        return "catalog/catalog";
    }

    /**
     * 通过分类的ID获取子分类
     */

    @RequestMapping(value = "getChildCatalog",method = RequestMethod.GET)
    public String getChildCatalog(Long catalogId, Map map){
        List<Catalog> catalogList = catalogService.getChildCatalog(catalogId);
        map.put("catalogList",catalogList);
        return "catalog/child_catalog";
    }

    /**
     * 拖曳排序分类
     */

    @RequestMapping(value = "updateSort",method = RequestMethod.POST)
    @ResponseBody
    public JSONResultVo updateSort(@RequestBody List<Long> ids){
        JSONResultVo vo = new JSONResultVo();

        try{
            catalogService.catalogSort(ids);
        }catch (Exception e){
            e.printStackTrace();
            vo.setErrorMsg(e.getMessage());
        }

        return vo;
    }

    /**
     * 新增和修改
     */
    @RequestMapping(value = "save",method = RequestMethod.POST)
    @ResponseBody
    public JSONResultVo saveOrUpdate(Catalog catalog){
        JSONResultVo vo = new JSONResultVo();

        try{
            catalogService.saveOrUpdate(catalog);
        }catch (Exception e){
            e.printStackTrace();
            vo.setErrorMsg(e.getMessage());
        }

        return vo;
    }

    /**
     * 删除
     */
    @RequestMapping(value = "delete",method = RequestMethod.GET)
    @ResponseBody
    public JSONResultVo delete(Long catalogId){
        JSONResultVo vo = new JSONResultVo();

        try{
            catalogService.delete(catalogId);
        }catch (Exception e){
            e.printStackTrace();
            vo.setErrorMsg(e.getMessage());
        }

        return vo;
    }

}
