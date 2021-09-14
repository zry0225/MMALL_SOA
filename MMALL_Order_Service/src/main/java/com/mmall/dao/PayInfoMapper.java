package com.mmall.dao;

import com.mmall.pojo.PayInfo;
import org.springframework.stereotype.Repository;

/**
 * @author zhangruiyan
 */
@Repository
public interface PayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    PayInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);
}