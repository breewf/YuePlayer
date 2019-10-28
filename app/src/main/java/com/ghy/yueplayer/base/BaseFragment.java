package com.ghy.yueplayer.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghy.yueplayer.api.ApiHelper;
import com.ghy.yueplayer.api.ApiService;
import com.ghy.yueplayer.common.event.Actions;
import com.ghy.yueplayer.common.event.Event;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.network.BaseRequestCallBack;
import com.ghy.yueplayer.network.RetrofitManager;
import com.ghy.yueplayer.utils.ViewUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashMap;

import butterknife.ButterKnife;

/**
 * @author GHY
 * @date 2017/7/31
 * Desc: BaseFragment
 */
public abstract class BaseFragment extends RxFragment {

    protected LinkedHashMap<String, Object> requestParams = new LinkedHashMap<>();
    protected String className;

    protected ImmersionBar mImmersionBar;

    /**
     * 获取布局ID
     *
     * @return
     */
    protected abstract int getLayoutId();

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initImmersionBar();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        className = getClass().getSimpleName();
    }

    @Subscribe
    public void onEvent(Event event) {
        // DarkMode
        if (Actions.ACTION_DARK_MODE_CHANGE.equals(event.getAction())) {
            onDarkModeChange(Global.DAY_MODE);
        }
    }

    /**
     * 是否在Fragment使用沉浸式
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        if (isImmersionBarEnabled()) {
            mImmersionBar = ImmersionBar.with(this);
            mImmersionBar.navigationBarColor(ViewUtils.getNavigationBarColorRes())
                    .navigationBarDarkIcon(Global.DAY_MODE)
                    .init();
        }
    }

    /***----------------Api请求Start------------------------------**/

    public void clearParams() {
        if (requestParams != null && requestParams.size() != 0) {
            requestParams.clear();
        }
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * DarkMode模式发生改变
     *
     * @param isDayMode 是否是日间模式
     */
    public void onDarkModeChange(boolean isDayMode) {

    }
}
