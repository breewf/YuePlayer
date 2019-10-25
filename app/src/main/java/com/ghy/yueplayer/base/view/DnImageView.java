package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;

import com.ghy.yueplayer.common.iface.IDarkMode;
import com.ghy.yueplayer.constant.Const;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.utils.DarkModeUtils;
import com.ghy.yueplayer.utils.ViewUtils;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:DrkMode-ImageView
 */
public class DnImageView extends BaseImageView implements IDarkMode {

    private int[] mSrcDrawable = new int[4];

    public DnImageView(Context context) {
        super(context);
        init(context, null);
    }

    public DnImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DnImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        mSrcDrawable = DarkModeUtils.getSrcDrawable(context, attrs);
        changeDarkModeUi(Global.DAY_MODE);
    }

    @Override
    public void darkModeChange(boolean isDayMode) {
        changeDarkModeUi(isDayMode);
    }

    private void changeDarkModeUi(boolean isDayMode) {
        if (isDayMode) {
            if (mSrcDrawable[0] != 0) {
                setImageResource(mSrcDrawable[0]);
            }
            if (mSrcDrawable[2] != 0) {
                setBackgroundResource(mSrcDrawable[2]);
            }
        } else {
            if (mSrcDrawable[1] != 0) {
                setImageResource(mSrcDrawable[1]);
            }
            if (mSrcDrawable[3] != 0) {
                setBackgroundResource(mSrcDrawable[3]);
            }
        }
    }

    /**
     * 设置ImageView背景Resource，支持DarkMode自动变色
     *
     * @param resId 日间正常resId
     */
    public void setImageResourceSupport(@DrawableRes int resId) {
        try {
            Context context = getContext();
            if (context == null) {
                return;
            }
            if (Global.DAY_MODE) {
                setImageResource(resId);
            } else {
                String resName = context.getResources().getResourceEntryName(resId);
                String nightResName = resName + Const.NIGHT_SUFFIX;
                int nightResId = context.getResources().getIdentifier(
                        nightResName, "drawable", context.getPackageName());
                setImageResource(nightResId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
