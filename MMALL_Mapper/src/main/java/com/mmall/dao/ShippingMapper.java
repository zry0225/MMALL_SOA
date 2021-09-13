package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

   // Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int add(@Param("shipping") Shipping shipping,@Param("userId") int userId);

    int selectId(@Param("shipping") Shipping shipping,@Param("userId") int userId);

    int del(@Param("shippingId") int shippingId,@Param("id") int id);


    Shipping select(@Param("shippingId") int shippingId,@Param("id") int id);

    List<Shipping> list(int id);

    int update(@Param("shipping") Shipping shipping,@Param("id") Integer id);
}