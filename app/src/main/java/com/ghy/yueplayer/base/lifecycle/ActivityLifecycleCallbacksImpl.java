package com.ghy.yueplayer.base.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;

import timber.log.Timber;


/**
 * ================================================
 * ActivityLifecycleCallbacks
 * 全局Activity生命周期监听
 * ================================================
 */
public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks, ActivityState {

    private final String TAG = "ActivityLifecycle";

    private ArrayList<Activity> mActivities = new ArrayList<>();
    private ArrayList<Activity> mResumeActivities = new ArrayList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.i(TAG + activity.getClass().getSimpleName() + " - onActivityCreated");
        mActivities.add(0, activity);

    }

    @Override
    public void onActivityStarted(Activity activity) {
        Timber.i(TAG + activity.getClass().getSimpleName() + " - onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.i(TAG + activity.getClass().getSimpleName() + " - onActivityResumed");
        if (!mResumeActivities.contains(activity)) {
            mResumeActivities.add(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.i(TAG + activity.getClass().getSimpleName() + " - onActivityPaused");
        mResumeActivities.remove(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Timber.i(TAG + activity.getClass().getSimpleName() + " - onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.i(TAG + activity.getClass().getSimpleName() + " - onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.i(TAG + activity.getClass().getSimpleName() + " - onActivityDestroyed");
        mActivities.remove(activity);
    }

    @Override
    public Activity currentActivity() {
        return mActivities.size() > 0 ? mActivities.get(0) : null;
    }

    @Override
    public int activityCount() {
        return mActivities.size();
    }

    @Override
    public boolean isFrontDesk() {
        return mResumeActivities.size() > 0;
    }

    @Override
    public boolean isActivityDestroy(Activity activity) {
        boolean isDestroy = !mActivities.contains(activity);
        if (isDestroy) {
            Timber.i(TAG + activity.getClass().getSimpleName() + " - isActivityDestroy == true");
        } else {
            Timber.i(TAG + activity.getClass().getSimpleName() + " - isActivityDestroy == false");
        }
        return isDestroy;
    }

    @Override
    public Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (isActivitiesNoEmpty()) {
            for (Activity activity : mActivities) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    @Override
    public void finishCurrentActivity() {
        if (isActivitiesNoEmpty()) {
            Activity activity = mActivities.get(0);
            activity.finish();
        }
    }

    @Override
    public void finishActivity(Class<?> cls) {
        if (isActivitiesNoEmpty()) {
            for (Activity activity : mActivities) {
                if (activity.getClass().equals(cls)) {
                    activity.finish();
                    break;
                }
            }
        }
    }

    @Override
    public void finishAllActivity() {
        if (isActivitiesNoEmpty()) {
            for (Activity activity : mActivities) {
                activity.finish();
            }
        }
    }

    @Override
    public ArrayList<Activity> getActivityList() {
        return mActivities;
    }

    private boolean isActivitiesNoEmpty() {
        return mActivities != null && !mActivities.isEmpty();
    }

}
