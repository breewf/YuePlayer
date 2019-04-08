package com.ghy.yueplayer.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.ghy.yueplayer.common.listener.SimpleAnimationListener;

/**
 * @author HY
 * @date 2019/4/8
 * Desc:CIRCLE动画管理
 */
public class CircleAnimManager {

    private Activity mContext;

    /**
     * CIRCLE View
     */
    private ImageView mCircleView;


    /**
     * 呼吸动画
     */
    private AnimatorSet mAnimSetBreath;

    public CircleAnimManager(Activity context) {
        mContext = context;
    }

    public void initView(ImageView circleView) {
        mCircleView = circleView;
    }

    /**
     * 底部 Circle 动画
     *
     * @param isOnResume isOnResume
     */
    public void refreshCircleAnim(Context context, boolean isOnResume) {
//        int animMode = Global.getCircleAnimType();
//        // 无动画
//        if (animMode <= 1) {
//            setCircleAnimManager(false);
//            // 重置Circle
//            resetCircleStatus();
//        } else {
//            // 有动画
//            setCircleAnimManager(isOnResume);
//        }
    }

    private void resetCircleStatus() {
        if (mCircleView == null) {
            return;
        }
//        if (mCircleView.getTranslationX() != 0 || mCircleView.getTranslationY() != 0) {
//            mCircleView.setTranslationX(0);
//            mCircleView.setTranslationY(0);
//        }
//        if (mCircleView.getScaleX() != 1.0f || mCircleView.getScaleY() != 1.0f) {
//            mCircleView.setScaleX(1.0f);
//            mCircleView.setScaleY(1.0f);
//        }
    }

    /**
     * Circle 动画管理
     *
     * @param isStart true开始动画 false停止动画
     */
    public void setCircleAnimManager(boolean isStart) {
//        int animMode = Global.getCircleAnimType();
//        if (isStart) {
//            if (animMode == 3) {
//
//            } else if (animMode == 2) {
//                startCircleBreathAnim();
//            }
//        } else {
//            if (animMode == 3) {
//
//            } else if (animMode == 2) {
//                stopCircleBreathAnim();
//            }
//        }
    }

    /**
     * 开始呼吸动画
     */
    private void startCircleBreathAnim() {
        if (mCircleView == null) {
            return;
        }
        if (mAnimSetBreath == null) {
            ObjectAnimator scanX = ObjectAnimator.ofFloat(mCircleView, "scaleX", 1.0f, 0.84f, 1.0f, 0.90f, 1.0f);
            ObjectAnimator scanY = ObjectAnimator.ofFloat(mCircleView, "scaleY", 1.0f, 0.84f, 1.0f, 0.90f, 1.0f);
            mAnimSetBreath = new AnimatorSet();
            mAnimSetBreath.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimSetBreath.play(scanX).with(scanY);
            mAnimSetBreath.setDuration(5000);
            mAnimSetBreath.setStartDelay(5000);
            mAnimSetBreath.addListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startCircleBreathAnim();
                }
            });
        }
        if (mAnimSetBreath.isPaused()) {
            mAnimSetBreath.resume();
        } else {
            if (!mAnimSetBreath.isRunning()) {
                mAnimSetBreath.start();
            }
        }
    }

    private void stopCircleBreathAnim() {
        if (mAnimSetBreath != null) {
            mAnimSetBreath.pause();
        }
    }
}
