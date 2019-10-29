package com.ghy.yueplayer.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.common.listener.SimpleAnimationListener;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.main.ImageLoader;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.utils.ScreenUtils;
import com.ghy.yueplayer.utils.Utils;
import com.ghy.yueplayer.view.CircleImageView;

import java.util.ArrayList;
import java.util.Random;

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
    private int mScreenWidthHalf;
    private int mScreenHeightHalf;

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

    private int mCircleLength;

    private ArrayList<View> mCircleList = new ArrayList<>();

    private String mMusicAlbumUri;

    private int mCount;

    public CircleAnimManager(Activity context) {
        mContext = context;
        mScreenWidth = ScreenUtils.getScreenWidth(context);
        mScreenHeight = ScreenUtils.getScreenHeight(context);
        mScreenWidthHalf = mScreenWidth / 2;
        mScreenHeightHalf = mScreenHeight / 2;
        mCircleLength = Utils.dip2px(40);
    }

    public void initView(FrameLayout mainLayout, ImageView circleView, ImageView circleViewAnim) {
        mMainLayout = mainLayout;
        mCircleViewOrigin = circleView;
        mCircleViewAnim = circleViewAnim;

        mMainLayout.post(() -> mMainHeight = mMainLayout.getHeight());

        mViewAnimHalf = Utils.dip2px(35);
    }

    private View.OnClickListener mCircleClickListener = v -> createOneCircleStartAnim(true);

    private View.OnLongClickListener mCircleLongClickListener = v -> {
        mCount = 0;
        delayCreateOneCircleAnim();
        return true;
    };

    public void setImage(String musicAlbumUri) {
        mMusicAlbumUri = musicAlbumUri;
        if (mCircleViewAnim != null) {
            ImageLoader.getInstance().loadImage(mCircleViewAnim, musicAlbumUri);
        }
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
            // 重置Circle
            resetCircleStatus();
        } else {
            // 有动画
            if (!isOnResume) {
                setCircleAnimManager(false);
            } else {
                if (MusicPlayService.MPS.isPlay()) {
                    setCircleAnimManager(true);
                } else {
                    setCircleAnimManager(false);
                }
            }
        }
    }

    /**
     * 重置--恢复为原始无动画状态
     */
    private void resetCircleStatus() {
        if (mCircleViewOrigin != null) {
            mCircleViewOrigin.setVisibility(View.VISIBLE);
        }
        if (mCircleViewAnim != null) {
            mCircleViewAnim.setVisibility(View.GONE);
        }

        stopCircleMagicCircle();
        stopCircleAnim3();
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

            if (animMode != CIRCLE_ANIM_TYPE_3) {
                mCircleViewAnim.setOnClickListener(null);
                mCircleViewAnim.setOnLongClickListener(null);
            } else {
                mCircleViewAnim.setOnClickListener(mCircleClickListener);
                mCircleViewAnim.setOnLongClickListener(mCircleLongClickListener);
            }

            if (animMode == CIRCLE_ANIM_TYPE_2) {
                startCircleMagicCircle();
            } else if (animMode == CIRCLE_ANIM_TYPE_3) {
                startCircleAnim3();
            }
        } else {
            if (animMode == CIRCLE_ANIM_TYPE_2) {
                stopCircleMagicCircle();
            } else if (animMode == CIRCLE_ANIM_TYPE_3) {
                stopCircleAnim3();
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
        mAnimatorSetCircle.setDuration(1000);
        mAnimatorSetCircle.setStartDelay(1500);
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
        mAnimatorSetCircle.setDuration(3000);
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
        mAnimatorSetCircle.setDuration(2000);
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
        mAnimatorSetCircle.setDuration(3000);
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
        mAnimatorSetCircle.setDuration(1500);
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
        if (view == null) {
            return;
        }
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0.4f, 1f);
        ObjectAnimator scanXx = ObjectAnimator.ofFloat(view, "scaleX", 0.4f, 1.0f);
        ObjectAnimator scanYy = ObjectAnimator.ofFloat(view, "scaleY", 0.4f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new BounceInterpolator());
        animSet.play(alpha).with(scanXx).with(scanYy);
        animSet.setDuration(1000);
        animSet.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animSet.start();
    }

    private void scaleInAnim(View view) {
        if (view == null) {
            return;
        }
        ObjectAnimator scanXx = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.7f, 1.0f);
        ObjectAnimator scanYy = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.7f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.play(scanXx).with(scanYy);
        animSet.setDuration(500);
        animSet.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animSet.start();
    }

    private void startCircleAnim3() {
        if (mCircleViewOrigin != null) {
            mCircleViewOrigin.setVisibility(View.GONE);
        }
        if (mCircleViewAnim != null) {
            mCircleViewAnim.setVisibility(View.VISIBLE);
            fadeScaleInAnim(mCircleViewAnim);
        }

    }

    private void stopCircleAnim3() {
        if (mMainLayout == null) {
            return;
        }
        mMainLayout.postDelayed(() -> {
            if (mMainLayout != null && mCircleList.size() > 0) {
                for (int i = 0; i < mCircleList.size(); i++) {
                    mMainLayout.removeView(mCircleList.get(i));
                }
            }
            mCircleList.clear();
        }, 600);
    }

    private void delayCreateOneCircleAnim() {
        if (mCount == 25) {
            return;
        }
        new Handler().postDelayed(() -> {
            createOneCircleStartAnim(false);
            mCount++;
            delayCreateOneCircleAnim();
        }, 100);
    }

    /**
     * 创建并发射一个圆圈
     */
    private void createOneCircleStartAnim(boolean showScaleAnim) {
        if (showScaleAnim) {
            scaleInAnim(mCircleViewAnim);
        }

        CircleImageView circleImageView = (CircleImageView) LayoutInflater.from(mContext).inflate(R.layout.layout_circle_image, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mCircleLength, mCircleLength);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        ImageLoader.getInstance().loadImage(circleImageView, mMusicAlbumUri);
        circleImageView.setLayoutParams(params);
        mMainLayout.addView(circleImageView);
        mCircleList.add(circleImageView);

        startCirclePointAnim(circleImageView);
    }

    private void startCirclePointAnim(View imageView) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int[] randomArray = {0, 1};
//                    int point1x;
                    int point1y;
                    int point2x;
                    int point2y;
//                    if (randomArray[new Random().nextInt(2)] == 0) {
//                        point1x = new Random().nextInt(mScreenWidthHalf);
//                    } else {
//                        point1x = new Random().nextInt(mScreenHeightHalf * 2);
//                    }
                    if (randomArray[new Random().nextInt(2)] == 0) {
                        point2x = new Random().nextInt(mScreenWidthHalf);
                    } else {
                        point2x = new Random().nextInt(mScreenWidthHalf * 2);
                    }
                    point1y = new Random().nextInt(mScreenHeightHalf * 2);
                    point2y = -new Random().nextInt(point1y) + point1y;
                    int endXx = new Random().nextInt(mScreenWidth * 2) - mScreenWidthHalf;
                    int endYy = -mScreenHeight;

                    ValueAnimator translateAnimator = ValueAnimator.ofObject(
                            new CircleEvaluator(new PointF(imageView.getX(), imageView.getY()), new PointF(point2x, point2y)),
                            new PointF(imageView.getX(), imageView.getY()),
                            new PointF(endXx, endYy));
                    translateAnimator.addUpdateListener(animation -> {
                        PointF pointFf = (PointF) animation.getAnimatedValue();
                        imageView.setX(pointFf.x);
                        imageView.setY(pointFf.y);
                    });
                    translateAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (mMainLayout != null) {
                                mMainLayout.removeView(imageView);
                            }
                        }
                    });

                    translateAnimator.setDuration(3000);
                    TimeInterpolator[] timeInterpolator = {new AccelerateInterpolator(), new AccelerateDecelerateInterpolator(),
                            new DecelerateInterpolator(), new LinearOutSlowInInterpolator(), new FastOutLinearInInterpolator(),
                            new FastOutSlowInInterpolator()};
                    translateAnimator.setInterpolator(timeInterpolator[new Random().nextInt(timeInterpolator.length)]);
                    translateAnimator.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class CircleEvaluator implements TypeEvaluator<PointF> {

        //贝塞尔曲线参考点1
        PointF f1;
        //贝塞尔曲线参考点2
        PointF f2;

        CircleEvaluator(PointF f1, PointF f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            float leftTime = 1f - fraction;
            PointF newPointFf = new PointF();
            newPointFf.x = startValue.x * leftTime * leftTime * leftTime
                    + f1.x * 3 * leftTime * leftTime * fraction
                    + f2.x * 3 * leftTime * fraction * fraction
                    + endValue.x * fraction * fraction * fraction;
            newPointFf.y = startValue.y * leftTime * leftTime * leftTime
                    + f1.y * 3 * leftTime * leftTime * fraction
                    + f2.y * 3 * leftTime * fraction * fraction
                    + endValue.y * fraction * fraction * fraction;
            return newPointFf;
        }
    }
}
