package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.CategoryService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("manage/category")
public class CategoryController {
    @DubboReference
    private CategoryService categoryService;


    /**
     * 通过父节点id获得同品级子类信息
     * @param categoryId 父节点id
     * @param session 登录的用户信息
     * @return
     */
    @GetMapping("get_category.do")
    public ServerResponse<List<Category>> getCategory(Integer categoryId, HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (categoryId == null){
            categoryId = 0;
        }
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return categoryService.getCategory(categoryId);
    }

    /**
     * 添加品类
     * @param parentId
     * @param categoryName
     * @return
     */
    @GetMapping("add_category.do")
    public ServerResponse addCategory(Integer parentId,String categoryName){
        //默认父类id为0
        if (parentId == null){
            parentId = 0;
        }
        return categoryService.addCategory(parentId,categoryName);
    }

    /**
     * 修改品类名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    @GetMapping("set_category_name.do")
    public ServerResponse<String> setCategoryName(Integer categoryId,String categoryName){

        return categoryService.setCategoryName(categoryId,categoryName);
    }


    @GetMapping("get_deep_category.do")
    public ServerResponse<List<String>> getDeepCategory(Integer categoryId,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user == null || user.getRole()==0){
            return ServerResponse.createByErrorCodeMessage(1,"无权限");
        }
        return categoryService.getDeepCategory(categoryId);
    }
}
