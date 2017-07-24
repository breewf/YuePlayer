package com.ghy.yueplayer;

import android.app.Application;

import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.network.NoHttpUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by GHY on 2015/8/7.
 */
public class PlayerApplication extends Application {

    /**
     * 全局application实例
     */
    private static PlayerApplication mInstance = null;

    public static PlayerApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initImageLoader();
        PreferManager.init(this);
        NoHttpUtils.initNoHttp(this);
    }

    private void initImageLoader() {
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }

}
