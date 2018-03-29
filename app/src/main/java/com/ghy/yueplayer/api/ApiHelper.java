package com.ghy.yueplayer.api;

import android.support.annotation.NonNull;

import com.ghy.yueplayer.constant.HttpConfig;
import com.ghy.yueplayer.network.ApiRequestCallBackString;
import com.ghy.yueplayer.network.ApiRequestResultCallBack;
import com.ghy.yueplayer.network.BaseRequestCallBack;
import com.ghy.yueplayer.network.RetrofitManager;
import com.ghy.yueplayer.network.observer.JSONObjectObserver;
import com.ghy.yueplayer.network.observer.StringObserver;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by GHY on 2018/1/6.
 * Desc:Api助手类
 */

public class ApiHelper {

    //====================获取ApiService实例===========================

    private static ApiService getApiService() {
        return RetrofitManager.getInstance().API();
    }

    //====================常用POST、GET请求=============================

    /**
     * api请求
     *
     * @param rxFragmentActivity context
     * @param requestParams      请求参数
     * @param url                请求url
     * @param loadingMsg         loading文案
     * @param requestCallBack    请求回调
     */
    public static void requestApi(RxFragmentActivity rxFragmentActivity, Map requestParams, String url, String loadingMsg, BaseRequestCallBack requestCallBack) {
        requestApi(rxFragmentActivity, requestParams, url, loadingMsg, requestCallBack, HttpConfig.REQUEST_POST);
    }

    public static void requestApi(RxFragmentActivity rxFragmentActivity, Map requestParams, String url, String loadingMsg, BaseRequestCallBack requestCallBack, String requestMethod) {
        requestApiPublish(rxFragmentActivity, null, requestParams, url, loadingMsg, requestCallBack, requestMethod);
    }

    public static void requestApi(RxFragment rxFragment,
                                  Map requestParams,
                                  String url, String loadingMsg,
                                  BaseRequestCallBack requestCallBack) {
        requestApi(rxFragment, requestParams, url, loadingMsg, requestCallBack, HttpConfig.REQUEST_POST);
    }

    public static void requestApi(RxFragment rxFragment,
                                  Map requestParams,
                                  String url, String loadingMsg,
                                  BaseRequestCallBack requestCallBack, String requestMethod) {
        requestApiPublish(null, rxFragment, requestParams, url, loadingMsg, requestCallBack, requestMethod);
    }

    //====================请求分发=========================

    @SuppressWarnings("unchecked")
    private static void requestApiPublish(RxFragmentActivity rxFragmentActivity, RxFragment rxFragment,
                                          Map requestParams,
                                          String url, String loadingMsg,
                                          BaseRequestCallBack requestCallBack, String requestMethod) {
        if (rxFragmentActivity == null && rxFragment == null) return;
        if (requestCallBack == null) return;
        LinkedHashMap<String, Object> requestParamsNew = new LinkedHashMap<>(requestParams);
        Observable<String> observable = getObservableMethod(requestParamsNew, url, requestMethod);
        if (observable != null) {
            observable.compose(RetrofitManager.schedulersTransformer())
                    .compose(rxFragmentActivity == null ? rxFragment.bindToLifecycle() : rxFragmentActivity.bindToLifecycle())//绑定生命周期
                    .subscribeWith(getUserObserver(rxFragmentActivity, rxFragment, loadingMsg, requestCallBack));
        }
    }

    //====================post提交Json=========================

    public static void requestApiPostJson(RxFragmentActivity rxFragmentActivity,
                                          String jsonData,
                                          String url, String loadingMsg,
                                          BaseRequestCallBack requestCallBack) {
        requestApiPostJson(rxFragmentActivity, null, jsonData, url, loadingMsg, requestCallBack);
    }

    public static void requestApiPostJson(RxFragment rxFragment,
                                          String jsonData,
                                          String url, String loadingMsg,
                                          BaseRequestCallBack requestCallBack) {
        requestApiPostJson(null, rxFragment, jsonData, url, loadingMsg, requestCallBack);
    }

    private static void requestApiPostJson(RxFragmentActivity rxFragmentActivity,
                                           RxFragment rxFragment,
                                           String jsonData,
                                           String url, String loadingMsg,
                                           BaseRequestCallBack requestCallBack) {
        if (rxFragmentActivity == null && rxFragment == null) return;
        if (requestCallBack == null) return;
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        getApiService().requestApiPostJson(url, body)
                .compose(RetrofitManager.schedulersTransformer())
                .compose(rxFragmentActivity == null ? rxFragment.bindToLifecycle() : rxFragmentActivity.bindToLifecycle())//绑定生命周期
                .subscribe(getUserObserver(rxFragmentActivity, rxFragment, loadingMsg, requestCallBack));

    }

    /**
     * 获取Observer
     */
    @NonNull
    private static Observer<String> getUserObserver(RxFragmentActivity rxFragmentActivity, RxFragment rxFragment, String loadingMsg, BaseRequestCallBack requestCallBack) {
        return requestCallBack instanceof ApiRequestResultCallBack ?
                getJSONObserver(rxFragmentActivity, rxFragment, loadingMsg, (ApiRequestResultCallBack) requestCallBack) :
                getStringObserver(rxFragmentActivity, rxFragment, loadingMsg, (ApiRequestCallBackString) requestCallBack);
    }

    /**
     * 获取请求方式Observable
     *
     * @param requestParams 请求参数
     * @param url           请求url
     * @param requestMethod 请求方式
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Observable<String> getObservableMethod(Map requestParams, String url, String requestMethod) {
        if (requestMethod.equals(HttpConfig.REQUEST_POST)) {
            if (haveRequestParams(requestParams)) {
                return getApiService().requestApiJSON(url, requestParams);
            } else {
                return getApiService().requestApiJSONNoParams(url);
            }
        } else if (requestMethod.equals(HttpConfig.REQUEST_GET)) {
            if (haveRequestParams(requestParams)) {
                return getApiService().requestApiJSONGet(url, requestParams);
            } else {
                return getApiService().requestApiJSONGetNoParams(url);
            }
        }
        return null;
    }

    private static boolean haveRequestParams(Map requestParams) {
        return (requestParams != null && requestParams.size() != 0);
    }

    private static JSONObjectObserver getJSONObserver(RxFragmentActivity rxFragmentActivity, RxFragment rxFragment, String loadingMsg, ApiRequestResultCallBack requestCallBack) {
        return new JSONObjectObserver<String>(rxFragmentActivity == null ? rxFragment.getActivity() : rxFragmentActivity, loadingMsg) {

            @Override
            protected void onSuccess(String text) {
                try {
                    JSONObject jData = new JSONObject(text);
                    String msg = jData.optString(HttpConfig.RESULT_MSG);//msg
                    int code = jData.optInt(HttpConfig.RESULT_CODE, -1);//code
                    if (requestCallBack != null)
                        requestCallBack.callbackResult(jData, msg, code, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onError(int code, String message) {
                if (requestCallBack != null)
                    requestCallBack.callbackResult(new JSONObject(), message, code, false);
            }
        };
    }

    private static StringObserver getStringObserver(RxFragmentActivity rxFragmentActivity, RxFragment rxFragment, String loadingMsg, ApiRequestCallBackString requestCallBack) {
        return new StringObserver<String>(rxFragmentActivity == null ? rxFragment.getActivity() : rxFragmentActivity, loadingMsg) {

            @Override
            protected void onSuccess(String s) {
                if (requestCallBack != null)
                    requestCallBack.requestCallback(s, true);
            }

            @Override
            protected void onError(int code, String message) {
                if (requestCallBack != null)
                    requestCallBack.requestCallback("", false);
            }
        };
    }

}
