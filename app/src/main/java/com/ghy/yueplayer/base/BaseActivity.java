package com.ghy.yueplayer.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.api.ApiHelper;
import com.ghy.yueplayer.api.ApiService;
import com.ghy.yueplayer.common.event.Actions;
import com.ghy.yueplayer.common.event.Event;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.network.BaseRequestCallBack;
import com.ghy.yueplayer.network.RetrofitManager;
import com.ghy.yueplayer.utils.ViewUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedHashMap;

import butterknife.ButterKnife;

/**
 * @author GHY
 * @date 2017/7/31
 * Desc: BaseActivity
 */
public abstract class BaseActivity extends RxFragmentActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View rootView = getLayoutInflater().inflate(getLayoutId(), null);
        setContentView(rootView);
        className = getClass().getSimpleName();
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        // 初始化沉浸式
        initImmersionBar();
        initView();
        initData();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(Event event) {
        // DarkMode
        if (Actions.ACTION_DARK_MODE_CHANGE.equals(event.getAction())) {
            onDarkModeChange(Global.DAY_MODE);
        }
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    protected void initImmersionBar() {
        if (isImmersionBarEnabled()) {
            mImmersionBar = ImmersionBar.with(this)
                    .fitsSystemWindows(true)
                    .transparentNavigationBar()
                    .transparentBar()
                    .statusBarColor(ViewUtils.getStatusBarColorRes())
                    .statusBarDarkFont(Global.DAY_MODE, 0.2F)
                    .navigationBarColor(ViewUtils.getNavigationBarColorRes())
                    .navigationBarDarkIcon(Global.DAY_MODE)
                    .fullScreen(false)
                    .keyboardEnable(true)
                    .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mImmersionBar.init();
        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    protected void backWithTitle(String title) {
        ImageView iv = findViewById(R.id.iv_back);
        TextView tv = findViewById(R.id.tv_title);
        if (tv != null && !TextUtils.isEmpty(title)) {
            tv.setText(title);
        }
        if (iv != null) {
            iv.setOnClickListener(view -> finish());
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
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * DarkMode模式发生改变
     *
     * @param isDayMode 是否是日间模式
     */
    public void onDarkModeChange(boolean isDayMode) {

    }

}
