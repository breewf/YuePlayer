package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ghy.yueplayer.common.iface.IDarkMode;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.utils.DarkModeUtils;
import com.ghy.yueplayer.utils.ViewUtils;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:DrkMode-View
 */
public class DnView extends BaseView implements IDarkMode {

    /**
     * mBgColor[0] 日间模式资源
     * mBgColor[1] 夜间模式资源
     * mBgColor[2] 0:color 1:drawable
     */
    private int[] mBgColor = new int[3];

    private int[] mLoadingColor = new int[2];

    public DnView(Context context) {
        super(context);
        init(context, null);
    }

    public DnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public DnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        mBgColor = DarkModeUtils.getBgColor(context, attrs);
        changeDarkModeUi(Global.DAY_MODE);
    }

    @Override
    public void darkModeChange(boolean isDayMode) {
        changeDarkModeUi(isDayMode);
    }

    private void changeDarkModeUi(boolean isDayMode) {
        DarkModeUtils.setBackgroundRes(this, isDayMode, mBgColor);
    }

    /**
     * 设置View背景颜色，支持DarkMode自动变色
     *
     * @param colorId 日间正常colorId
     */
    public void setBackgroundColorSupport(@ColorRes int colorId) {
        ViewUtils.setBackgroundColor(this, colorId);
    }

    /**
     * 设置View背景Resource，支持DarkMode自动变色
     *
     * @param resId 日间正常resId
     */
    public void setBackgroundResourceSupport(@DrawableRes int resId) {
        ViewUtils.setBackgroundResource(this, resId);
    }

}
