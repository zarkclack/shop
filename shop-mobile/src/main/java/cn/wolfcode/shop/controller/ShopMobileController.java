package cn.wolfcode.shop.controller;

import cn.wolfcode.shop.domain.UserLogin;
import cn.wolfcode.shop.service.IUserService;
import cn.wolfcode.shop.vo.JSONResultVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users")
public class ShopMobileController {
    @Reference
    private IUserService userService;

    /**
     * 用户注册接口
     * 方法:post
     * url:/api/users
     * @param userLogin
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    public JSONResultVo register(UserLogin userLogin){
        JSONResultVo jsonResultVo = new JSONResultVo();
        try{
            UserLogin register = userService.register(userLogin);
            jsonResultVo.setResult(register);
        }catch (Exception e){
            e.printStackTrace();
            jsonResultVo.setErrorMsg(e.getMessage());
        }
        return jsonResultVo;
    }

    /**
     * 登录接口
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping(value = "/tokens",method = RequestMethod.POST)
    public JSONResultVo login(String userName,String password){
        JSONResultVo jsonResultVo = new JSONResultVo();
        try{
            String token = userService.login(userName,password);
            Map<String,String> tokenMap = new HashMap<>();
            tokenMap.put("token",token);
            jsonResultVo.setResult(tokenMap);
        }catch (Exception e){
            e.printStackTrace();
            jsonResultVo.setErrorMsg(e.getMessage());
        }
        return jsonResultVo;
    }


    /**
     * 用户退出登录
     * 方法:delete
     * url:/api/users
     * @param token
     * @return
     */
    /*@RequestMapping(value = "",method = RequestMethod.DELETE)
    public JSONResultVo logout(@RequestHeader("head_token") String token){
        JSONResultVo jsonResultVo = new JSONResultVo();
        try{
            userService.logout(token);
        }catch (Exception e){
            e.printStackTrace();
            jsonResultVo.setErrorMsg(e.getMessage());
        }
        return jsonResultVo;
    }*/

    /**
     * 用户退出登录
     * 方法:delete
     * url:/api/users
     * @param request
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.DELETE)
    public JSONResultVo logout(HttpServletRequest request){
        JSONResultVo jsonResultVo = new JSONResultVo();
        try{
            String token = request.getHeader("head_token");
            userService.logout(token);
        }catch (Exception e){
            e.printStackTrace();
            jsonResultVo.setErrorMsg(e.getMessage());
        }
        return jsonResultVo;
    }
}
