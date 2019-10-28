package com.ghy.yueplayer.constant;

import com.ghy.yueplayer.common.PreferManager;

/**
 * @author HY
 * @date 2019/4/5
 * Desc:Global
 */
public class Global {

    /**
     * 是否是日间模式--全局
     */
    public static boolean DAY_MODE = true;

    /**
     * YUE动画类型
     */
    private static int mYueAnimType;

    /**
     * Circle动画类型
     */
    private static int mCircleAnimType;

    public static void setYueAnimType(int yueAnimType) {
        mYueAnimType = yueAnimType;
    }

    public static void setCircleAnimType(int circleAnimType) {
        mCircleAnimType = circleAnimType;
    }

    public static int getYueAnimType() {
        return mYueAnimType == 0 ? PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1) : mYueAnimType;
    }

    public static int getCircleAnimType() {
        return mCircleAnimType == 0 ? PreferManager.getInt(PreferManager.MAIN_CIRCLE_ANIM, -1) : mCircleAnimType;
    }
}
