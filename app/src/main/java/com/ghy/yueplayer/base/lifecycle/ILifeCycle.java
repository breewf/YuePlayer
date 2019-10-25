package com.ghy.yueplayer.base.lifecycle;

/**
 * Created by HY on 2018/10/24.
 * Desc:生命周期接口
 */
public interface ILifeCycle {

    void onCreate();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onAny();
}
