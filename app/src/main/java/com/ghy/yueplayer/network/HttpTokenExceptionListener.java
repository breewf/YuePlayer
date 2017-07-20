package com.ghy.yueplayer.network;

/**
 * Created by GHY on 2017/7/6.
 * Desc: http请求token异常回调
 */

public interface HttpTokenExceptionListener {

    void tokenException(int code, String msg);

}
