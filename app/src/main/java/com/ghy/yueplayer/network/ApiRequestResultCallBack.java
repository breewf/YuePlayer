package com.ghy.yueplayer.network;


import org.json.JSONObject;

/**
 * Created by GHY on 2018/1/6.
 * Modify by GHY on 2018/1/6.
 * ClassDesc:Http网络请求回调
 * 返回完整的json结果，jData为完整的返回结果，不解析服务器返回的result，使用者自行解析result
 **/
public abstract class ApiRequestResultCallBack extends BaseRequestCallBack {

    public abstract void callbackResult(JSONObject jData, String msg, int code, boolean success);

}