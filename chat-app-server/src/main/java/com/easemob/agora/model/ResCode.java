package com.easemob.agora.model;

public enum ResCode {
    RES_OK(200),//可以成功返回
    RES_REQUEST_PARAM_ERROR(400),//请求参数错误
    RES_USER_ALREADY_EXISTS(400),//用户已经存在
    RES_CONTENT_TYPE_ERROR(400), //传参类型错误
    RES_APPKEY_ERROR(400),  //appkey错误
    RES_UNAUTHORIZED_ERROR(401), //表示未授权[无token、token错误、token过期]
    RES_USER_NOT_FOUND(404), //此用户不存在
    RES_REQUEST_METHOD_ERROR(405), //请求方法错误
    RES_REACH_LIMIT(429), //超过限制
    RES_UNKNOWN(500);  //其他未知错误

    public int code;

    ResCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
