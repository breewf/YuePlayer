package com.ghy.yueplayer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghy.yueplayer.api.ApiHelper;
import com.ghy.yueplayer.api.ApiService;
import com.ghy.yueplayer.network.BaseRequestCallBack;
import com.ghy.yueplayer.network.RetrofitManager;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.LinkedHashMap;

import butterknife.ButterKnife;

/**
 * Created by GHY on 2017/7/31.
 * Desc: BaseFragment
 */

public abstract class BaseFragment extends RxFragment {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutID(), container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        className = getClass().getSimpleName();
        initView();
        initData();
    }

    /***----------------Api请求Start------------------------------**/

    public void clearParams() {
        if (requestParams != null && requestParams.size() != 0) requestParams.clear();
    }

    public ApiService getAPiService() {
        return RetrofitManager.getInstance().API();
    }

    public void requestApi(String url, BaseRequestCallBack requestCallBack) {
        requestApi(url, "", requestCallBack);
    }

    public void requestApi(String url, String loadingMsg, BaseRequestCallBack requestCallBack) {
        ApiHelper.requestApi(this, requestParams, url, loadingMsg, requestCallBack);
    }

    public void requestApi(String url, String loadingMsg, String requestMethod, BaseRequestCallBack requestCallBack) {
        ApiHelper.requestApi(this, requestParams, url, loadingMsg, requestCallBack, requestMethod);
    }

    public void requestApiPostJson(String url, String jsonData, String loadingMsg, BaseRequestCallBack requestCallBack) {
        ApiHelper.requestApiPostJson(this, jsonData, url, loadingMsg, requestCallBack);
    }

    /***----------------Api请求End------------------------------**/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
