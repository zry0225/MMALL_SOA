package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @author zhangruiyan
 */
public interface CategoryService {

    ServerResponse<List<Category>> getCategory(int categoryId);

    ServerResponse<String> addCategory(Integer parentId, String categoryName);

    ServerResponse<String> setCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<String>> getDeepCategory(Integer categoryId);
}
