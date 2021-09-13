package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import com.mmall.pojo.Product;
import com.mmall.vo.OrderItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    //OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> getByOrderNoUserId(@Param("orderNo") Long orderNo,@Param("userId") Integer userId);

    List<OrderItem> list(@Param("orderNo") Long orderNo, @Param("id") Integer id);

    OrderItem getCarOrderItemList(@Param("id") Integer id,@Param("orderNo") long orderNo,@Param("productId") Integer productId);

    int addOrderitem(@Param("id") Integer id, @Param("product") Product product, @Param("quantity") Integer quantity,@Param("orderNo")long orderNo);
}