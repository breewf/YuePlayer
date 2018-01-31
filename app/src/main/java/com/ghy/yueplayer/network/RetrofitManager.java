package com.ghy.yueplayer.network;

import android.util.ArrayMap;

import com.ghy.yueplayer.BuildConfig;
import com.ghy.yueplayer.api.ApiService;
import com.ghy.yueplayer.constant.HttpConfig;
import com.ghy.yueplayer.constant.URL;
import com.ghy.yueplayer.network.Interceptors.HeaderInterceptor;
import com.ghy.yueplayer.network.Interceptors.RequestInterceptor;

import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by GHY on 2018/1/6.
 * Desc:Retrofit网络请求
 */

public class RetrofitManager {

    private static volatile RetrofitManager mRetrofitManager;
    private static ApiService mAPIService;
    private static ArrayMap<String, CompositeDisposable> apiRequestManager = new ArrayMap<>();

    public static RetrofitManager getInstance() {
        if (mRetrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (mRetrofitManager == null)
                    mRetrofitManager = new RetrofitManager();
            }
        }
        return mRetrofitManager;
    }

    private RetrofitManager() {
//        SSLSocketFactory sslSocketFactory = null;
//        if (!BuildConfig.DEBUG) {//正式环境使用默认的 https 协议
//            if (HttpSUtils.getDefaultSLLContext() != null) {//设置证书（默认证书）
//                sslSocketFactory = HttpSUtils.getDefaultSLLContext().getSocketFactory();
//            }
//        }
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(HttpConfig.HTTP_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(HttpConfig.HTTP_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(HttpConfig.HTTP_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)//失败重试
                .addInterceptor(new HeaderInterceptor())//添加head信息
//                .addInterceptor(new ParametersInterceptor())//添加通用参数
                .addInterceptor(new RequestInterceptor(BuildConfig.DEBUG ?
                        RequestInterceptor.Level.ALL : RequestInterceptor.Level.NONE))//添加日志拦截器
//                .sslSocketFactory(sslSocketFactory)//此方法标记为过时
//                .sslSocketFactory(HttpSUtils.getDefaultSSLSocketFactory(), HttpSUtils.getX509TrustManager())
                .build();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(URL.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())//字符串
                .addConverterFactory(GsonConverterFactory.create())//Gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加adapter转换器
                .client(mOkHttpClient)
                .build();
        mAPIService = mRetrofit.create(ApiService.class);
    }

    /**
     * 获取ApiService实例
     *
     * @return
     */
    public ApiService API() {
        return mAPIService;
    }

    /**
     * 线程调度Observable
     */
    public static <T> ObservableTransformer<T, T> schedulersTransformer() {
        return observable ->
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 将 {@link Disposable} 添加到 {@link CompositeDisposable} 中统一管理
     * 可在BaseActivity的onDestroy()中使用 {@link #clearDisposable(String)} ()} 停止正在执行的 RxJava 任务,避免内存泄漏
     * 或者使用 {RxLifecycle} 避免内存泄漏,二选一,此方法作为备用方案
     *
     * @param key        类名,或者包名+类名
     * @param disposable
     */
    public static void addDisposable(String key, Disposable disposable) {
        if (apiRequestManager.containsKey(key)) {
            apiRequestManager.get(key).add(disposable);
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            apiRequestManager.put(key, compositeDisposable);
        }
    }

    public static void clearDisposable(String key) {
        if (apiRequestManager.containsKey(key)) {
            CompositeDisposable compositeDisposable = apiRequestManager.get(key);
            compositeDisposable.clear();
        }
    }

}

