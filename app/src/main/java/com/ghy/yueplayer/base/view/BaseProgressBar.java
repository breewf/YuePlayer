package com.ghy.yueplayer.base.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseProgressBar
 */
public class BaseProgressBar extends ProgressBar {

    private final String TAG = "BaseProgressBar";

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final Interpolator INTERPOLATOR2 = new LinearOutSlowInInterpolator();
    private static final Interpolator INTERPOLATOR3 = new FastOutLinearInInterpolator();

    /**
     * 是否使用自定义的进度加载动画
     * 默认使用
     */
    private boolean mCustomProgressAnim = true;

    /**
     * 开始和目标progress
     * 用于执行ValueAnimator
     */
    private int mStartProgress;
    private int mTargetProgress;

    private boolean mIsProgressAnim;
    private List<Integer> mProgressList = new ArrayList<>();

    public BaseProgressBar(Context context) {
        super(context);
    }

    public BaseProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCustomProgressAnim(boolean customProgressAnim) {
        mCustomProgressAnim = customProgressAnim;
    }

    protected void init(Context context, AttributeSet attrs) {

    }

    @Override
    public synchronized void setProgress(int progress) {
        if (mCustomProgressAnim) {
            loadCustomProgressAnim(progress);
            return;
        }
        super.setProgress(progress);
        if (progress == 100) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setProgress(int progress, boolean animate) {
        super.setProgress(progress, animate);
    }

    /**
     * 自定义加载进度动画
     *
     * @param progress progress
     */
    private void loadCustomProgressAnim(int progress) {
        int fakeProgress = getFakeProgress(progress);
        if (fakeProgress != 0 && fakeProgress <= mTargetProgress) {
            return;
        }

        mProgressList.add(fakeProgress);

        if (mIsProgressAnim) {
            return;
        }
        startProgressAnim();
    }

    private void startProgressAnim() {
        if (mProgressList.size() == 0) {
            return;
        }
        // 取出最后一个值，即最大的值
        mTargetProgress = mProgressList.get(mProgressList.size() - 1);
        if (mStartProgress == mTargetProgress) {
            return;
        }
        if (mStartProgress == 0 && mTargetProgress == 100) {
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(mStartProgress, mTargetProgress);
        int scale = Math.abs((mTargetProgress - mStartProgress)) / 35;
        int ratio = scale <= 0 ? 1 : scale;
        valueAnimator.setDuration(300 * ratio);
        valueAnimator.setInterpolator(INTERPOLATOR3);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            super.setProgress(value);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (getAlpha() == 0f) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(BaseProgressBar.this,
                            "alpha", 0f, 1f);
                    animator.setDuration(1000);
                    animator.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mStartProgress = mTargetProgress;
                mIsProgressAnim = false;
                if (mTargetProgress == 100) {
                    mProgressList.clear();
                    mStartProgress = 0;
                    mTargetProgress = 0;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(BaseProgressBar.this,
                            "alpha", 1f, 0f);
                    animator.setDuration(400);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
                    animator.start();
                } else {
                    startProgressAnim();
                }
            }
        });
        valueAnimator.start();
        mIsProgressAnim = true;
    }

    /**
     * 获取一个假的进度
     *
     * @param progress progress
     * @return 20-40-60-80-90-100
     * 当真正的进度为100时才返回100
     */
    private int getFakeProgress(int progress) {
        if (progress < 80) {
            return (progress / 20 + 1) * 20;
        }
        if (progress != 100) {
            return 90;
        }
        return progress;
    }
}
