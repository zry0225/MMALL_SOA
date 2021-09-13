package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * @author zhangruiyan
 */
public interface ShoppingService {
    ServerResponse<Integer> add(Shipping shipping,int userId);

    ServerResponse<String> del(int shippingId, Integer id);

    ServerResponse<String> update(Shipping shipping, Integer id);

    ServerResponse<Shipping> select(int shippingId,int id);

    ServerResponse<PageInfo<Shipping>> list(int id);
}
