package com.mmall.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.ProductService;
import com.mmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("manage")
public class ManageController {
    @Autowired
    private UserService userService;

    /**
     * 获得用户列表
     * @return
     */
    /*@GetMapping("/user/list.do")
    public ServerResponse< PageInfo<User>> userList(@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                     @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum){
        PageHelper.startPage(pageNum, pageSize);
        ServerResponse<User> list =  userService.selectAll();
        PageInfo<User> userPageInfo = new PageInfo<>();
        System.out.println(userPageInfo);
        return ServerResponse.createBySuccess(userPageInfo);

    }*/


}
