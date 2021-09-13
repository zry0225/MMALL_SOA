package com.mmall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @author zhangruiyan
 * 响应公共类
 * 保证序列化json的时候，如果是null的对象，key也会消失
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    /**
     * 使之不在json序列化结果中，is相当于set方法，不让json序列化它
     *
     * 返回的是成功的code
     * @return
     */
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    /**
     * 成功
     */
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    /**
     * 成功（携带信息）
     */
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    /**
     * 成功（携带数据）
     */
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    /**
     * 成功（携带数据和信息）
     */
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    /**
     * 失败
     */
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    /**
     * 失败（携带错误信息）
     */
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    /**
     * 自定义失败码以及信息
     */
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }
}
