package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhangruiyan
 */
public interface ProductService {
    /**
     * 查看所有产品
     * @return
     */
    ServerResponse<PageInfo<Product>> show();

    ServerResponse<PageInfo<Product>> search(String productName,Integer productId);

    ServerResponse<String> upload(MultipartFile upload_file);

    ServerResponse<ProductDetailVo> detail(int productId);

    ServerResponse<String> setSaleStatue(int productId, int status);

    ServerResponse<String> add(@Param("Product") Product product);

    ServerResponse<String> update(@Param("Product") Product product);

    ServerResponse<PageInfo<ProductListVo>> list(Integer categoryId, String keyword );
}
