package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseFrameLayout
 */
public class BaseFrameLayout extends FrameLayout {

    /**
     * mBgColor[0] 日间模式资源
     * mBgColor[1] 夜间模式资源
     * mBgColor[2] 0:color 1:drawable
     */
    private int[] mBgColor = new int[3];

    public BaseFrameLayout(Context context) {
        super(context);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
