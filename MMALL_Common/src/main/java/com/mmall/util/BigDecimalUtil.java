package com.mmall.util;

import java.math.BigDecimal;

/**
 * @author zhangruiyan
 * 小数工具类
 */
public class BigDecimalUtil {
    private BigDecimalUtil(){}
    public static BigDecimal add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    /**
     * 减法
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    /**
     * 乘法
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    /**
     * 除法
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        //四舍五入，保留两位小数
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }

}
