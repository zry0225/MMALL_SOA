package com.mmall.common;

/**
 * @author zhangruiyan
 */

public enum ResponseCode {
    /**
     * 成功
     */
    SUCCESS(0,"SUCCESS"),
    /**
     * 错误
     */
    ERROR(1,"ERROR"),
    /**
     * 未登录
     */
    NEED_LOGIN(10,"NEED_LOGIN"),
    /**
     * 非法参数
     */
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;
    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }
    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }
}
