package com.mmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.ShoppingService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("shipping")
public class ShoppingController {
    @Autowired
    private ShoppingService shoppingService;

    /**
     * 添加地址
     * @param shipping
     * @param session
     * @return
     */
    @RequestMapping(value = "add.do",method = RequestMethod.GET)
    public ServerResponse<Integer> add(Shipping shipping, HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        return shoppingService.add(shipping,user.getId());
    }


    /**
     * 删除地址
     * @param shippingId
     * @param session
     * @return
     */
    @RequestMapping(value = "del.do",method = RequestMethod.GET)
    public ServerResponse<String> del(int shippingId,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        return shoppingService.del(shippingId,user.getId());
    }


    /**
     * 在线修改地址
     * @param shipping
     * @param session
     * @return
     */
    @RequestMapping(value = "update.do",method = RequestMethod.GET)
    public ServerResponse<String> update(Shipping shipping,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        return shoppingService.update(shipping,user.getId());
    }

    /**
     * 选中查看具体的地址
     * @param shippingId
     * @param session
     * @return
     */
    @RequestMapping(value = "select.do",method = RequestMethod.GET)
    public ServerResponse<Shipping> select(int shippingId, HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(1,"请登录后查询");
        }
        return shoppingService.select(shippingId,user.getId());
    }


    /**
     * 获得地址列表
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping("list.do")
    public ServerResponse<PageInfo<Shipping>> list(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(1,"请登录之后查看");
        }
        PageHelper.startPage(pageNum,pageSize);
        return shoppingService.list(user.getId());
    }





}
