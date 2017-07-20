package com.ghy.yueplayer.network;

/**
 * 网络请求回调结果
 */
public interface HttpListener<T> {

//    void onSucceed(Response<T> response);
//
//    void onFailed(Response<T> response);

    void onSucceed(T t);

    void onFailed(int errorCode, String msg);

}
