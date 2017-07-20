package com.ghy.yueplayer.network;

import com.google.gson.Gson;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.RestRequest;
import com.yolanda.nohttp.rest.StringRequest;

/**
 * 自定义JavaBean请求
 * 使用Gson解析
 * 默认使用Post请求
 */
public class JavaBeanRequest<T> extends RestRequest<T> {

    private Class<T> clazz;

    public JavaBeanRequest(String url, Class<T> clazz) {
        this(url, RequestMethod.POST, clazz);
    }

    public JavaBeanRequest(String url, RequestMethod requestMethod, Class<T> clazz) {
        super(url, requestMethod);
        this.clazz = clazz;
    }

    @Override
    public T parseResponse(Headers responseHeaders, byte[] responseBody) throws Throwable {
        String response = StringRequest.parseResponseString(responseHeaders, responseBody);
        // 这里如果数据格式错误，或者解析失败，会在失败的回调方法中返回 ParseError 异常。
        Logger.i("请求响应Headers-->>  " + responseHeaders.toString());
        Logger.i("请求响应Response-->>  " + response);
        return new Gson().fromJson(response, clazz);
    }
}
