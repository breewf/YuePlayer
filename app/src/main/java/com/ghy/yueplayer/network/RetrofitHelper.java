package com.ghy.yueplayer.network;


import com.ghy.yueplayer.api.APIS;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by GHY on 2017/7/20.
 * Retrofit 网络请求
 */
public class RetrofitHelper {

    /**
     * Retrofit
     *
     * @return
     */
    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                //baseUrl
                .baseUrl(APIS.BASE_URL)
                //如果你想接收json结果并解析，你必须把Gson Converter作为一个独立的依赖添加进来
                .addConverterFactory(GsonConverterFactory.create())
                //Retrofit团队有已经准备好了的CallAdapter module。其中最著名的module可能是为RxJava准备的CallAdapter，它将作为Observable返回
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

}


