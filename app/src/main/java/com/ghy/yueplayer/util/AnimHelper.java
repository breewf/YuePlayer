package com.ghy.yueplayer.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.ghy.yueplayer.common.listener.SimpleAnimationListener;


/**
 * @author HY
 * @date 2018/10/29
 * ClassDesc:动画助手类.
 **/
public class AnimHelper {

    /**
     * 淡入
     */
    public static void fadeIn(View view) {
        fadeIn(view, 400, 0);
    }

    /**
     * 淡入
     */
    public static void fadeIn(View view, long duration) {
        fadeIn(view, duration, 0);
    }

    /**
     * 淡入
     */
    public static void fadeIn(View view, long duration, long delay) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.start();
    }

    /**
     * 淡出
     */
    public static void fadeOut(View view) {
        fadeOut(view, 400, 0);
    }

    /**
     * 淡出
     */
    public static void fadeOut(View view, long duration) {
        fadeOut(view, duration, 0);
    }

    /**
     * 淡出
     */
    public static void fadeOut(View view, long duration, long delay) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.start();
    }

    /**
     * 平移X轴
     */
    public static void translationX(View view, long duration, float fromX, float endX) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", fromX, endX);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * 平移Y轴
     */
    public static void translationY(View view, long duration, float fromY, float endY) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", fromY, endY);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * 平移Y轴
     */
    public static void translationY(View view, long duration, long delay, float fromY, float endY) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", fromY, endY);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.start();
    }

    /**
     * loading旋转
     *
     * @param view 旋转的view
     */
    public static void loadingRotation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * View抖动动画
     *
     * @param view
     */
    public static void shakeAnim(View view) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(4));
        translateAnimation.setDuration(400);
        view.startAnimation(translateAnimation);
    }

    /**
     * 向下arrow箭头向上旋转动画
     *
     * @param view
     */
    public static void arrowUpAnim(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 180f);
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    /**
     * 向上arrow箭头向下旋转动画
     *
     * @param view
     */
    public static void arrowDownAnim(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 180f, 0f);
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    /**
     * 旋转
     *
     * @param view       旋转的view
     * @param angleValue 要旋转的角度
     * @param duration   时长
     */
    public static void rotationAngle(View view, float angleValue, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, angleValue);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * 旋转
     *
     * @param view     旋转的view
     * @param duration 时长
     */
    public static void rotationAngle(View view, float angleFromValue, float angleToValue, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", angleFromValue, angleToValue);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * 刷新旋转
     *
     * @param view 旋转的view
     */
    public static void webRefreshRotation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        animator.setDuration(1200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    /**
     * View抖动动画
     *
     * @param view
     */
    public static void shakeAnimCycle(View view) {
        Animation translateAnimation = new TranslateAnimation(0, 12, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(2.0f));
        translateAnimation.setDuration(300);
        view.startAnimation(translateAnimation);
    }

    /**
     * 通知铃铛左右摇摆动画
     *
     * @param view
     */
    public static void notifyTransPointAnim(View view) {
        new Handler().postDelayed(() -> {
            view.setPivotX(view.getWidth() / 2);
            view.setPivotY(0f);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", -14f, 0f, 14f, 0f, -14f, 0f, 10f, 0f, -6f, 0f);
            rotation.setInterpolator(new LinearInterpolator());
            rotation.setDuration(600);
            rotation.setRepeatCount(0);
            rotation.start();
            notifyTransPointAnim(view);
        }, 5000);
    }

    /**
     * 点击缩放动画
     *
     * @param view
     */
    public static void scaleXYAnim(View view) {
        ObjectAnimator scanX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.85f, 1.0f);
        ObjectAnimator scanY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.85f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.play(scanX).with(scanY);
        animSet.setDuration(400);
        animSet.start();
    }

    /**
     * icon缩放动画
     *
     * @param view
     */
    public static void breathIconScanAnim(View view) {
        ObjectAnimator scanX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.88f, 1.0f, 0.92f, 1.0f);
        ObjectAnimator scanY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.88f, 1.0f, 0.92f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.play(scanX).with(scanY);
        animSet.setDuration(5000);
        animSet.setStartDelay(5000);
        animSet.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animSet.start();
            }
        });
        animSet.start();
    }

}
