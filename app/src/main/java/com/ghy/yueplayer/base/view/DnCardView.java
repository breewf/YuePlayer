package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;

import com.ghy.yueplayer.common.iface.IDarkMode;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.utils.DarkModeUtils;
import com.ghy.yueplayer.utils.ViewUtils;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:DarkMode-CardView
 */
public class DnCardView extends BaseCardView implements IDarkMode {

    /**
     * mBgColor[0] 日间模式资源
     * mBgColor[1] 夜间模式资源
     * mBgColor[2] 0:color 1:drawable
     */
    private int[] mBgColor = new int[3];

    public DnCardView(Context context) {
        super(context);
        init(context, null);
    }

    public DnCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DnCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        setCardBackgroundColor(isDayMode);
    }

    private void setCardBackgroundColor(boolean isDayMode) {
        if (isDayMode) {
            if (isColorRes(mBgColor)) {
                if (mBgColor[0] != 0) {
                    setCardBackgroundColor(mBgColor[0]);
                }
            }
        } else {
            if (isColorRes(mBgColor)) {
                if (mBgColor[1] != 0) {
                    setCardBackgroundColor(mBgColor[1]);
                }
            }
        }
    }

    /**
     * 设置View背景颜色，支持DarkMode自动变色
     *
     * @param colorId 日间正常colorId
     */
    public void setBackgroundColorSupport(@ColorRes int colorId) {
        setCardBackgroundColor(ViewUtils.getColor(getContext(), colorId));
    }

    /**
     * 是否是color资源
     *
     * @return true:是 false:为drawable
     */
    private static boolean isColorRes(int[] bgRes) {
        return bgRes[2] == 0;
    }

}
