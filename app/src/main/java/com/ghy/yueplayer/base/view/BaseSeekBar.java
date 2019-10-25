package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseSeekBar
 */
public class BaseSeekBar extends android.support.v7.widget.AppCompatSeekBar {

    public BaseSeekBar(Context context) {
        super(context);
    }

    public BaseSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
