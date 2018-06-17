package cn.wolfcode.shop.mapper;

import cn.wolfcode.shop.domain.UserLogin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserLoginMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserLogin record);

    UserLogin selectByPrimaryKey(Long id);

    List<UserLogin> selectAll();

    int updateByPrimaryKey(UserLogin record);

    Long isExists(String userName);

    UserLogin login(@Param("userName") String userName, @Param("password") String password);
}