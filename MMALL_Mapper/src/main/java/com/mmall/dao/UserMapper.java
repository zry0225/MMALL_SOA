package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkName(String username);

    User login(@Param("username") String username,@Param("password") String password);

    int checkEmail(String email);

    String getQuestion(String username);

    String checkAnswer(@Param("username") String username,
                       @Param("question")String question,
                       @Param("answer") String answer);

    List<User> list();

    int resetPassword(@Param("username") String username,@Param("passwordNew") String passwordNew);

    int resetLoginPassword(@Param("passwordNew") String passwordNew,@Param("id") int id);

    int updateInfo(@Param("user") User user);

    User getInfo(int id);
}