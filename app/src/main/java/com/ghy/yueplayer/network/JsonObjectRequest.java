package com.ghy.yueplayer.network;


import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.RestRequest;
import com.yolanda.nohttp.rest.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * JsonObject请求
 * 默认使用Post请求
 */
public class JsonObjectRequest extends RestRequest<JSONObject> {

    public JsonObjectRequest(String url) {
        this(url, RequestMethod.POST);
    }

    public JsonObjectRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
        setAccept(Headers.HEAD_VALUE_ACCEPT_APPLICATION_JSON);
    }

    @Override
    public JSONObject parseResponse(Headers responseHeaders, byte[] responseBody) throws JSONException {
        String result = StringRequest.parseResponseString(responseHeaders, responseBody);
        Logger.i("请求响应Headers-->>  " + responseHeaders.toString());
        Logger.i("请求响应Response-->>  " + result);
        return new JSONObject(result);
    }
}
