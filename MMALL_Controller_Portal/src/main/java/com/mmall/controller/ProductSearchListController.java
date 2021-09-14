package com.mmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.service.ProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("product")
public class ProductSearchListController {

    @DubboReference
    private ProductService productService;

    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    public ServerResponse<PageInfo<ProductListVo>> list (@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                                         Integer categoryId, String keyword,
                                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){


        PageHelper.startPage(pageNum,pageSize);
        if (!StringUtils.isBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] s = orderBy.split("_");
                PageHelper.orderBy(s[0]+" "+s[1]);
            }
        }


        return productService.list(categoryId,keyword);
    }

    @RequestMapping("detail.do")
    public ServerResponse<ProductDetailVo> detail(int productId){
        return productService.detail(productId);
    }



}
