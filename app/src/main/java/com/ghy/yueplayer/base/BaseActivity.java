package com.ghy.yueplayer.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ghy.yueplayer.network.HttpListener;
import com.ghy.yueplayer.network.HttpResponseListener;
import com.ghy.yueplayer.network.NoHttpUtils;
import com.ghy.yueplayer.network.RequestServer;
import com.yolanda.nohttp.rest.Request;

import java.util.LinkedHashMap;

import butterknife.ButterKnife;

/**
 * Created by GHY on 2017/7/31.
 * Desc: BaseActivity
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected RequestServer mRequestServer;
    protected LinkedHashMap<String, Object> requestParams = new LinkedHashMap<>();
    protected String loadingMsg;
    protected String className;

    /**
     * 获取布局ID
     *
     * @return
     */
    protected abstract int getLayoutID();

    /**
     * 初始化View
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View rootView = getLayoutInflater().inflate(getLayoutID(), null);
        setContentView(rootView);
        className = getClass().getSimpleName();
        mRequestServer = RequestServer.getRequestInstance();
        ButterKnife.bind(this);
        initView();
        initData();
    }

    /**
     * 使用noHttp进行网络请求
     * 显示默认loading
     */
    public <T> void requestAPI(Request<T> request, HttpListener<T> callback) {
        requestAPI(request, callback, "");
    }

    /**
     * 使用noHttp进行网络请求
     * 显示自定义文案loading
     * 不显示loading时传null
     */
    public <T> void requestAPI(Request<T> request, HttpListener<T> callback, String loadingMsg) {
        this.loadingMsg = loadingMsg;
        request.setCancelSign(className);
        NoHttpUtils.addRequestParams(request, requestParams);
        HttpResponseListener<T> httpResponseListener = new HttpResponseListener<>(callback);
        RequestServer.getRequestInstance().add(0, request, httpResponseListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mRequestServer != null) mRequestServer.cancelBySign(className);
    }
}
