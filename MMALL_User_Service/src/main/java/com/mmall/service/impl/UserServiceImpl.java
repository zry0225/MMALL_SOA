package com.mmall.service.impl;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author zhangruiyan
 */
@DubboService
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;


    /**
     * 登陆方法
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        //通过用户名查询用户
        int count = userMapper.checkName(username);
        if (count == 0) {
            return ServerResponse.createByErrorCodeMessage(1,"用户名错误");
        }
        //用户名存在后查询密码，将用户输入的密码进行MD5加密后进入数据库进行查询
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.login(username, password);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(1,"密码错误");
        }
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 检查账号
     * @param username 账户名
     * @return 返回计数器，大于零说明查到了用户
     */
    @Override
    public int checkUserName(String username) {
        return userMapper.checkName(username);
    }

    /**
     * 检查邮箱
     * @param email 邮箱
     * @return 返回计数器，若计数器大于零则查询到用户
     */
    @Override
    public int checkEmail(String email) {
        return userMapper.checkEmail(email);
    }

    /**
     * 添加用户
     * @param user 页面输入的用户对象
     * @return 返回计数器
     */
    @Override
    public int insert(User user) {
        return userMapper.insertSelective(user);
    }

    /**
     *
     * @return
     */
    @Override
    public ServerResponse<User> selectAll() {
        return null;
    }

    /**
     * 忘记密码，根据账号找到问题
     * @return 问题
     */
    @Override
    public ServerResponse<String> getQuestion(String username) {
        String  question = userMapper.getQuestion(username);
        if (question == "" || question.equals("")){
            return ServerResponse.createByErrorCodeMessage(1,"该用户未设置找回密码问题");
        }
        return ServerResponse.createBySuccess(question);
    }

    /**
     * 提交问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        String realAnswer = userMapper.checkAnswer(username,question,answer);
        if (realAnswer!=null){
            //生成随机uuid
            String uuid = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,uuid);
            return ServerResponse.createBySuccess(uuid);
        }
        return ServerResponse.createByErrorCodeMessage(1,"问题答案错误");
    }

    /**
     * 用户列表
     * @return List<User>
     */
    @Override
    public ServerResponse<PageInfo<User>> list() {

        List<User> list =  userMapper.list();
        PageInfo<User> userPageInfo = new PageInfo<>(list);
        if (list!=null){
            return ServerResponse.createBySuccess(userPageInfo);
        }
        return ServerResponse.createByError();
    }

    /**
     * 重设密码
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String username, String passwordNew, String uuid) {
        String key = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(key)){
            return ServerResponse.createByErrorMessage("token失效");
        }
        if (uuid.equals(key)){
            passwordNew = MD5Util.MD5EncodeUtf8(passwordNew);
            int cont = userMapper.resetPassword(username,passwordNew);
            if (cont>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误");
        }
        return ServerResponse.createByError();
    }

    /**
     * 登陆中修改密码
     * @return
     */
    @Override
    public ServerResponse<String> resetLoginPassword(String passwordNew,int id) {
        passwordNew = MD5Util.MD5EncodeUtf8(passwordNew);
        int cont = userMapper.resetLoginPassword(passwordNew,id);
        if (cont!=0){
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登陆状态更改个人信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> updateInfo(User user) {
        //username不能更改，邮箱需要效验，不能和旧邮箱一样
        int cont = checkEmail(user.getEmail());
        LOGGER.debug("返回值不为0则邮箱存在：{}",cont);
        if (cont>0){
            return ServerResponse.createByErrorMessage("邮箱已存在，请修改");
        }
        int i = userMapper.updateInfo(user);
        if (i==0){
            return ServerResponse.createByErrorMessage("修改失败");
        }
        return ServerResponse.createBySuccessMessage("更新个人信息成功");
    }

    @Override
    public ServerResponse<User> getInfo(int id) {
        User user = userMapper.getInfo(id);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        return ServerResponse.createBySuccess(user);
    }

}
