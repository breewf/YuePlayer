package com.ghy.yueplayer.network.observer;

import android.content.Context;

import io.reactivex.disposables.Disposable;

/**
 * Created by GHY on 2018/1/6.
 * Desc:JSONObjectObserver,用string接收,转换成JsonObject
 */

public abstract class JSONObjectObserver<T> extends BaseObserver<T> {

    public JSONObjectObserver(Context context) {
        super(context);
    }

    public JSONObjectObserver(Context context, String loadingMsg) {
        super(context, loadingMsg, TYPE_JSON_OBJECT);
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
