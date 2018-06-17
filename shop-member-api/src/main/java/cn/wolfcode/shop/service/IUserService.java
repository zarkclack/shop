package cn.wolfcode.shop.service;

import cn.wolfcode.shop.domain.UserInfo;
import cn.wolfcode.shop.domain.UserLogin;

public interface IUserService {
    /**
     * 用户注册
     * @param userLogin
     * @return
     */
    UserLogin register(UserLogin userLogin);

    /**
     * 用户登录
     * @param userName
     * @param password
     * @return
     */
    String login(String userName, String password);

    /**
     * 用户退出登录
     * @param token
     */
    void logout(String token);


    UserInfo getById(Long id);

}
