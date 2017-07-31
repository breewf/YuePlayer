package com.ghy.yueplayer.network;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;


/**
 * 请求服务-请求队列
 */
public class RequestServer {

    private static RequestServer requestServer;

    /**
     * 请求队列.
     */
    private RequestQueue requestQueue;

    /**
     * 下载队列.
     */
    private static DownloadQueue downloadQueue;

    private RequestServer() {
        //队列的默认并发值为3
        requestQueue = NoHttp.newRequestQueue();
    }

    /**
     * 请求队列.
     */
    public synchronized static RequestServer getRequestInstance() {
        if (requestServer == null)
            synchronized (RequestServer.class) {
                if (requestServer == null)
                    requestServer = new RequestServer();
            }
        return requestServer;
    }

    /**
     * 下载队列.
     */
    public static DownloadQueue getDownloadInstance() {
        if (downloadQueue == null)
            downloadQueue = NoHttp.newDownloadQueue(3);
        return downloadQueue;
    }

    /**
     * 添加一个请求到请求队列.
     */
    public <T> void add(int what, Request<T> request, HttpResponseListener<T> callback) {
        requestQueue.add(what, request, callback);
    }

    /**
     * 添加一个请求到请求队列.
     */
    public <T> void addCommon(int what, Request<T> request, HttpListener<T> callback) {
        requestQueue.add(what, request, new HttpResponseListener<>(callback));
    }

    /**
     * 取消这个sign标记的所有请求.
     */
    public void cancelBySign(Object sign) {
        if (sign == null) return;
        requestQueue.cancelBySign(sign);
    }

    /**
     * 取消队列中所有请求.
     */
    public void cancelAll() {
        requestQueue.cancelAll();
    }

    /**
     * 退出app时停止所有请求.
     */
    public void stopAll() {
        requestQueue.stop();
    }

}
