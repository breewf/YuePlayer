package com.ghy.yueplayer.network.Interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by GHY on 2018/1/6.
 * Desc:http请求添加通用header
 */

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
//                .addHeader("token", "token")
//                .addHeader("token", "token")
                //403 forbidden的错误解决，使用okhttp获取并没有异常，用retrofit+RxJava会报错403
                .addHeader("User-Agent", "Mozilla/5.0");
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
