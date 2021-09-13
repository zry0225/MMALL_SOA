package com.mmall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 产品列表
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    public ServerResponse<PageInfo<OrderVo>> list(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                                  @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                                  HttpSession session){

        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (user.getRole()==0){
            return ServerResponse.createByErrorCodeMessage(1,"没有权限");
        }

        return orderService.list(user.getId());
    }

    /**
     * 根据订单号查询
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @param session
     * @return
     */
    @RequestMapping(value = "search.do",method = RequestMethod.GET)
    public ServerResponse<OrderVo> search(Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                                    HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.search(orderNo,user.getId());

    }

    /**
     * 订单详情
     * @param orderNo
     * @param session
     * @return
     */
    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    public ServerResponse<OrderVo> detail(Long orderNo, HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.search(orderNo,user.getId());
    }

    /**
     * 发货
     * @param orderNo
     * @param session
     * @return
     */
   /* @RequestMapping(value = "send_goods.do",method = RequestMethod.GET)
    public ServerResponse<String> sendGoods(Long orderNo,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.sendGoods(orderNo,user.getId());
    }*/


    /**
     * 创建订单
     * @param shippingId
     * @param session
     * @return
     */
    @RequestMapping(value = "create.do",method = RequestMethod.GET)
    public ServerResponse<OrderVo> create(int shippingId, HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.create(shippingId,user.getId());
    }


    /**
     * 订单商品信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_order_cart_product.do",method = RequestMethod.GET)
    public ServerResponse<OrderProductVo> getOrderCartProduct(HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping(value = "cancel.do",method = RequestMethod.GET)
    public ServerResponse<String> cancel(Long orderNo,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.cancel(orderNo,user.getId());
    }





    /**
     * 支付
     * @param session
     * @param orderNo
     * @return
     */
    /*@RequestMapping(value = "pay.do",method = RequestMethod.GET)
    public ServerResponse pay(HttpSession session, Long orderNo){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
         String path = session.getServletContext().getRealPath("/");
        return orderService.pay(orderNo,user.getId(),path);

    }*/

    @RequestMapping("pay.do")
    public ServerResponse pay(HttpSession session,Long orderNo,HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return orderService.pay(orderNo,user.getId(),path);
    }

    /**
     * 支付宝回调
     * @param req
     * @return
     */
    @RequestMapping(value = "alipay_callback.do",method = RequestMethod.POST)
    public String alipay_callback(HttpServletRequest req){
        Map<String,String> map = Maps.newHashMap();
        Map<String,String[]> map2 = req.getParameterMap();
        for (Iterator<String> i = map2.keySet().iterator(); i.hasNext();) {
            String key = i.next();
            String[] strings = map2.get(key);
            String value = Arrays.toString(strings).replace("[", "").replace("]", "");
            map.put(key,value);

        }
        map.remove("sign_type");
        boolean b = false;
        try {
            b = AlipaySignature.rsaCheckV2(map, Configs.getPublicKey(), "utf-8");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (!b){
            ServerResponse.createByErrorMessage("非支付宝请求");
        }
        ServerResponse response = orderService.aliCallback(map);
        if (response.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;


    }


    /**
     * 查询订单支付状态
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "query_order_pay_status.do")
    public ServerResponse<Boolean> queryOrderPay(HttpSession session, Long orderNo){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = orderService.queryOrderPayStatus(user.getId(),orderNo);
        if (response.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }



}
