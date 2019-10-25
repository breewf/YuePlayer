package com.ghy.yueplayer.base.lifecycle;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by HY on 2018/10/24.
 * Desc:Activity状态.
 **/
public interface ActivityState {

    /**
     * 获取当前Activity
     *
     * @return
     */
    Activity currentActivity();

    /**
     * 任务栈中Activity的总数
     *
     * @return
     */
    int activityCount();

    /**
     * 判断应用是否处于前台，即是否可见
     *
     * @return
     */
    boolean isFrontDesk();

    /**
     * 判断Activity是否销毁
     *
     * @return
     */
    boolean isActivityDestroy(Activity activity);

    /**
     * 获取Activity
     *
     * @return
     */
    Activity findActivity(Class<?> cls);

    /**
     * 关闭当前Activity
     *
     * @return
     */
    void finishCurrentActivity();

    /**
     * 关闭指定Activity
     *
     * @return
     */
    void finishActivity(Class<?> cls);

    /**
     * 关闭所有Activity
     *
     * @return
     */
    void finishAllActivity();

    /**
     * 获取栈内Activity
     *
     * @return
     */
    ArrayList<Activity> getActivityList();

}
