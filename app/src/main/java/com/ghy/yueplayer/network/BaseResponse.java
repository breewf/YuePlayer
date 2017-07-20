package com.ghy.yueplayer.network;

/**
 * Created by GHY on 2016/12/28.
 * Desc: 请求返回数据基类，外层基础数据
 */

public class BaseResponse {

    private String result;//请求结果 0：失败，1：成功
    private String msg;//状态信息
    private String errorCode;//错误码

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
