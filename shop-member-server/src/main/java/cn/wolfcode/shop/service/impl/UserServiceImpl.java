package cn.wolfcode.shop.service.impl;

import cn.wolfcode.shop.domain.UserInfo;
import cn.wolfcode.shop.domain.UserLogin;
import cn.wolfcode.shop.mapper.UserInfoMapper;
import cn.wolfcode.shop.mapper.UserLoginMapper;
import cn.wolfcode.shop.service.IUserService;
import cn.wolfcode.shop.util.MD5;
import cn.wolfcode.shop.util.RedisConstant;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public UserLogin register(UserLogin userLogin) {
        //判断注册的用户名是否存在
        if (isExists(userLogin.getUserName())){
            //如果存在,提示用户,该注册的用户名已经存在
            throw new RuntimeException("用户名已存在");
        }
        //否则,把userLogin和userInfo插入数据库
        userLogin.setState(new Byte("0"));
        userLogin.setPassword(MD5.encode(userLogin.getPassword()));

        userLoginMapper.insert(userLogin);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(userLogin.getId());
        userInfo.setRegistTime(new Date());
        userInfoMapper.insert(userInfo);
        return userLogin;
    }

    /**
     * 判断用户名是否被注册过,true是注册过
     * @param userName
     * @return
     */
    private boolean isExists(String userName) {
        if (userLoginMapper.isExists(userName) != null) {
            return true;
        }
        return false;
    }


    @Override
    public String login(String userName, String password) {
        //通过userName和password去数据库查出对应的用户
        UserLogin userLogin = userLoginMapper.login(userName,MD5.encode(password));
        //如果该用户不存在,告知用户用户名和密码不正确
        if (userLogin == null) {
            throw new RuntimeException("用户名和密码不正确");
        }

        //否则,创建出token,并且把数据库中查询到用户存到Redis中
        String token = createToken(userLogin);

        //返回token给客户端
        return token;
    }

    /**
     * 创建出token,并且把数据库中查询到用户存到Redis中
     * @param userLogin
     */
    private String createToken(UserLogin userLogin) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(RedisConstant.CURRENT_USER+token,userLogin);
        redisTemplate.expire(RedisConstant.CURRENT_USER+token,30, TimeUnit.DAYS);
        return token;
    }


    @Override
    public void logout(String token) {
        //判断token的有效性
        if (token == null || token.trim().length() == 0) {
            throw new RuntimeException("token异常");
        }

        //通过token去Redis查询出该token对应的用户
        Object userLogin = redisTemplate.opsForValue().get(RedisConstant.CURRENT_USER + token);

        //如果查询出的用户是null,说明登录超时或者没有登录
        if (userLogin == null) {
            throw new RuntimeException("登录超时");
        }else {
            //否则,把该token对应的Redis中value删除掉
            redisTemplate.delete(RedisConstant.CURRENT_USER + token);
        }

    }

    @Override
    public UserInfo getById(Long id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }
}
