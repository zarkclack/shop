package cn.wolfcode.shop.controller;

import cn.wolfcode.shop.service.ICartService;
import cn.wolfcode.shop.vo.JSONResultVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/carts")
public class CartController {

    @Reference
    private ICartService cartService;

    /**
     * 添加商品到购物车接口
     * 方法:POST
     * url:/api/carts
     * @param token
     * @param skuId
     * @param productNumber
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    public Object add(HttpServletRequest request, Long skuId, Integer productNumber){
        JSONResultVo vo = new JSONResultVo();
        try{
            String token = request.getHeader("head_token");
            cartService.add(token,skuId,productNumber);
        }catch (Exception e){
            e.printStackTrace();
            vo.setErrorMsg(e.getMessage());
        }
        return vo;
    }
}
