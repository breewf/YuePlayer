package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseSwitchCompat
 */
public class BaseSwitchCompat extends SwitchCompat {

    public BaseSwitchCompat(Context context) {
        super(context);
    }

    public BaseSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
