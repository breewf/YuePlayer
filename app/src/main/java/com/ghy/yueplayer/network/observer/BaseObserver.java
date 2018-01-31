package com.ghy.yueplayer.network.observer;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.constant.Code;
import com.ghy.yueplayer.constant.HttpConfig;
import com.ghy.yueplayer.network.BaseEntity;
import com.ghy.yueplayer.util.NetworkUtils;
import com.ghy.yueplayer.util.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by GHY on 2018/1/6.
 * Desc:Observer基类，处理数据
 */

public abstract class BaseObserver<T> implements Observer<T> {

    static final int TYPE_STRING = 1;//string
    static final int TYPE_JSON_OBJECT = 2;//JSONObject

    private int mStringType;//string类型
    private Context mContext;
    private ProgressDialog mDialog;
    private Disposable mDisposable;
    private String mLoadingMsg;

    public BaseObserver(Context context) {
        mContext = context;
        initLoadingDialog();
    }

    public BaseObserver(Context context, String loadingMsg) {
        mContext = context;
        mLoadingMsg = loadingMsg;
        initLoadingDialog();
    }

    public BaseObserver(Context context, String loadingMsg, int stringType) {
        mContext = context;
        mLoadingMsg = loadingMsg;
        mStringType = stringType;
        initLoadingDialog();
    }

    private void initLoadingDialog() {
        if (mDialog == null) mDialog = new ProgressDialog(mContext);
        mDialog.setOnCancelListener(dialog1 -> mDisposable.dispose());
    }

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        onRequestStart();
    }

    private void onRequestStart() {
        Timber.tag(HttpConfig.API_LOG_TAG).i("requestApi--onRequestStart-->>请求开始!");
        Timber.tag(HttpConfig.API_LOG_TAG).i("\n"
                + "---------------------****---------------------" + "\n"
                + "请求开始时间-->>" + TimeUtils.getTimeStrHMSSSS(System.currentTimeMillis())
                + "\n");

        if (!NetworkUtils.isConnected(mContext)) {
            Toast.makeText(mContext, R.string.net_work_disable, Toast.LENGTH_SHORT).show();

            onComplete();
        }

        showDialog();

    }

    private void showDialog() {
        if (mLoadingMsg == null) return;
        mDialog.show();
    }

    private void dismissDialog() {
        mDialog.dismiss();
    }

    @Override
    public void onNext(T t) {
        if (t instanceof String) {
            if (mStringType == TYPE_STRING) {
                Log.i(HttpConfig.API_LOG_TAG, "requestApi--onNext-->>接收String-->>success!");
                onSuccess(t);
            } else {
                try {
                    JSONObject jData = new JSONObject((String) t);
                    String msg = jData.optString(HttpConfig.RESULT_MSG);//msg
                    int code = jData.optInt(HttpConfig.RESULT_CODE, -1);//code
                    Log.i(HttpConfig.API_LOG_TAG, "requestApi--onNext-->>接收JSONObject-->>success!");
                    onSuccess(t);
//                    if (code == Code.SUCCESS) {
//                        onSuccess(t);
//                    } else {
//                        onError(code, msg);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(HttpConfig.API_LOG_TAG, "requestApi--onNext-->>接收JSONObject-->>Exception!");
                }
            }
        } else if (t instanceof BaseEntity) {
            Log.i(HttpConfig.API_LOG_TAG, "requestApi--onNext-->>接收Entity-->>success!");
            onSuccess(t);
//            if (((BaseEntity) t).getCode() == Code.SUCCESS) {
//                onSuccess(t);
//            } else {
//                onError(((BaseEntity) t).getCode(), ((BaseEntity) t).getMessage());
//            }
        } else {
            Log.i(HttpConfig.API_LOG_TAG, "requestApi--onNext-->>接收Entity-->>success!");
            onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(HttpConfig.API_LOG_TAG, "error:" + e.toString());
        dismissDialog();
        if (e instanceof TimeoutException) {
            onError(Code.ERROR_SYS_TIMEOUT, "抱歉，连接超时");
        } else if (e instanceof ConnectException) {
            onError(Code.ERROR_SYS_CONNECT_EXCEPTION, "抱歉，连接异常");
        } else if (e instanceof NetworkErrorException) {
            onError(Code.ERROR_SYS_NET_ERROR, "抱歉，网络错误");
        } else if (e instanceof UnknownHostException) {
            onError(Code.ERROR_SYS_HOST_ERROR, "抱歉，主机异常");
        } else {
            onError(Code.ERROR_SYS_UNKNOWN_ERROR, "抱歉，请求出错");
        }

//        if (e instanceof ExceptionHandle.ResponseThrowable) {
//            onError(e);
//        } else {
//            onError(new ExceptionHandle.ResponseThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
//        }

    }

    @Override
    public void onComplete() {
        Log.i(HttpConfig.API_LOG_TAG, "requestApi--onComplete!");
        dismissDialog();
    }

    protected abstract void onSuccess(T t);

    protected abstract void onError(int code, String message);

}
