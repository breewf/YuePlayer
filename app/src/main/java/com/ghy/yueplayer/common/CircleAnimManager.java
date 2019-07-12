package com.ghy.yueplayer.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ghy.yueplayer.common.listener.SimpleAnimationListener;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.util.ScreenUtils;
import com.ghy.yueplayer.util.Utils;

import static com.ghy.yueplayer.constant.Const.CIRCLE_ANIM_TYPE_1;
import static com.ghy.yueplayer.constant.Const.CIRCLE_ANIM_TYPE_2;
import static com.ghy.yueplayer.constant.Const.CIRCLE_ANIM_TYPE_3;

/**
 * @author HY
 * @date 2019/4/8
 * Desc:CIRCLE动画管理
 */
public class CircleAnimManager {

    private Activity mContext;

    /**
     * 主界面frameLayout
     */
    private FrameLayout mMainLayout;

    /**
     * CIRCLE View
     */
    private ImageView mCircleViewOrigin;

    /**
     * 动画CircleView
     */
    private ImageView mCircleViewAnim;

    /**
     * 屏幕宽高
     */
    private int mScreenWidth;
    private int mScreenHeight;

    /**
     * 主界面高度--不含顶部toolbar
     */
    private int mMainHeight;

    /**
     * circle半径
     */
    private int mViewAnimHalf;

    /**
     * 起始位
     */
    float x0 = 0f;
    float y0 = 0f;
    /**
     * 右下角
     */
    float x1;
    float y1;
    /**
     * 右上角
     */
    float x2;
    float y2;
    /**
     * 左上角
     */
    float x3;
    float y3;
    /**
     * 左下角
     */
    float x4;
    float y4;

    private boolean mIsCircleAnim;

    private AnimatorSet mAnimatorSetCircle;

    public CircleAnimManager(Activity context) {
        mContext = context;
        mScreenWidth = ScreenUtils.getScreenWidth(context);
        mScreenHeight = ScreenUtils.getScreenHeight(context);
    }

    public void initView(FrameLayout mainLayout, ImageView circleView, ImageView circleViewAnim) {
        mMainLayout = mainLayout;
        mCircleViewOrigin = circleView;
        mCircleViewAnim = circleViewAnim;

        mMainLayout.post(() -> mMainHeight = mMainLayout.getHeight());

        mViewAnimHalf = Utils.dip2px(35);
    }

    /**
     * 底部 Circle 动画
     *
     * @param isOnResume isOnResume
     */
    public void refreshCircleAnim(Context context, boolean isOnResume) {
        int animMode = Global.getCircleAnimType();
        // 无动画
        if (animMode <= CIRCLE_ANIM_TYPE_1) {
            setCircleAnimManager(false);
            // 重置Circle
            resetCircleStatus();
        } else {
            // 有动画
            setCircleAnimManager(isOnResume);
        }
    }

    /**
     * 重置--恢复为原始无动画状态
     */
    private void resetCircleStatus() {
        if (mCircleViewOrigin == null) {
            return;
        }
        mCircleViewOrigin.setVisibility(View.VISIBLE);
        mCircleViewAnim.setVisibility(View.GONE);
    }

    /**
     * Circle 动画管理
     *
     * @param isStart true开始动画 false停止动画
     */
    public void setCircleAnimManager(boolean isStart) {
        int animMode = Global.getCircleAnimType();
        if (isStart) {

            if (animMode != CIRCLE_ANIM_TYPE_2) {
                resetMagicCircle();
            }

            if (animMode == CIRCLE_ANIM_TYPE_1) {

            } else if (animMode == CIRCLE_ANIM_TYPE_2) {
                startCircleMagicCircle();
            }
        } else {
            if (animMode == CIRCLE_ANIM_TYPE_2) {
                stopCircleMagicCircle();
            } else if (animMode == CIRCLE_ANIM_TYPE_3) {

            }
        }
    }

    /**
     * 魔力圈圈动画--start
     */
    private void startCircleMagicCircle() {
        if (mContext == null || mCircleViewOrigin == null) {
            return;
        }
        mCircleViewOrigin.setVisibility(View.GONE);

        if (mCircleViewAnim == null) {
            return;
        }
        if (mMainHeight == 0) {
            return;
        }
        if (mIsCircleAnim) {
            return;
        }

        // 开始动画
        mCircleViewAnim.setVisibility(View.VISIBLE);

        // 右下角
        x1 = (float) mScreenWidth / 2 - mViewAnimHalf;
        y1 = 0;
        // 右上角
        x2 = (float) mScreenWidth / 2 - mViewAnimHalf;
        y2 = -(mMainHeight - mViewAnimHalf * 2);
        // 左上角
        x3 = -((float) mScreenWidth / 2 - mViewAnimHalf);
        y3 = -(mMainHeight - mViewAnimHalf * 2);
        // 左下角
        x4 = -((float) mScreenWidth / 2 - mViewAnimHalf);
        y4 = 0;

        startMagicCircleStep1(x1, y1);
        mIsCircleAnim = true;
    }

    private void startMagicCircleStep1(float x, float y) {
        if (mCircleViewAnim == null) {
            return;
        }

        if (mAnimatorSetCircle != null) {
            if (mAnimatorSetCircle.isPaused()) {
                new Handler().postDelayed(() -> mAnimatorSetCircle.resume(), 1000);
                return;
            }
        }

        // 首次执行缩放动画
        if (mAnimatorSetCircle == null) {
            fadeScaleInAnim(mCircleViewAnim);
        }

        ObjectAnimator rotation = ObjectAnimator.ofFloat(mCircleViewAnim, "rotation", 0f, 360f);
        ObjectAnimator translationXx = ObjectAnimator.ofFloat(mCircleViewAnim, "translationX", 0f, x);
        ObjectAnimator translationYy = ObjectAnimator.ofFloat(mCircleViewAnim, "translationY", 0f, y);
        mAnimatorSetCircle = new AnimatorSet();
        mAnimatorSetCircle.setInterpolator(new LinearInterpolator());
        mAnimatorSetCircle.play(rotation).with(translationXx).with(translationYy);
        mAnimatorSetCircle.setDuration(2000);
        mAnimatorSetCircle.setStartDelay(2000);
        mAnimatorSetCircle.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startMagicCircleStep2(x2, y2);
            }
        });
        mAnimatorSetCircle.start();
    }

    private void startMagicCircleStep2(float x, float y) {
        if (mCircleViewAnim == null) {
            return;
        }
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mCircleViewAnim, "rotation", 0f, 360f * 3);
        ObjectAnimator translationXx = ObjectAnimator.ofFloat(mCircleViewAnim, "translationX", x1, x2);
        ObjectAnimator translationYy = ObjectAnimator.ofFloat(mCircleViewAnim, "translationY", y1, y2);
        mAnimatorSetCircle = new AnimatorSet();
        mAnimatorSetCircle.setInterpolator(new LinearInterpolator());
        mAnimatorSetCircle.play(rotation).with(translationXx).with(translationYy);
        mAnimatorSetCircle.setDuration(5000);
        mAnimatorSetCircle.setStartDelay(0);
        mAnimatorSetCircle.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startMagicCircleStep3(x3, y3);
            }
        });
        mAnimatorSetCircle.start();
    }

    private void startMagicCircleStep3(float x, float y) {
        if (mCircleViewAnim == null) {
            return;
        }
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mCircleViewAnim, "rotation", 0f, 360f * 2);
        ObjectAnimator translationXx = ObjectAnimator.ofFloat(mCircleViewAnim, "translationX", x2, x3);
        ObjectAnimator translationYy = ObjectAnimator.ofFloat(mCircleViewAnim, "translationY", y2, y3);
        mAnimatorSetCircle = new AnimatorSet();
        mAnimatorSetCircle.setInterpolator(new LinearInterpolator());
        mAnimatorSetCircle.play(rotation).with(translationXx).with(translationYy);
        mAnimatorSetCircle.setDuration(3000);
        mAnimatorSetCircle.setStartDelay(0);
        mAnimatorSetCircle.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startMagicCircleStep4(x4, y4);
            }
        });
        mAnimatorSetCircle.start();
    }

    private void startMagicCircleStep4(float x, float y) {
        if (mCircleViewAnim == null) {
            return;
        }
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mCircleViewAnim, "rotation", 0f, 360f * 3);
        ObjectAnimator translationXx = ObjectAnimator.ofFloat(mCircleViewAnim, "translationX", x3, x4);
        ObjectAnimator translationYy = ObjectAnimator.ofFloat(mCircleViewAnim, "translationY", y3, y4);
        mAnimatorSetCircle = new AnimatorSet();
        mAnimatorSetCircle.setInterpolator(new LinearInterpolator());
        mAnimatorSetCircle.play(rotation).with(translationXx).with(translationYy);
        mAnimatorSetCircle.setDuration(5000);
        mAnimatorSetCircle.setStartDelay(0);
        mAnimatorSetCircle.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startMagicCircleStep5(0f, 0f);
            }
        });
        mAnimatorSetCircle.start();
    }

    private void startMagicCircleStep5(float x, float y) {
        if (mCircleViewAnim == null) {
            return;
        }
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mCircleViewAnim, "rotation", 0f, 360f);
        ObjectAnimator translationXx = ObjectAnimator.ofFloat(mCircleViewAnim, "translationX", x4, 0);
        ObjectAnimator translationYy = ObjectAnimator.ofFloat(mCircleViewAnim, "translationY", y4, 0);
        mAnimatorSetCircle = new AnimatorSet();
        mAnimatorSetCircle.setInterpolator(new LinearInterpolator());
        mAnimatorSetCircle.play(rotation).with(translationXx).with(translationYy);
        mAnimatorSetCircle.setDuration(2000);
        mAnimatorSetCircle.setStartDelay(0);
        mAnimatorSetCircle.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startMagicCircleStep1(x1, y1);
            }
        });
        mAnimatorSetCircle.start();
    }

    /**
     * 魔力圈圈动画--stop
     */
    private void stopCircleMagicCircle() {
        if (mAnimatorSetCircle != null) {
            mAnimatorSetCircle.pause();
        }
        mIsCircleAnim = false;
    }

    /**
     * 魔力圈圈动画--重置
     */
    private void resetMagicCircle() {
        mAnimatorSetCircle = null;
        if (mCircleViewAnim != null) {
            mCircleViewAnim.setTranslationX(0f);
            mCircleViewAnim.setTranslationY(0f);
            mCircleViewAnim.setRotation(0f);
            mCircleViewAnim.setVisibility(View.GONE);
        }
    }

    private void fadeScaleInAnim(View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0.4f, 1f);
        ObjectAnimator scanX = ObjectAnimator.ofFloat(view, "scaleX", 0.4f, 1.0f);
        ObjectAnimator scanY = ObjectAnimator.ofFloat(view, "scaleY", 0.4f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new BounceInterpolator());
        animSet.play(alpha).with(scanX).with(scanY);
        animSet.setDuration(1000);
        animSet.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animSet.start();
    }
}
