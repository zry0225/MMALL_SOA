package com.mmall.service.impl;

import com.alibaba.druid.sql.visitor.functions.Concat;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.controller.ProductController;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.ProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FastDFSUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.midi.Soundbank;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author zhangruiyan
 */
@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;
    @Override
    public ServerResponse<PageInfo<Product>> show() {

        List<Product> list = productMapper.show();

        if (!list.isEmpty()){
            PageInfo<Product> productPageInfo = new PageInfo<>(list);
            return ServerResponse.createBySuccess(productPageInfo);
        }
        return ServerResponse.createByErrorCodeMessage(2,"产品为空");
    }

    /**
     * 产品查询
     * @return
     */
    @Override
    public ServerResponse<PageInfo<Product>> search(String productName,Integer productId) {
        List<Product> list = productMapper.search(productName,productId);
        if (!list.isEmpty()){
            PageInfo<Product> productPageInfo = new PageInfo<>(list);
            return ServerResponse.createBySuccess(productPageInfo);
        }
        return ServerResponse.createByErrorMessage("产品不存在");
    }

    /**
     * 上传文件
     * @param upload_file
     * @return
     */
    @Override
    public ServerResponse<String> upload(MultipartFile upload_file) {
        String url = FastDFSUtil.upload(upload_file);
        LOGGER.warn("这是上传文件到文件服务器上之后返回的链接:{}",url);

        return null;
    }

    /**
     * 产品详情
     * @param productId
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> detail(int productId) {
        ProductDetailVo ProductDetailVo = productMapper.detail(productId);
        ProductDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        if (ProductDetailVo==null){
            return ServerResponse.createByErrorCodeMessage(1,"该商品已下架或删除");
        }
        return ServerResponse.createBySuccess(ProductDetailVo);
    }

    /**
     * 修改产品状态
     * @param productId
     * @param status
     * @return
     */
    @Override
    public ServerResponse<String> setSaleStatue(int productId, int status) {
        int cont = productMapper.setSaleStatue(productId,status);
        if (cont>0){
            return ServerResponse.createBySuccessMessage("修改产品状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品状态失败");
    }

    /**
     * 添加商品
     * @param product
     * @return
     */
    @Override
    public ServerResponse<String> add(Product product) {
        if (product.getSubImages()!=null){
            String[] split = product.getSubImages().split(",");
            product.setMainImage(split[0]);

        }
        int cont = productMapper.add(product);
        if (cont>0){
            return ServerResponse.createBySuccessMessage("添加产品成功");
        }
        return ServerResponse.createByErrorMessage("更新商品失败");
    }

    /**
     * 修改商品
     * @param product
     * @return
     */
    @Override
    public ServerResponse<String> update(Product product) {
        if (product.getSubImages()!=null){
            String[] split = product.getSubImages().split(",");
            product.setMainImage(split[0]);

        }
        int cont = productMapper.updateByPrimaryKeySelective(product);
        if (cont>0){
            return ServerResponse.createBySuccessMessage("修改产品成功");
        }
        return ServerResponse.createByErrorMessage("更新商品失败");
    }


    /**
     * 产品搜索以及动态排序
     * @param categoryId
     * @param name
     * @return
     */
    @Override
    public ServerResponse<PageInfo<ProductListVo>> list(Integer categoryId, @Param("keyword") String  name ) {
        //先通过keyword和categoryId获得相对应的产品列表，然后再通过orderBy进行动态排序
        List<ProductListVo> products = productMapper.list(categoryId,name);
        for (ProductListVo product : products) {
            product.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }
        PageInfo<ProductListVo> list = new PageInfo<>(products);
        return ServerResponse.createBySuccess(list);
    }
}
