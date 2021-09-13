package com.mmall.controller;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("manage/order")
public class manageOrderController {
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
    public ServerResponse<PageInfo<OrderVo>> search(Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                          @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                          HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.searchByOrderNo(orderNo,user.getId());

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
    @RequestMapping(value = "send_goods.do",method = RequestMethod.GET)
    public ServerResponse<String> sendGoods(Long orderNo,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.sendGoods(orderNo,user.getId());
    }
}
