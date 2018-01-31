package com.ghy.yueplayer.network.observer;

import android.content.Context;

import io.reactivex.disposables.Disposable;

/**
 * Created by GHY on 2018/1/6.
 * Desc:EntityObserverNoBase
 */

public abstract class EntityObserverNoBase<T> extends BaseObserver<T> {

    public EntityObserverNoBase(Context context) {
        super(context);
    }

    public EntityObserverNoBase(Context context, String loadingMsg) {
        super(context, loadingMsg);
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    public void onNext(T t) {
        super.onNext(t);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onSuccess(T t) {
        requestCallback(
                t, "请求成功", 100000, true);
    }

    @Override
    protected void onError(int code, String message) {
        requestCallback(
                null, message, code, false);
    }

    public abstract void requestCallback(T t, String msg, int code, boolean success);

}
