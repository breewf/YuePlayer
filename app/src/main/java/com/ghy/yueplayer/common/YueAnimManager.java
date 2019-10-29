package com.ghy.yueplayer.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.base.view.DnHeroTextView;
import com.ghy.yueplayer.common.listener.SimpleAnimationListener;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.helper.AnimHelper;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.utils.AppUtils;
import com.ghy.yueplayer.utils.ViewUtils;
import com.ghy.yueplayer.view.HeroTextView;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ghy.yueplayer.constant.Const.YUE_ANIM_TYPE_1;
import static com.ghy.yueplayer.constant.Const.YUE_ANIM_TYPE_2;
import static com.ghy.yueplayer.constant.Const.YUE_ANIM_TYPE_3;

/**
 * @author HY
 * @date 2019/4/8
 * Desc:YUE动画管理
 */
public class YueAnimManager {

    private Activity mContext;

    /**
     * YUE动画Layout
     */
    private FrameLayout mAnimLayout;
    /**
     * YuePlayer文本Layout
     */
    private LinearLayout mYueLayout;
    /**
     * 音乐信息Layout
     */
    private LinearLayout mMusicInfoLayout;


    /**
     * YUE PLAYER
     */
    private String[] mYueSnakeStr = {"Y", "U", "E", "P", "L", "A", "Y", "E", "R"};

    /**
     * YUE PLAYER
     */
    private View[] mYueSnakeAnimView = new HeroTextView[9];

    private ScheduledExecutorService scheduledExecutorService;
    private TimerTask mCountTimeSnakeTimerTask;

    private int mYueAnimWidth;
    private int mYueAnimHeight;
    /**
     * 控制器宽度
     */
    private int mCircleConW;

    /**
     * 贪吃蛇--节拍器
     */
    private int mSnakeMetronome;

    /**
     * 贪吃蛇--步长
     */
    private int mStepW;

    private int[] iW = new int[9];
    private int[] iW2 = new int[9];
    private int[] iW3 = new int[9];

    private DnHeroTextView mShakeTextView;

    /**
     * 晃晃漂流--晃一晃
     */
    private ObjectAnimator mShakeRotation;
    /**
     * 晃晃漂流--漂流
     */
    private ObjectAnimator mShakeTransX;

    public YueAnimManager(Activity context) {
        mContext = context;

        mYueAnimWidth = AppUtils.getScreenWidth(context);
        mYueAnimHeight = AppUtils.dip2px(context, 50);
        mCircleConW = AppUtils.dip2px(context, 70);
    }

    public void initView(FrameLayout animLayout, LinearLayout yueLayout, LinearLayout musicInfoLayout) {
        mAnimLayout = animLayout;
        mYueLayout = yueLayout;
        mMusicInfoLayout = musicInfoLayout;
    }

    /**
     * 底部 YUE PLAYER 动画
     *
     * @param isOnResume isOnResume
     */
    public void refreshYueAnim(Context context, boolean isOnResume) {

        int animMode = Global.getYueAnimType();
        // 无动画
        if (animMode <= YUE_ANIM_TYPE_1) {
            resetBottomStatus();
            // clear
            setYueAnimManager(false);
            // 贪吃蛇动画配置
            clearSnakeAnimConfig();
        } else {
            // 有动画
            if (animMode != YUE_ANIM_TYPE_3) {
                // 贪吃蛇动画配置
                clearSnakeAnimConfig();
            }
            if (!isOnResume) {
                setYueAnimManager(false);
            } else {

                if (mAnimLayout.getVisibility() == View.VISIBLE) {
                    if (MusicPlayService.MPS.isPlay()) {
                        setYueAnimManager(true);
                    } else {
                        setYueAnimManager(false);
                    }
                    return;
                }

                // 显示布局
                setBottomStatusAnim();

                // 根据动画类型创建view
                if (animMode == YUE_ANIM_TYPE_2) {
                    mAnimLayout.removeAllViews();
                    mShakeTextView = new DnHeroTextView(context);
                    mShakeTextView.setText("YuePlayer");
                    mShakeTextView.setTextSize(18);
                    mShakeTextView.setTextColor(ViewUtils.getColor(context, R.color.dn_content_yue));
                    mShakeTextView.setVisibility(View.INVISIBLE);
                    mShakeTextView.setGravity(Gravity.CENTER);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.BOTTOM;
                    mShakeTextView.setLayoutParams(params);
                    mAnimLayout.addView(mShakeTextView);
                    return;
                }

                if (animMode == YUE_ANIM_TYPE_3) {
                    mAnimLayout.removeAllViews();
                    // 添加view
                    for (int i = 0; i < mYueSnakeStr.length; i++) {
                        DnHeroTextView heroTextView = new DnHeroTextView(context);
                        heroTextView.setText(mYueSnakeStr[i]);
                        heroTextView.setTextSize(16);
                        heroTextView.setTextColor(ViewUtils.getColor(context, R.color.dn_content_yue));
                        heroTextView.setVisibility(View.INVISIBLE);
                        heroTextView.setGravity(Gravity.CENTER);
//                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                AppUtils.dip2px(mContext, 18), AppUtils.dip2px(mContext, 18));
                        heroTextView.setLayoutParams(params);
                        mYueSnakeAnimView[i] = heroTextView;
                        mAnimLayout.addView(heroTextView);
                    }
                }
            }
        }
    }

    /**
     * YUE 动画管理
     *
     * @param isStart true开始动画 false停止动画
     */
    public void setYueAnimManager(boolean isStart) {
        int animMode = Global.getYueAnimType();
        if (isStart) {

            if (animMode != YUE_ANIM_TYPE_2) {
                mShakeRotation = null;
                mShakeTransX = null;
            }

            if (animMode == YUE_ANIM_TYPE_3) {
                startYueAnimTimerTask();
            } else if (animMode == YUE_ANIM_TYPE_2) {
                startShakeAnim();
            }
        } else {
            if (animMode == YUE_ANIM_TYPE_3) {
                stopYueAnimTimerTask();
            } else if (animMode == YUE_ANIM_TYPE_2) {
                stopShakeAnim();
            }
        }
    }

    private void stopShakeAnim() {
        if (mShakeRotation != null) {
            mShakeRotation.pause();
        }
        if (mShakeTransX != null) {
            mShakeTransX.pause();
        }
    }

    private void startShakeAnim() {
        if (mShakeTextView == null) {
            return;
        }
        mShakeTextView.post(new Runnable() {
            @Override
            public void run() {
                if (mShakeRotation == null) {
                    float x = (float) mShakeTextView.getWidth() / 2;
                    mShakeTextView.setPivotX(x);
                    mShakeTextView.setPivotY(0f);
                    mShakeRotation = ObjectAnimator.ofFloat(mShakeTextView, "rotation", -14f, 0f, 14f, 0f, -14f, 0f, 10f, 0f, -6f, 0f);
                    mShakeRotation.setInterpolator(new LinearInterpolator());
                    mShakeRotation.setDuration(600);
                    mShakeRotation.setStartDelay(2000);
                    mShakeRotation.addListener(new SimpleAnimationListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            startShakeAnim();
                        }
                    });
                }
                if (mShakeRotation.isPaused()) {
                    mShakeRotation.resume();
                } else {
                    if (!mShakeRotation.isRunning()) {
                        mShakeRotation.start();
                    }
                }

                if (mShakeTransX == null) {
                    float startX = 0;
                    float endX = mYueAnimWidth - mShakeTextView.getWidth();
                    mShakeTransX = ObjectAnimator.ofFloat(mShakeTextView, "translationX",
                            startX, endX, startX);
//                    mShakeTransX.setInterpolator(new BounceInterpolator());
                    mShakeTransX.setInterpolator(new AccelerateDecelerateInterpolator());
                    mShakeTransX.setDuration(8000);
                    mShakeTransX.setStartDelay(1000);
                    mShakeTransX.addListener(new SimpleAnimationListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            startShakeAnim();
                        }
                    });
                }
                if (mShakeTransX.isPaused()) {
                    mShakeTransX.resume();
                } else {
                    if (!mShakeTransX.isRunning()) {
                        mShakeTransX.start();
                    }
                }
            }

        });
    }

    /**
     * TimerTask
     */
    private void startYueAnimTimerTask() {
        createExecutorService();
        if (mCountTimeSnakeTimerTask == null) {
            mCountTimeSnakeTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (mContext == null) {
                        return;
                    }
                    mContext.runOnUiThread(() -> {
                        if (mContext.isFinishing() || mContext.isDestroyed()) {
                            return;
                        }
                        if (mYueSnakeAnimView.length != mYueSnakeStr.length) {
                            return;
                        }
                        // 动画计算
                        calculationSnakeAnim();
                    });
                }
            };
            scheduledExecutorService.scheduleAtFixedRate(mCountTimeSnakeTimerTask,
                    500, 150, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 贪吃蛇动画计算
     */
    private void calculationSnakeAnim() {
        for (int i = 0; i <= mSnakeMetronome; i++) {

            if (mYueSnakeAnimView[i] == null) {
                return;
            }

            if (mYueSnakeAnimView[i].getTranslationX() == 0 && mYueSnakeAnimView[i].getTranslationY() == 0) {
                if (mYueSnakeAnimView[i].getVisibility() != View.VISIBLE) {
                    mYueSnakeAnimView[i].setVisibility(View.VISIBLE);
                }
            }

            if (mYueSnakeAnimView[i].getTranslationY() == 0) {
                mStepW = mYueAnimWidth / AppUtils.dip2px(mContext, 18);
                int oneStepX = mYueAnimWidth / (mStepW);
                if (iW[i] != 0) {
                    mYueSnakeAnimView[i].setTranslationX(mYueSnakeAnimView[i].getTranslationX() + oneStepX);
                }
                iW[i]++;
                if (iW[i] == mStepW + 1) {
                    mYueSnakeAnimView[i].setTranslationX(mYueAnimWidth);
                    int transY = mYueAnimHeight / 2 - mYueSnakeAnimView[i].getHeight() / 2;
                    mYueSnakeAnimView[i].setTranslationY(transY);
                    iW[i] = 0;
                }
            }

            if (mYueSnakeAnimView[i].getTranslationY() == mYueAnimHeight / 2 - mYueSnakeAnimView[i].getHeight() / 2) {
                mStepW = mYueAnimWidth / AppUtils.dip2px(mContext, 18);
                int oneStepX = mYueAnimWidth / (mStepW);
                if (iW2[i] != 0) {
                    mYueSnakeAnimView[i].setTranslationX(mYueSnakeAnimView[i].getTranslationX() - oneStepX);
                }
                iW2[i]++;
                if (iW2[i] == mStepW + 2) {
                    mYueSnakeAnimView[i].setTranslationX(-oneStepX);
                    mYueSnakeAnimView[i].setTranslationY(mYueAnimHeight - mYueSnakeAnimView[i].getHeight());
                    iW2[i] = 0;
                }
            }

            if (mYueSnakeAnimView[i].getTranslationY() == mYueAnimHeight - mYueSnakeAnimView[i].getHeight()) {
                mStepW = mYueAnimWidth / AppUtils.dip2px(mContext, 18);
                int oneStepX = mYueAnimWidth / (mStepW);
                if (iW3[i] != 0) {
                    mYueSnakeAnimView[i].setTranslationX(mYueSnakeAnimView[i].getTranslationX() + oneStepX);
                }
                iW3[i]++;
                if (iW3[i] == mStepW + 2) {
                    mYueSnakeAnimView[i].setTranslationX(0);
                    mYueSnakeAnimView[i].setTranslationY(0);
                    mYueSnakeAnimView[i].setVisibility(View.INVISIBLE);
                    iW3[i] = 0;
                }
            }
        }

        mSnakeMetronome++;
        if (mSnakeMetronome > mYueSnakeStr.length - 1) {
            mSnakeMetronome = mYueSnakeStr.length - 1;
        }
    }

    /**
     * 晃晃漂流--平移动画
     *
     * @param progress
     */
    public void setYueShakeTransAnim(int progress) {
        if (Global.getYueAnimType() != YUE_ANIM_TYPE_2) {
            return;
        }
        if (mShakeTextView == null) {
            return;
        }
        if (mShakeTextView.getVisibility() != View.VISIBLE) {
            mShakeTextView.setVisibility(View.VISIBLE);
        }

        if (progress > 50) {
            // 50后下沉
            progress = 100 - progress;
        }

        float tranY = (float) -(mYueAnimHeight) / 100 * progress;
        mShakeTextView.setTranslationY(tranY);

//        if (mShakeTextView.getTranslationY() < -(mYueAnimHeight - mShakeTextView.getHeight())) {
//            mShakeTextView.setTranslationY(-(mYueAnimHeight - mShakeTextView.getHeight()));
//        }
    }

    /**
     * 清除贪吃蛇动画配置
     */
    private void clearSnakeAnimConfig() {
        mSnakeMetronome = 0;
        iW = new int[9];
        iW2 = new int[9];
        iW3 = new int[9];
    }

    /**
     * 底部状态为默认
     */
    private void resetBottomStatus() {
        if (mAnimLayout != null && mAnimLayout.getVisibility() != View.GONE) {
            mAnimLayout.setVisibility(View.GONE);
        }
        if (mYueLayout != null && mYueLayout.getVisibility() != View.VISIBLE) {
            mYueLayout.setVisibility(View.VISIBLE);
            AnimHelper.fadeIn(mYueLayout, 1000);
        }
        if (mMusicInfoLayout != null && mMusicInfoLayout.getVisibility() != View.VISIBLE) {
            mMusicInfoLayout.setVisibility(View.VISIBLE);
            AnimHelper.fadeIn(mMusicInfoLayout, 1000);
        }
    }

    /**
     * 底部状态为动画
     */
    private void setBottomStatusAnim() {
        if (mAnimLayout != null && mAnimLayout.getVisibility() != View.VISIBLE) {
            mAnimLayout.setVisibility(View.VISIBLE);
            AnimHelper.fadeIn(mAnimLayout, 1000);
        }
        if (mYueLayout != null && mYueLayout.getVisibility() != View.GONE) {
            mYueLayout.setVisibility(View.GONE);
        }
        if (mMusicInfoLayout != null && mMusicInfoLayout.getVisibility() != View.GONE) {
            mMusicInfoLayout.setVisibility(View.GONE);
        }
    }

    private void createExecutorService() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = new ScheduledThreadPoolExecutor(3,
                    new BasicThreadFactory.Builder().namingPattern("scheduled-pool-%d").daemon(true).build());
        }
    }

    private void cancelCountTimeTask() {
        if (mCountTimeSnakeTimerTask != null) {
            mCountTimeSnakeTimerTask.cancel();
            mCountTimeSnakeTimerTask = null;
        }
    }

    private void stopYueAnimTimerTask() {
        cancelCountTimeTask();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

    public void darkModeChange() {
        List<View> listAllViews = DarkModeManager.getInstance().getAllViews(mAnimLayout);
        for (int i = 0; i < listAllViews.size(); i++) {
            if (listAllViews.get(i) instanceof DnHeroTextView) {
                ((DnHeroTextView) listAllViews.get(i)).setTextColor(
                        ViewUtils.getColor(mContext, R.color.dn_content_yue));
            }
        }
    }
}
