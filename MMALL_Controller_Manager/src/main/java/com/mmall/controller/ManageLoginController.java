package com.mmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("manage/user")
public class ManageLoginController {
    @DubboReference
    private UserService userService;

    /**
     * 管理员登陆
     * @param password
     * @param username
     * @param session
     * @return
     */
    @PostMapping("login.do")
    public ServerResponse<User> login(String username,String password, HttpSession session){
        ServerResponse<User> response = userService.login(username,password);
        if (response.isSuccess()){
            if (response.getData().getRole()==1){
                //将管理员信息存储到session中
                session.setAttribute(Const.CURRENT_USER,response.getData());
            }else {
                return ServerResponse.createByErrorCodeMessage(1,"权限不足");
            }
        }
        return response;
    }


    /**
     * 用户列表
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @GetMapping("list.do")
    public ServerResponse<PageInfo<User>> list(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "5")Integer pageSize,HttpSession session){
        PageHelper.startPage(pageNum,pageSize);
        ServerResponse<PageInfo<User>> response = userService.list();
        User user =(User) session.getAttribute(Const.CURRENT_USER);

        if (response.isSuccess()){
            if (user == null){
                return ServerResponse.createByErrorCodeMessage(10,"用户未登陆，请登录");
            }else if (user.getRole() == 0){
                return ServerResponse.createByErrorCodeMessage(1,"没有权限");
            }
        }
        return response;
    }

}
