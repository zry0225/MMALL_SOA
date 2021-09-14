package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @DubboReference
    private UserService userService;
    @PostMapping("register.do")
    public ServerResponse<String> register(User user){

        //判断账号或邮箱是否存在
        ServerResponse<String> checkName = inspection(user.getUsername(), Const.USERNAME);
        //如果查询到用户
        if (!checkName.isSuccess()){
            return checkName;
        }
        ServerResponse<String> checkEmail = inspection(user.getEmail(), Const.EMAIL);
        //如果查询到用户
        if (!checkEmail.isSuccess()){
            return checkEmail;
        }
        //能执行到这一步就代表账号、邮箱均未被注册，给予该用户普通用户权限
        user.setRole(Const.Role.ROLE_CUSTOMER);

        //将密码进行MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        //添加用户
        int cont = userService.insert(user);
        if (cont >0 ){
            return ServerResponse.createBySuccessMessage("效验成功");
        }
        return ServerResponse.createByErrorCodeMessage(1,"添加失败");
    }



    /**
     * 检验账号或邮箱是否被注册，已经被注册过的无法继续使用同样的账号或者邮箱
     * @param str 在页面输入的账号或者邮箱
     * @param type 存放在const中的数据
     * @return
     */
    @PostMapping("check_valid.do")
    public ServerResponse<String> inspection(String str,String type){

        /*在校验一个String类型的变量是否为空时，通常存在3中情况
        是否为 null
        是否为 ""
        是否为空字符串(引号中间有空格)  如： "     "。
        StringUtils的isBlank()方法可以一次性校验这三种情况，返回值都是true。*/
        if (!StringUtils.isBlank(type)){
            //计数器
            int cont = 0;
            //查看的是账号则进此分支
            if (Const.USERNAME.equals(type)){
               cont = userService.checkUserName(str);
               if (cont > 0){
                   return ServerResponse.createByErrorCodeMessage(1,"账号已存在");
               }
            }
            //查看的是邮箱则进此分支
            if (Const.EMAIL.equals(type)){
                cont = userService.checkEmail(str);
                if (cont > 0){
                    return ServerResponse.createByErrorCodeMessage(1,"邮箱已存在");
                }
            }
        }
        return ServerResponse.createBySuccessMessage("效验成功");
    }

    /**
     * 获取登陆用户信息
     * @param session 登陆后用户信息储存在session中
     * @return
     */
    @PostMapping("get_user_info.do")
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(1,"用户未登录，无法获取当前用户信息");
        }
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 忘记密码
     * @return 成功返回问题
     */
    @PostMapping("forget_get_question.do")
    public ServerResponse<String> getQuestion(String username){
        ServerResponse<String> response = userService.getQuestion(username);
        return response;
    }

    /**
     * 提交问题答案正确的话返回一个uuid
     * @param user
     * @return
     */

    @PostMapping("forget_check_answer.do")
    public ServerResponse<String> checkAnswer(@RequestBody User user){
        return userService.checkAnswer(user.getUsername(),user.getQuestion(),user.getAnswer());
    }

    /**
     * 忘记密码可重新更改密码
     * @param username
     * @param passwordNew
     * @param uuid
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(String username,String passwordNew,String uuid){
        if (org.apache.commons.lang3.StringUtils.isBlank(uuid)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        ServerResponse<String> inspection = this.inspection(username, Const.USERNAME);
        if (inspection.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        return userService.resetPassword(username, passwordNew, uuid);
    }

    /**
     * 登陆时更改密码
     * @param passwordOld
     * @param passwordNew
     * @param session
     * @return
     */
    @PostMapping("reset_password.do")
    public ServerResponse<String> resetLoginPassword(String passwordOld,String passwordNew,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        String oldPwd = MD5Util.MD5EncodeUtf8(passwordOld);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        LOGGER.debug("session中的user.password:{},页面输入的旧密码:{}",user.getPassword(),oldPwd);
        if (user.getPassword().equals(oldPwd)){
            //将新密码进行加密存到user中
            user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
            //将user存入session，相当于更新了session中的密码
            session.setAttribute(Const.CURRENT_USER,user);
            return userService.resetLoginPassword(passwordNew,user.getId());
        }
       return ServerResponse.createByErrorMessage("旧密码输入错误");


    }

    /**
     * 修改登录时个人信息
     * @param question
     * @param answer
     * @param email
     * @param phone
     * @param session
     * @return
     */
    @PostMapping("update_information.do")
    public ServerResponse<String> updateInfo(String email,String phone,String question,String answer,HttpSession session) {
        User loginUser =(User) session.getAttribute(Const.CURRENT_USER);
        if (loginUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        loginUser.setEmail(email);
        loginUser.setPhone(phone);
        loginUser.setQuestion(question);
        loginUser.setAnswer(answer);
        session.setAttribute(Const.CURRENT_USER,loginUser);
        return userService.updateInfo(loginUser);
    }

    /**
     * 登录时获得用户信息
     * @param session
     * @return
     */
    @PostMapping("get_information.do")
    public ServerResponse<User> getInfo(HttpSession session){
        User loginUser =(User) session.getAttribute(Const.CURRENT_USER);
        if (loginUser==null){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息，status=10强制登陆");
        }
        return userService.getInfo(loginUser.getId());
    }


    /**
     * 登出
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    public ServerResponse<String> logOut(HttpSession session){
/*        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user!=null){
            //清空登陆状态
            session.setAttribute(Const.CURRENT_USER,null);
            User userNew =(User) session.getAttribute(Const.CURRENT_USER);
            if (userNew!=null){
                return ServerResponse.createByErrorMessage("服务端异常");
            }
            return ServerResponse.createBySuccessMessage("退出成功");
        }
        return ServerResponse.createByErrorMessage("未登录");*/
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMessage("登出成功");
    }
}
