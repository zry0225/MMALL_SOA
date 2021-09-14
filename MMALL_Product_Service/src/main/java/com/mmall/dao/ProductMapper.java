package com.mmall.dao;

import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author zhangruiyan
 */
@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

   // Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> show();

    List<Product> search(@Param("productName") String productName, @Param("productId") Integer productId);

    ProductDetailVo detail(int productId);

    int setSaleStatue(@Param("productId") int productId,@Param("status") int status);

    int add(@Param("product") Product product);

    int update(@Param("product") Product product);

    List<ProductListVo> list(@Param("categoryId") Integer categoryId,
                             @Param("name") String name);

    Product selectProduct(Integer productId);

    int editStock(@Param("stock") Integer stock,@Param("id") Integer id);

    Product selecetProductOnSale(Integer productId);
}