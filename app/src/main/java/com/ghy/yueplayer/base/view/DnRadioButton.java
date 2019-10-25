package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;

import com.ghy.yueplayer.common.iface.IDarkMode;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.utils.DarkModeUtils;
import com.ghy.yueplayer.utils.ViewUtils;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:DarkMode-RadioButton
 */
public class DnRadioButton extends BaseRadioButton implements IDarkMode {

    private int[] mTextColor = new int[8];
    /**
     * mBgColor[0] 日间模式资源
     * mBgColor[1] 夜间模式资源
     * mBgColor[2] 0:color 1:drawable
     */
    private int[] mBgColor = new int[3];

    public DnRadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public DnRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DnRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        mTextColor = DarkModeUtils.getTextColor(context, attrs);
        mBgColor = DarkModeUtils.getBgColor(context, attrs);
        changeDarkModeUi(Global.DAY_MODE);
    }

    @Override
    public void darkModeChange(boolean isDayMode) {
        changeDarkModeUi(isDayMode);
    }

    private void changeDarkModeUi(boolean isDayMode) {
        DarkModeUtils.setTextRes(this, isDayMode, mTextColor);
        DarkModeUtils.setBackgroundRes(this, isDayMode, mBgColor);
    }

    /**
     * 设置View背景颜色，支持DarkMode自动变色
     *
     * @param colorId 日间正常colorId
     */
    protected void setBackgroundColorSupport(@ColorRes int colorId) {
        ViewUtils.setBackgroundColor(this, colorId);
    }

    /**
     * 设置View背景Resource，支持DarkMode自动变色
     *
     * @param resId 日间正常resId
     */
    protected void setBackgroundResourceSupport(@DrawableRes int resId) {
        ViewUtils.setBackgroundResource(this, resId);
    }

}
