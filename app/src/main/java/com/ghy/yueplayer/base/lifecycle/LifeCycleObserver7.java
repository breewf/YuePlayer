package com.ghy.yueplayer.base.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

/**
 * Created by HY on 2018/10/24.
 * Desc:LifeCycleObserver生命周期
 * java7
 */
public class LifeCycleObserver7 implements LifecycleObserver {

    private static final String TAG = LifeCycleObserver7.class.getSimpleName();

    private AndroidLifeCycle mAndroidLifeCycle;

    public LifeCycleObserver7(AndroidLifeCycle androidLifeCycle) {
        mAndroidLifeCycle = androidLifeCycle;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onCreate();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onStart();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onResume();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onPause();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onStop();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onDestroy();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onAny() {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onAny();
        }
    }
}
