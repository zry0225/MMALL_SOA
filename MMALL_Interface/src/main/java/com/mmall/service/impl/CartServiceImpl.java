package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.CartService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author zhangruiyan
 */
@Service
public class CartServiceImpl implements CartService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceImpl.class);
    @Autowired
    private CartMapper cartMapper;


    /**
     * 获得购物车列表
     * @param id
     * @return
     */
    @Override
    public ServerResponse<CartVo> list(Integer id) {
        List<CartProductVo> cartProductVos = new ArrayList<>();
        CartVo cartVo = new CartVo();
        //查找只有cart的数据放入到集合中
        List<Cart> cartList = cartMapper.list(id);

        //购物车里面这个产品的总价
        BigDecimal cartTotalPrice = new BigDecimal(0.00);
        //购物车里所有产品的总价
        BigDecimal productTotalPrice = new BigDecimal(0.00);
        for (Cart cart : cartList) {
            LOGGER.warn("这里是查询cart表之后返回的集合:{}",cart);

            CartProductVo cartProductVo = new CartProductVo();
            //一个一个往里赋值
            cartProductVo.setId(cart.getId());
            cartProductVo.setUserId(cart.getUserId());
            cartProductVo.setProductId(cart.getProductId());
            cartProductVo.setQuantity(cart.getQuantity());
            cartProductVo.setProductChecked(cart.getChecked());

            //将cart表中的产品id传入，获得对应的产品
            Product product = cartMapper.haveProduct(cart.getProductId());
            cartProductVo.setProductName(product.getName());
            cartProductVo.setProductSubtitle(product.getSubtitle());
            cartProductVo.setProductMainImage(product.getMainImage());
            cartProductVo.setProductPrice(product.getPrice());
            cartProductVo.setProductStatus(product.getStatus());


            //获得这个商品总共的价钱（单价*该商品数量）
            productTotalPrice = product.getPrice().multiply(new BigDecimal(cartProductVo.getQuantity()));
            cartProductVo.setProductTotalPrice(productTotalPrice);

            //获得整个购物车的总价
            if (cart.getChecked()==1){
                cartTotalPrice = cartTotalPrice.add(productTotalPrice);
            }

            cartProductVo.setProductStock(product.getStock());


            //如果购物车里商品的数量高于库存数量就返回LIMIT_NUM_FAIL,否则返回LIMIT_NUM_SUCCESS
            if (cartProductVo.getQuantity()>cartProductVo.getProductStock()){
                cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
            }else {
                cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
            }



            //将封装好数据的cartProductVo对象封装到cartProductVo对象集合中
            LOGGER.warn("这是封装好的cartProductVo对象:{}",cartProductVo.toString());
            cartProductVos.add(cartProductVo);
        }
        cartVo.setCartProductVoList(cartProductVos);
        int count = cartMapper.getListCount(id);
        int cont = 0;
        for (CartProductVo cartProductVo : cartProductVos) {
            if (cartProductVo.getProductChecked()==1){
                cont+=1;
            }
        }
        if (cont<count){
            cartVo.setAllChecked(false);
        }else {
            cartVo.setAllChecked(true);
        }

        //购物车总价
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 购物车添加商品
     * 需要根据productId和当前用户id添加一条cart信息，
     * 添加之后，通过当前用户id获得对应的所有购物车信息，
     * 根据productId获得产品对象
     * 将产品对象、购物车对象放入cartProductVo中在放入List<cartProductVo>再放入carVo中
     *
     * @param productId 产品id
     * @param count 新加上去的产品数量
     * @return
     */
    @Override
    public ServerResponse<CartVo> add(int productId, int count,int id) {
        int j = cartMapper.selectProductId(productId,id);
        if (j>0){
           cartMapper.addCount(productId,count,id);
           return list(id);
        }
        //要根据productId和当前用户id添加一条cart信息
        int i = cartMapper.add(productId,count,id);
        if (i<=0){
            return ServerResponse.createByError();
        }
        return list(id);
    }

    /**
     *
     * 购物车更新某一个商品数量
     * 需要根据productId来修改cart中的quantity（加上count），
     * 修改完成之后返回cart对象，通过productId再次获得product对象，
     * 将cart和product里面的字段一个一个set到一个cartProductVo中，
     * 将cartProductVo放到List<cartProductVo>中，再放到cartVo中
     * 更新购物车某一个产品的数量
     * @param productId
     * @param count
     * @param id
     * @return
     */
    @Override
    public ServerResponse<CartVo> update(int productId, int count, Integer id) {
        //需要根据productId、当前登陆对象id来修改cart中的quantity（变成count）
        int i = cartMapper.editQuantity(productId,count,id);
        if (i<=0){
            return ServerResponse.createByError();
        }
        //调用list方法获得该用户的购物车（修改后的）
        return list(id);
    }

    /**
     * 删除购物车某商品（可多选）
     * @param productIds
     * @return
     */
    @Override
    public ServerResponse<CartVo> deleteProduct(int[] productIds,int userId) {
        for (int productId : productIds) {
            cartMapper.deleteProduct(productId,userId);
        }
        return list(userId);
    }

    /**
     * 选中购物车某商品
     * @param productId
     * @param id
     * @return
     */
    @Override
    public ServerResponse<CartVo> select(int productId, Integer id) {
        //通过产品id和当前登陆对象获得该对象购物车中该商品信息
        Cart cart = cartMapper.select(productId,id);
        //设置该条信息为true
        int i = cartMapper.check(productId,id);
        //列表
        ServerResponse<CartVo> list = list(id);


        /*
        //获得对应的产品
        Product product = cartMapper.haveProduct(productId);
        LOGGER.warn("这是获得的产品信息:{}",product.toString());

        List<CartProductVo> cartProductVos = new ArrayList<>();
        CartVo cartVo = new CartVo();
        //购物车里面这个产品的总价
        BigDecimal cartTotalPrice = new BigDecimal(0.00);
        //购物车里所有产品的总价
        BigDecimal productTotalPrice = new BigDecimal(0.00);

            CartProductVo cartProductVo = new CartProductVo();
            //一个一个往里赋值
            cartProductVo.setId(cart.getId());
            cartProductVo.setUserId(cart.getUserId());
            cartProductVo.setProductId(cart.getProductId());
            cartProductVo.setQuantity(cart.getQuantity());
            cartProductVo.setProductChecked(cart.getChecked());

            cartProductVo.setProductName(product.getName());
            cartProductVo.setProductSubtitle(product.getSubtitle());
            cartProductVo.setProductMainImage(product.getMainImage());
            cartProductVo.setProductPrice(product.getPrice());
            cartProductVo.setProductStatus(product.getStatus());
            cartProductVo.setProductStock(product.getStock());
            //获得这个商品总共的价钱（单价*该商品数量）
            productTotalPrice = product.getPrice().multiply(new BigDecimal(cartProductVo.getQuantity()));
            cartProductVo.setProductTotalPrice(productTotalPrice);

        LOGGER.warn("这是购物车商品数量：{}",cartProductVo.getQuantity());
        LOGGER.warn("这是商品库存数量：{}",cartProductVo.getProductStock());
            //如果购物车里商品的数量高于库存数量就返回LIMIT_NUM_FAIL,否则返回LIMIT_NUM_SUCCESS
            if (cartProductVo.getQuantity()>cartProductVo.getProductStock()){
                cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
            }else {
                cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
            }


            //获得整个购物车的总价
            cartTotalPrice = cartTotalPrice.add(productTotalPrice);
            cartProductVo.setProductStock(product.getStock());

            cartProductVo.setProductChecked(Const.Cart.CHECKED);

            //将封装好数据的cartProductVo对象封装到cartProductVo对象集合中
            LOGGER.warn("这是封装好的cartProductVo对象:{}",cartProductVo.toString());
            cartProductVos.add(cartProductVo);
        cartVo.setCartProductVoList(cartProductVos);
        cartVo.setAllChecked(true);
        //购物车总价
        cartVo.setCartTotalPrice(cartTotalPrice);
        return ServerResponse.createBySuccess(cartVo);*/
        return list;
    }

    /**
     * 反选
     * @param productId
     * @param id
     * @return
     */
    @Override
    public ServerResponse<CartVo> unSelect(int productId, Integer id) {
        //获得登陆用户所有购物车信息
        ServerResponse<CartVo> list = list(id);

        //将这个商品设置未选中
        int i = cartMapper.uncheck(productId,id);
       /* for (CartProductVo cartProductVo : list.getData().getCartProductVoList()) {
            if (cartProductVo.getProductId()==productId){
                cartProductVo.setProductChecked(0);
            }
        }*/
       /* for (CartProductVo cartProductVo : list.getData().getCartProductVoList()) {
            if (cartProductVo.getProductId() == productId){
                cartProductVo.setProductChecked(Const.Cart.UN_CHECKED);
            }
        }*/
        //list.getData().getCartProductVoList().get(0).setProductChecked(Const.Cart.UN_CHECKED);
        return list;
    }

    /**
     * 获取购物车商品总数
     * @param id
     * @return
     */
    @Override
    public ServerResponse<Integer> getCartProductCount(Integer id) {
        Integer cont = cartMapper.getCartProductCount(id);
        System.out.println(cont);
        if (cont<0){
            return ServerResponse.createByErrorCodeMessage(10,"出现异常");
        }
        return ServerResponse.createBySuccess(cont);
    }

    /**
     * 全选
     * @param id
     * @return
     */
    @Override
    public ServerResponse<CartVo> selectAll(Integer id) {
        //获得购物车所有信息
        ServerResponse<CartVo> list = list(id);
        list.getData().setAllChecked(true);
        BigDecimal price = new BigDecimal(0.00);
        for (CartProductVo cartProductVo : list.getData().getCartProductVoList()) {
            if (cartProductVo.getProductChecked()==0){
                cartMapper.check(cartProductVo.getProductId(),id);
                cartProductVo.setProductChecked(1);
            }
        }
        for (CartProductVo cartProductVo : list.getData().getCartProductVoList()) {
           price = price.add(cartProductVo.getProductPrice());
        }
        list.getData().setCartTotalPrice(price);
        return list;
    }

    /**
     * 取消全选
     * @param id
     * @return
     */
    @Override
    public ServerResponse<CartVo> unSelectAll(Integer id) {
        ServerResponse<CartVo> list = list(id);
        for (CartProductVo cartProductVo : list.getData().getCartProductVoList()) {
            cartProductVo.setProductChecked(0);
        }
        list.getData().setAllChecked(false);
        list.getData().setCartTotalPrice(new BigDecimal(0.00));
        return list;
    }


}
