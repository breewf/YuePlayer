package com.ghy.yueplayer.base.lifecycle;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

/**
 * Created by HY on 2018/10/24.
 * Desc:LifeCycleObserver生命周期
 * java8之后
 */
public class LifeCycleObserver implements DefaultLifecycleObserver {

    private static final String TAG = LifeCycleObserver.class.getSimpleName();

    private AndroidLifeCycle mAndroidLifeCycle;

    public LifeCycleObserver(AndroidLifeCycle androidLifeCycle) {
        mAndroidLifeCycle = androidLifeCycle;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onCreate();
        }
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onStart();
        }
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onResume();
        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onPause();
        }
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onStop();
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (mAndroidLifeCycle != null) {
            mAndroidLifeCycle.onDestroy();
        }
    }
}
