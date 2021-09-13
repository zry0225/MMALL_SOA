package com.mmall.dao;

import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.vo.CartProductVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangruiyan
 */
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

   // Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> list(Integer id);

    Product haveProduct(Integer productId);

    int editQuantity(@Param("productId") int productId,@Param("count") int count,@Param("id") int id);

    int add(@Param("productId")int productId,@Param("count") int count, @Param("id")int id);

    int selectProductId(@Param("productId")int productId,@Param("id") int id);

    void deleteProduct(@Param("productId")int productId,@Param("userId") int userId);

    Cart select(@Param("productId")int productId,@Param("id") int id);

    Product selectProduct(Integer productId);

    Integer getCartProductCount(Integer id);

    int getListCount(Integer id);

    int uncheck(@Param("productId")int productId,@Param("id") int id);

    int check(@Param("productId")int productId,@Param("id") int id);


    void addCount(@Param("productId")int productId,@Param("count") int count, @Param("id")int id);

    List<Cart> selectCheckByUserId(Integer id);
}