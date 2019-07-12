package com.ghy.yueplayer.constant;

/**
 * @author GHY
 * @date 2018/1/6
 * Desc:HttpConfig配置信息
 */
public class HttpConfig {

    /**
     * ================================================
     * 请求方法
     * ================================================
     */
    public static final String REQUEST_POST = "POST";
    public static final String REQUEST_GET = "GET";

    /**
     * ================================================
     * 请求日志TAG
     * ================================================
     */
    public static final String API_LOG_TAG = "API_HTTP_URL";

    public static final String API_LOG_URL = "URL";

    /**
     * ================================================
     * 请求解析字段
     * ================================================
     */
    public static final String RESULT_CODE = "code";
    public static final String RESULT_MSG = "msg";

    /**
     * ================================================
     * 请求超时配置
     * ================================================
     */
    public static final int HTTP_TIME_OUT = 30;
    public static final int HTTP_TIME_OUT_BIG = 120;
}
