package com.ghy.yueplayer.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ObjectUtils;
import com.ghy.yueplayer.PlayerApplication;
import com.ghy.yueplayer.base.lifecycle.ActivityLifecycleCallbacksImpl;
import com.ghy.yueplayer.common.event.Actions;
import com.ghy.yueplayer.common.event.Event;
import com.ghy.yueplayer.common.iface.IDarkMode;
import com.ghy.yueplayer.constant.Arguments;
import com.ghy.yueplayer.constant.Global;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * @author HY
 * @date 2019-07-12
 * Desc:DarkMode管理
 */
public class DarkModeManager {

    /**
     * 过渡页面截图bitmap
     */
    private Bitmap mDarkModeBitmap;

    private DarkModeManager() {

    }

    public static DarkModeManager getInstance() {
        return DarkModeHolder.instance;
    }

    private static class DarkModeHolder {
        private static DarkModeManager instance = new DarkModeManager();
    }

    /**
     * 获取模式
     *
     * @see DarkModeConfig
     */
    public int getDarkMode() {
        return PreferManager.getDarkModeConfig();
    }

    /**
     * 是否是日间模式
     *
     * @return true:是
     */
    public boolean isDayMode() {
        return PreferManager.getDarkModeConfig() == DarkModeConfig.DARK_MODE_DAY;
    }

    /**
     * 是否是夜间模式
     *
     * @return true:是
     */
    public boolean isNightMode() {
        return PreferManager.getDarkModeConfig() == DarkModeConfig.DARK_MODE_NIGHT;
    }

    /**
     * 是否是跟随系统
     *
     * @return true:是
     */
    public boolean isSystemMode() {
        return PreferManager.getDarkModeConfig() == DarkModeConfig.DARK_MODE_SYSTEM;
    }

    /**
     * 设置darkMode
     *
     * @param darkMode darkMode
     * @see DarkModeConfig
     */
    public void setDarkMode(int darkMode) {
        PreferManager.setDarkModeConfig(darkMode);
        setAppDarkMode();

        Bundle bundle = new Bundle();
        bundle.putInt(Arguments.ARG_TYPE, getDarkMode());
        Event event = new Event(Actions.ACTION_DARK_MODE_CHANGE, bundle);
        EventBus.getDefault().post(event);
    }

    /**
     * 设置App-darkMode
     *
     * @see DarkModeConfig
     */
    public void setAppDarkMode() {
        Global.DAY_MODE = isDayMode();
        Timber.i(DarkModeConfig.TAG + "AppDarkMode-->>isDayMode()-->>" + Global.DAY_MODE);
    }

    /**
     * 设置截图
     *
     * @param bitmap bitmap
     */
    public void setDarkModeBitmap(Bitmap bitmap) {
        mDarkModeBitmap = bitmap;
    }

    /**
     * 获取截图
     */
    public Bitmap getDarkModeBitmap() {
        return mDarkModeBitmap;
    }

    /**
     * DarkMode切换
     */
    public void publishDarkModeEvent() {
        Timber.i("%sDarkMode切换模式-->>DarkModeManager--publishDarkModeEvent-->>", DarkModeConfig.TAG);

        ActivityLifecycleCallbacksImpl lifecycleCallbacks = PlayerApplication.getInstance().mLifecycleCallbacks;
        if (lifecycleCallbacks == null) {
            return;
        }
        ArrayList<Activity> mActivities = lifecycleCallbacks.getActivityList();
        if (ObjectUtils.isEmpty(mActivities)) {
            return;
        }
        for (int i = 0; i < mActivities.size(); i++) {
//            if (mActivities.get(i) instanceof BrightnessActivity) {
//                BrightnessActivity brightnessActivity = ((BrightnessActivity) mActivities.get(i));
//                View decorView = brightnessActivity.getWindow().getDecorView();
//                Bitmap bitmap = ShareScreenshotUtils.getBitmapByView(decorView);
//                ImageView imageView = brightnessActivity.getDarkModeIv();
//                darkModeImageAnim(bitmap, imageView);
//                brightnessActivity.setImmersionBar();
//            }
            traversingAllViews(mActivities.get(i).getWindow().getDecorView());

            // 此处不再分发给Activity(原因是Activity还要再次分发给Fragment),由EventBus在基类统一分发
//            if (mActivities.get(i) instanceof BaseActivity) {
//                ((BaseActivity) mActivities.get(i)).onDarkModeChange(Global.DAY_MODE);
//            }
        }
    }

    private void darkModeImageAnim(Bitmap bitmap, ImageView imageView) {
        if (bitmap == null || imageView == null) {
            return;
        }
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imageView.setImageBitmap(null);
                imageView.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    /**
     * 遍历所有view调用darkModeChange方法
     *
     * @param rootView rootView
     */
    public void traversingAllViews(View rootView) {
        if (rootView == null) {
            return;
        }
        boolean isDayMode = Global.DAY_MODE;
        List<View> listAllViews = getAllViews(rootView);
        for (int i = 0; i < listAllViews.size(); i++) {
            if (listAllViews.get(i) instanceof IDarkMode) {
                ((IDarkMode) listAllViews.get(i)).darkModeChange(isDayMode);
            }
        }
    }

    /**
     * 遍历所有view
     *
     * @param view view
     * @return
     */
    private List<View> getAllViews(View view) {
        List<View> allViews = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewChild = vp.getChildAt(i);
                allViews.add(viewChild);
                allViews.addAll(getAllViews(viewChild));
            }
        }
        return allViews;
    }

}
