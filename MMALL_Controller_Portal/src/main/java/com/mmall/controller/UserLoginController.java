package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("user")
public class UserLoginController {

    @DubboReference
    private UserService userService;
    @PostMapping("login.do")
    public ServerResponse<User> userLogin(String username, String password, HttpSession session){
        ServerResponse<User> response = userService.login(username, password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
            return ServerResponse.createBySuccess(response.getData());
        }
        return ServerResponse.createByErrorCodeMessage(1,"密码错误");
    }
}
