package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;

import java.util.List;

/**
 * @author zhangruiyan
 */
public interface CartService {
    ServerResponse<CartVo> list(Integer id);

    ServerResponse<CartVo> add(int productId, int count,int id);

    ServerResponse<CartVo> update(int productId, int count, Integer id);

    ServerResponse<CartVo> deleteProduct(int[] productIds,int userId);

    ServerResponse<CartVo> select(int productId, Integer id);

    ServerResponse<CartVo> unSelect(int productId, Integer id);

    ServerResponse<Integer> getCartProductCount(Integer id);

    ServerResponse<CartVo> selectAll(Integer id);

    ServerResponse<CartVo> unSelectAll(Integer id);
}
