package com.mmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.ProductService;
import com.mmall.util.FastDFSUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangruiyan
 */
@RestController
@RequestMapping("manage/product")
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService productService;

    /**
     * 产品列表
     * @param pageSize
     * @param pageNum
     * @param session
     * @return
     */
    @GetMapping("list.do")
    public ServerResponse<PageInfo<Product>> productList(@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                     @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, HttpSession session){

        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            PageHelper.startPage(pageNum,pageSize);
            return productService.show();
        }
        return ServerResponse.createByErrorCodeMessage(10,"用户未登录，请登录");
    }

    /**
     * 产品搜索（可模糊）
     * @param productName
     * @param productId
     * @param pageSize
     * @param pageNum
     * @param session
     * @return
     */
    @GetMapping("search.do")
    public ServerResponse<PageInfo<Product>> search(String productName,Integer productId,@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                                    @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, HttpSession session){

        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            PageHelper.startPage(pageNum,pageSize);
            return productService.search(productName,productId);
        }
        return ServerResponse.createByErrorCodeMessage(10,"用户未登录，请登录");
    }

    /**
     * 上传文件，调用之前需要开启对应服务器的跟踪器、储存节点、nginx
     * @param
     * @return
     */
    @PostMapping("upload.do")
    public ServerResponse<Map<String,String>> upload(MultipartFile upload_file){
        String url = FastDFSUtil.upload(upload_file);
        String uri = url.replace("http://img.happymmall.com/", "");
        HashMap<String, String> srcMap = new HashMap<>();
        srcMap.put("uri",uri);
        srcMap.put("url",url);
        TokenCache.setKey("fileUrl",uri);
        return ServerResponse.createBySuccess(srcMap);


    }


    /**
     * 附文本上传图片
     */
    @RequestMapping(value = "richtext_img_upload.do",method = RequestMethod.POST)
    public Map richtextImgUpload(MultipartFile upload_file){
        String url = FastDFSUtil.upload(upload_file);
        HashMap<String, String> map = new HashMap<>();
        if (url == null) {
            map.put("file_path", "[real file path]");
            map.put("msg", "error message");
            map.put("success", "false");
            return map;
        }
        map.put("file_path", url);
        map.put("msg", "上传成功");
        map.put("success", "true");
        return map;

    }

    /**
     * 上传富文本
     * @param session
     * @param uploadFile
     * @param request
     * @param response
     * @return
     */
/*    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(HttpSession session,
                                 @RequestParam("uploadFile") MultipartFile uploadFile,
                                 HttpServletRequest request,
                                 HttpServletResponse response){

        Map resultMap = Maps.newHashMap();
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = path+ FastDFSUtil.upload(uploadFile);
            if (StringUtils.isNotBlank(targetFileName)){
                String url = PropertiesUtil.getProperty("") + targetFileName;
                resultMap.put("success", true);
                resultMap.put("msg", "上传成功");
                resultMap.put("file_path", url);
                response.addHeader("Access-Control-Allow-Headers","X-File-Name");
                return resultMap;
            }else {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }




    }*/

    /**
     * 产品详情
     * @param productId
     * @param session
     * @return
     */
    @GetMapping("detail.do")
    public ServerResponse<ProductDetailVo> detail(int productId,HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null||user.getRole()==0){
            return ServerResponse.createByErrorCodeMessage(1,"没有权限");
        }
        return productService.detail(productId);
    }

    /**
     * 产品上下架
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.GET)
    public ServerResponse<String> setSaleStatue(int productId,int status){
        return productService.setSaleStatue(productId,status);
    }


    /**
     * 新增或更新产品
     * @return
     */
    @RequestMapping(value = "save.do",method = RequestMethod.GET)
    public ServerResponse<String> save(Product product){

        //id为空，也就是说是新增
        if (product.getId() == null){
            return productService.add(product);
        }
        return productService.update(product);
    }
}
