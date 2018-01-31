package com.ghy.yueplayer.network.Interceptors;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by GHY on 2018/1/8.
 * Desc:请求的header已经有token了，则不需要在添加token了，token的键一般是Authorization
 */

public class TokenInterceptor implements Interceptor {

    private static final String USER_TOKEN = "Authorization";
    private final String token;

    public TokenInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        if (token == null || originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest);
        }
        Request request = originalRequest.newBuilder()
                .header(USER_TOKEN, token)
                .build();
        return chain.proceed(request);
    }
}
