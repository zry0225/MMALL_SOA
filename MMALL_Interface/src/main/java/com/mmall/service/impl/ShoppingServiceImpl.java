package com.mmall.service.impl;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.ShoppingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangruiyan
 *
 */
@Service
public class ShoppingServiceImpl implements ShoppingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingServiceImpl.class);
    @Autowired
    private ShippingMapper shippingMapper;
    /**
     * 添加地址
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse<Integer> add(Shipping shipping,int userId) {
        int i = shippingMapper.add(shipping,userId);
        LOGGER.warn("这是执行添加方法之后返回的判断值：{}",i);
        if (i>0){
            int shippingId = shippingMapper.selectId(shipping,userId);
            return ServerResponse.createBySuccess("新建地址成功",shippingId);
        }
        return ServerResponse.createByErrorCodeMessage(1,"新建地址失败");
    }

    /**
     * 删除地址
     * @param shippingId
     * @param id
     * @return
     */
    @Override
    public ServerResponse<String> del(int shippingId, Integer id) {
        int i = shippingMapper.del(shippingId, id);
        if (i>0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorCodeMessage(1,"删除地址失败");
    }


    /**
     * 在线更改地址信息
     * @param shipping
     * @return
     */
    @Override
    public ServerResponse<String> update(Shipping shipping, Integer id) {
        int i = shippingMapper.update(shipping,id);
        if (i>0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }



    /**
     * 选中查看具体的地址
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse<Shipping> select(int shippingId,int id) {
        Shipping shipping = shippingMapper.select(shippingId,id);
        return ServerResponse.createBySuccess(shipping);
    }

    /**
     * 地址列表
     * @param id
     * @return
     */
    @Override
    public ServerResponse<PageInfo<Shipping>> list(int id) {
        List<Shipping> shippings =  shippingMapper.list(id);
        PageInfo<Shipping> list = new PageInfo<>(shippings);
        return ServerResponse.createBySuccess(list);
    }
}
