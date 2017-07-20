package com.ghy.yueplayer.network;

import com.ghy.yueplayer.BuildConfig;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ParseError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.RestResponse;

import java.net.ConnectException;
import java.net.ProtocolException;

/**
 * 网络请求回调监听
 * Java后台
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

    private HttpListener<T> httpCallback;

    private HttpDialogListener mHttpDialogListener;

    public void setHttpDialogListener(HttpDialogListener httpDialogListener) {
        this.mHttpDialogListener = httpDialogListener;
    }

    /**
     * @param httpCallback 回调对象.
     */
    public HttpResponseListener(HttpListener<T> httpCallback) {
        this.httpCallback = httpCallback;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        if (mHttpDialogListener != null) mHttpDialogListener.showHttpDialog();
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        if (mHttpDialogListener != null) mHttpDialogListener.dismissHttpDialog();
    }

    /**
     * 成功回调
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (httpCallback != null) {
            // http响应码，一般是200成功
            // w3c标准http响应码：http://www.w3school.com.cn/tags/html_ref_httpmessages.asp
            int code = response.responseCode();
            if (code == 200) {
                httpCallback.onSucceed(response.get());
            } else {
                Response<T> error = new RestResponse<>(response.request(),
                        response.isFromCache(),
                        response.getHeaders(),
                        null,
                        response.getNetworkMillis(),
                        new ParseError("网络请求错误")); //这里可以传一个你的自定义异常
                onFailed(what, error); //去让错误的回调处理
            }
        }
    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, Response<T> response) {
        Exception exception = response.getException();
        int code = response.responseCode();//响应码
        int errorCode;//自定义错误码
        String msg;//自定义错误信息
        //异常错误信息
        if (exception instanceof NetworkError) {//网络错误
            errorCode = ErrorCode.NET_WORK_ERROR;
            msg = "请检查网络设置";
        } else if (exception instanceof TimeoutError) {//请求超时
            errorCode = ErrorCode.TIME_OUT;
            msg = "抱歉，请求超时";
        } else if (exception instanceof UnKnownHostError) {//找不到服务器
            errorCode = ErrorCode.UN_KNOWN_HOST;
            msg = "连接服务器失败";
        } else if (exception instanceof URLError) {//URL错误
            errorCode = ErrorCode.URL_ERROR;
            msg = "请求地址错误";
        } else if (exception instanceof NotFoundCacheError) {//这个异常只会在仅仅查找缓存时没有找到缓存时返回
            errorCode = ErrorCode.NOT_FOUND_CACHE;
            msg = "没有发现缓存";
        } else if (exception instanceof ProtocolException) {//系统不支持的请求方法
            errorCode = ErrorCode.SYSTEM_NOT_SUPPORT;
            msg = "系统不支持的请求方法";
        } else if (exception instanceof ParseError) {//解析出现异常
            errorCode = ErrorCode.PARSER_ERROR;
            msg = "解析数据时发生错误";
        } else if (exception instanceof ConnectException) {//连接服务器异常
            errorCode = ErrorCode.CONNECT_ERROR;
            msg = "连接服务器出错，请稍后重试";
        } else {//未知其他错误
            errorCode = ErrorCode.UN_KNOWN;
            msg = "请求出错，请稍后重试";
        }
        //错误码信息--debug模式下提供错误码提示信息，release模式下提供简洁信息
        if (code == 403) {
            errorCode = ErrorCode.CODE_403;
            msg = BuildConfig.DEBUG ? "403-请求页面的访问被禁止" :
                    "请求出错，请稍后重试";
        } else if (code == 404) {
            errorCode = ErrorCode.CODE_404;
            msg = BuildConfig.DEBUG ? "404-服务器无法找到被请求的页面" :
                    "请求出错，请稍后重试";
        } else if (code == 500) {
            errorCode = ErrorCode.CODE_500;
            msg = BuildConfig.DEBUG ? "500-服务器遇到不可预知的情况" :
                    "请求出错，请稍后重试";
        } else if (code == 502) {
            errorCode = ErrorCode.CODE_502;
            msg = BuildConfig.DEBUG ? "502-Bad Gateway" :
                    "请求出错，请稍后重试";
        } else if (code == 503) {
            errorCode = ErrorCode.CODE_503;
            msg = BuildConfig.DEBUG ? "503-服务器不可用" :
                    "请求出错，请稍后重试";
        } else if (code == 504) {
            errorCode = ErrorCode.CODE_504;
            msg = BuildConfig.DEBUG ? "504-服务器网关超时" :
                    "请求出错，请稍后重试";
        }
        Logger.e("请求出错-->>" + "  httpCode-->" + code + "  exception-->" + exception.getMessage());
        //回调错误信息
        if (httpCallback != null) httpCallback.onFailed(errorCode, msg);
    }

}
