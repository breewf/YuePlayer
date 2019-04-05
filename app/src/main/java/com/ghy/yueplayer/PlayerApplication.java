package com.ghy.yueplayer;

import android.app.Application;

import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.constant.Global;
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

        initGlobalConfig();
    }

    private void initGlobalConfig() {
        Global.setYueAnimType(PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1));
    }

    private void initImageLoader() {
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }

}
