package com.ghy.yueplayer.util;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * Created by GHY on 2015/3/23.
 */
public class GetJsonUtil {

    public interface JsonCallBack{
        void getJson(String getJson);
    }

    public static void getJsonFromWeb(String url, final JsonCallBack callback){
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(1000 * 6);// 设置超时
        httpUtils.send(HttpRequest.HttpMethod.GET,url,new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> result) {
                callback.getJson(result.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                callback.getJson(null);
            }
        });
    }
}
