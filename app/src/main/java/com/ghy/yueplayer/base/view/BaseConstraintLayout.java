package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseConstraintLayout
 */
public class BaseConstraintLayout extends ConstraintLayout {

    public BaseConstraintLayout(Context context) {
        super(context);
    }

    public BaseConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
