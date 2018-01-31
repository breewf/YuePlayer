package com.ghy.yueplayer.network.observer;

import android.content.Context;

import io.reactivex.disposables.Disposable;

/**
 * Created by GHY on 2018/1/6.
 * Desc:StringObserver
 */

public abstract class StringObserver<T> extends BaseObserver<T> {

    public StringObserver(Context context) {
        super(context);
    }

    public StringObserver(Context context, String loadingMsg) {
        super(context, loadingMsg, TYPE_STRING);
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    public void onNext(T t) {
        super.onNext(t);
    }
}
