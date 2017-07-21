package com.ghy.yueplayer.network;

import android.content.Context;

import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;


/**
 * Created by GHY on 2016/12/22.
 * Desc: NoHttp网络请求初始化
 * 文档地址：http://doc.nohttp.net/222342
 */

public class NoHttpUtils {

    public static void initNoHttp(Context context) {
//        NoHttp.initialize(context); // NoHttp默认初始化。
        NoHttp.initialize(context, new NoHttp.Config()
                .setConnectTimeout(30 * 1000) // 全局连接超时时间，单位毫秒。
                .setReadTimeout(30 * 1000) // 全局服务器响应超时时间，单位毫秒。
                .setNetworkExecutor(new OkHttpNetworkExecutor()) // 使用OkHttp做网络层。
        );
        //设置noHttp请求logger
        Logger.setDebug(true); // 开启NoHttp调试模式。
        Logger.setTag("NoHttpLogger"); // 设置NoHttp打印Log的TAG。
    }

}