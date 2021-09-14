package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Cart;
import com.mmall.pojo.User;
import com.mmall.service.CartService;
import com.mmall.service.CategoryService;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("cart")
public class CartController {
    @DubboReference
    private CartService cartService;

    /**
     * 购物车list
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    public ServerResponse<CartVo> list(HttpSession session) {
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(10, "用户未登录，请登录");
        }
        return cartService.list(user.getId());
    }

    /**
     * 购物车添加商品
     * @param productId
     * @param count
     * @param session
     * @return
     */
    @RequestMapping(value = "add.do", method = RequestMethod.GET)
    public ServerResponse<CartVo> add(int productId, int count, HttpSession session) {
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return cartService.add(productId,count,user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(10, "用户未登录，请登录");
    }

    /**
     * 修改商品个数
     * @param productId
     * @param count
     * @param session
     * @return
     */
    @RequestMapping("update.do")
    public ServerResponse<CartVo> update(int productId, int count, HttpSession session){
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return cartService.update(productId,count,user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(10, "用户未登录，请登录");
    }

    /**
     * 删除购物车中产品
     * @param productIds
     * @param session
     * @return
     */
    @RequestMapping(value = "delete_product.do",method = RequestMethod.GET)
    public ServerResponse<CartVo> deleteProduct(int[] productIds,HttpSession session){
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return cartService.deleteProduct(productIds,user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(10, "用户未登录，请登录");
    }


    /**
     * 选取购物车某个商品
     * @param productId
     * @param session
     * @return
     */

    @RequestMapping(value = "select.do",method = RequestMethod.GET)
    public ServerResponse<CartVo> select(int productId,HttpSession session){
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return cartService.select(productId,user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(10, "用户未登录，请登录");
    }


    /**
     * 反选
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping(value = "un_select.do",method = RequestMethod.GET)
    public ServerResponse<CartVo> unSelect(int productId,HttpSession session){
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return cartService.unSelect(productId,user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(10, "用户未登录，请登录");
    }

    /**
     * 获得购物车产品总和
     * @param session
     * @return
     */
    @RequestMapping(value = "get_cart_product_count.do",method = RequestMethod.GET)
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return cartService.getCartProductCount(user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(0, "用户未登录，请登录");

    }

    /**
     * 购物车全选
     * @param session
     * @return
     */
    @RequestMapping("select_all.do")
    public ServerResponse<CartVo> selectAll(HttpSession session){
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return cartService.selectAll(user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(10, "用户未登录，请登录");
    }

    /**
     * 购物车全不选
     * @param session
     * @return
     */
    @RequestMapping(value = "un_select_all.do",method = RequestMethod.GET)
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        //判断登陆
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return cartService.unSelectAll(user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(10, "用户未登录，请登录");
    }




}
