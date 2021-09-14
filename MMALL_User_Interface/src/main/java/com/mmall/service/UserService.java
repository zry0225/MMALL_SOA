package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;


/**
 * @author zhangruiyan
 */
public interface UserService {

    ServerResponse<User> login(String username, String password);
    int checkUserName(String username);

    int checkEmail(String registerMsg);

    int insert(User user);

    ServerResponse<User> selectAll();

    ServerResponse<String> getQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<PageInfo<User>> list();

    ServerResponse<String> resetPassword(String username,String passwordNew,String uuid);

    ServerResponse<String> resetLoginPassword(String passwordNew,int id);

    ServerResponse<String> updateInfo(User user);

    ServerResponse<User> getInfo(int id);

}
