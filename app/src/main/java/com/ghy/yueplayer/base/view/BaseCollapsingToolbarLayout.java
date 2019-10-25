package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseCollapsingToolbarLayout
 */
public class BaseCollapsingToolbarLayout extends CollapsingToolbarLayout {

    public BaseCollapsingToolbarLayout(Context context) {
        super(context);
    }

    public BaseCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
