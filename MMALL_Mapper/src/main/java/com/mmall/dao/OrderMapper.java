package com.mmall.dao;

import com.mmall.pojo.Order;
import com.mmall.vo.ShippingVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    //Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId,@Param("orderNo") Long orderNo);

    Order selectByOrderNo(long id);

    List<Order> list(Integer id);

    ShippingVo getShipping(Integer shippingId);

    Order search(@Param("orderNo") Long orderNo,@Param("id") Integer id);

    int sendGoods(@Param("order") Order order);

    int create(@Param("id")int id,@Param("price") BigDecimal price,@Param("orderNo") long orderNo, @Param("shippingId") int shippingId);

    List<Order> listAll();

}