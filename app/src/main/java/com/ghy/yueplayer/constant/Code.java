package com.ghy.yueplayer.constant;

/**
 * Created by GHY on 2018/1/6.
 * Desc: http请求响应码
 */

public class Code {

    /***------------------业务逻辑code码--和后台约定------------------------*/
    public static final int SUCCESS = 100000;//业务逻辑响应成功

    public static final int ERROR_CODE_TOKEN_EXCEPTION = 406;//token失效

    public static final int SYSTEM_EXCEPTION = 100001;//系统异常

    public static final int USERNAME_PASSWORD_ERROR = 100100;//用户名或密码错误


    /***------------------系统/请求异常--自定义------------------------*/
    public static final int ERROR_SYS_TIMEOUT = -1000;//超时
    public static final int ERROR_SYS_CONNECT_EXCEPTION = -1001;//连接异常
    public static final int ERROR_SYS_NET_ERROR = -1002;//网络错误
    public static final int ERROR_SYS_HOST_ERROR = -1003;//主机错误
    public static final int ERROR_SYS_UNKNOWN_ERROR = -1004;//未知异常

}
