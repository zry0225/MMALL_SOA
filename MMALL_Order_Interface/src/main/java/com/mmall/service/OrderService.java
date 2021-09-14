package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * @author zhangruiyan
 */
public interface OrderService {
    /**
     * 支付
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    public ServerResponse pay(Long orderNo,Integer userId,String path);

    /**
     * 处理异步通知回调函数
     * @param params
     * @return
     */
    public ServerResponse aliCallback(Map<String,String> params);

    /**
     * 查询订单支付状态
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse<PageInfo<OrderVo>> list(Integer id);

    ServerResponse<OrderVo> search(Long orderNo, Integer id);

    ServerResponse<String> sendGoods(Long orderNo, Integer id);

    ServerResponse<OrderVo> create(int shippingId, Integer id);

    ServerResponse<OrderProductVo> getOrderCartProduct(Integer id);

    ServerResponse<String> cancel(Long orderNo, Integer id);

    ServerResponse<PageInfo<OrderVo>> searchByOrderNo(Long orderNo, Integer id);
}
