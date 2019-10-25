package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseCoordinatorLayout
 */
public class BaseCoordinatorLayout extends CoordinatorLayout {

    public BaseCoordinatorLayout(Context context) {
        super(context);
    }

    public BaseCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
