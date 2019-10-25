package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseImageView
 */
public class BaseImageView extends android.support.v7.widget.AppCompatImageView {

    public BaseImageView(Context context) {
        super(context);
    }

    public BaseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
