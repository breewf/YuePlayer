package com.ghy.yueplayer.network;

/**
 * Created by GHY on 2016/12/28.
 * 请求错误码
 */
public class ErrorCode {
    /**
     * 未知错误
     */
    public static final int UN_KNOWN = 0;
    /**
     * 网络错误
     */
    public static final int NET_WORK_ERROR = -1;
    /**
     * 请求超时
     */
    public static final int TIME_OUT = -2;
    /**
     * 找不到服务器
     */
    public static final int UN_KNOWN_HOST = -3;
    /**
     * url错误
     */
    public static final int URL_ERROR = -4;
    /**
     *没有找到缓存
     */
    public static final int NOT_FOUND_CACHE = -5;
    /**
     *系统不支持的方法
     */
    public static final int SYSTEM_NOT_SUPPORT = -6;
    /**
     *解析异常
     */
    public static final int PARSER_ERROR = -7;
    /**
     *连接服务器异常
     */
    public static final int CONNECT_ERROR = -8;

    /**
     *403异常
     */
    public static final int CODE_403 = -9;
    /**
     *404异常
     */
    public static final int CODE_404 = -10;
    /**
     *500异常
     */
    public static final int CODE_500 = -11;
    /**
     *502异常
     */
    public static final int CODE_502 = -12;
    /**
     *503异常
     */
    public static final int CODE_503 = -13;
    /**
     *504异常
     */
    public static final int CODE_504 = -14;
    /**
     *other异常
     */
    public static final int CODE_OTHER = -15;
}
