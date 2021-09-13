package com.mmall.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangruiyan
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 通过父类id获得同级子类
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getCategory(int categoryId) {
        List<Category> list = categoryMapper.getCategory(categoryId);
        LOGGER.debug("根据父节点从数据库中获得到的同级子品类：{}",list.get(1).toString());
        if (!list.isEmpty()){
            return ServerResponse.createBySuccess(list);
        }
        return ServerResponse.createByErrorCodeMessage(1,"未找到该品类");
    }


    @Override
    public ServerResponse<String> addCategory(Integer parentId, String categoryName) {
        int cont = categoryMapper.addCategory(parentId,categoryName);
        if (cont>0 && categoryName!=null){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    /**
     * 更新品类名
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse<String> setCategoryName(Integer categoryId, String categoryName) {
        if (categoryId != null || categoryName != null){
            int cont =  categoryMapper.setCategoryName(categoryId,categoryName);
            if (cont > 0){
                return ServerResponse.createBySuccessMessage("更新品类名字成功");
            }
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    /**
     * 获取当前分类id以及递归子节点id
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<String>> getDeepCategory(Integer categoryId) {
        if (categoryId != null){
            List<String> list =categoryMapper.getDeepCategory(categoryId);
            if (!list.isEmpty()){
                return ServerResponse.createBySuccess(list);
            }
        }
        return ServerResponse.createByErrorCodeMessage(1,"无该品类");
    }
}
