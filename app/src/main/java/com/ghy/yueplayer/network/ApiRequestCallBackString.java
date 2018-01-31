package com.ghy.yueplayer.network;


/**
 * Created by GHY on 2018/1/6.
 * Modify by GHY on 2018/1/6.
 * ClassDesc:Http网络请求回调--String
 **/
public abstract class ApiRequestCallBackString extends BaseRequestCallBack {
    public abstract void requestCallback(String s, boolean success);
}