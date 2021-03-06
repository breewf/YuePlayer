package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.ghy.yueplayer.common.iface.IDarkMode;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.utils.DarkModeUtils;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:DarkMode-ProgressBar
 */
public class DnProgressBar extends BaseProgressBar implements IDarkMode {

    /**
     * mDrawable[0] 日间模式资源
     * mDrawable[1] 夜间模式资源
     * 对应indeterminateDrawable
     */
    private int[] mDrawable = new int[2];
    /**
     * mDrawable2[0] 日间模式资源
     * mDrawable2[1] 夜间模式资源
     * 对应progressDrawable
     */
    private int[] mDrawable2 = new int[2];

    public DnProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public DnProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DnProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        mDrawable = DarkModeUtils.getIndeterDrawable(context, attrs);
        mDrawable2 = DarkModeUtils.getProgressDrawable(context, attrs);
        changeDarkModeUi(Global.DAY_MODE);
    }

    @Override
    public void darkModeChange(boolean isDayMode) {
        changeDarkModeUi(isDayMode);
    }

    private void changeDarkModeUi(boolean isDayMode) {
        Rect bounds = getIndeterminateDrawable().getBounds();
        if (isDayMode) {
            if (mDrawable[0] != 0) {
                getIndeterminateDrawable().setBounds(bounds);
                setIndeterminateDrawable(ContextCompat.getDrawable(getContext(), mDrawable[0]));
            }
            if (mDrawable2[0] != 0) {
                setProgressDrawable(ContextCompat.getDrawable(getContext(), mDrawable2[0]));
            }
        } else {
            if (mDrawable[1] != 0) {
                getIndeterminateDrawable().setBounds(bounds);
                setIndeterminateDrawable(ContextCompat.getDrawable(getContext(), mDrawable[1]));
            }
            if (mDrawable2[1] != 0) {
                setProgressDrawable(ContextCompat.getDrawable(getContext(), mDrawable2[1]));
            }
        }
    }

}
