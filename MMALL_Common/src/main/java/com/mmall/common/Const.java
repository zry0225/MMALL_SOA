package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author zhangruiyan
 */
public class Const {
    /**
     * 当前用户?  对象.toString????
     */
    public static final String CURRENT_USER = "currentUser";

    /**
     * 邮箱(可以判断是否用户存在？)
     */
    public static final String EMAIL = "email";

    /**
     * 用户名称（可以判断是否用户存在？）
     */
    public static final String USERNAME = "username";

    /**
     * 产品列表常量（接口）
     */
    public interface ProductListOrderBy{
        /**
         * 产品价格升序，常量降序，存入sets集合中
         */
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    /**
     * 购物车常量接口
     */
    public interface Cart{
        //购物车选中状态
        int CHECKED = 1;
        //购物车未选中状态
        int UN_CHECKED = 0;
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    /**
     * 权限常量接口
     */

    public interface Role{
        //普通用户
        int ROLE_CUSTOMER = 0;
        //管理员
        int ROLE_ADMIN = 1;
    }

    /**
     * 产品状态枚举类
     */
    public enum ProductStatusEnum{
        /**
         * 产品是正常上架状态
         */
        ON_SALE(1,"在线");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        public String getValue(){
            return value;
        }
        public int getCode(){
            return code;
        }
    }

    /**
     * 订单状态枚举
     */
    public enum OrderStatusEnum {
        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        ORDER_CLOSE(60, "订单关闭"),
        ORDER_SUCCESS(50, "订单完成"),
        PAID(20, "已付款"),
        SHIPPED(40, "已发货");

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    /**
     * 支付宝回调函数接口常量
     */
    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    /**
     * 支付方式枚举，目前就一种支付方式，方便以后扩展
     */
    public enum PayPlatFormEnum{
        ALIPAY(1,"支付宝");
        PayPlatFormEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;
        public String getValue(){
            return value;
        }
        public int getCode(){
            return code;
        }
    }

    /**
     * 付款方式枚举，目前就一种方式，方便以后扩展
     */
    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");
        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;
        public String getValue(){
            return value;
        }
        public int getCode(){
            return code;
        }
        public static PaymentTypeEnum codeOf(int code){
            for (PaymentTypeEnum paymentTypeEnum : values()){
                if (paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

}
