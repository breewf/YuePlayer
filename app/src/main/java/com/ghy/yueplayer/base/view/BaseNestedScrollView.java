package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseNestedScrollView
 */
public class BaseNestedScrollView extends NestedScrollView {

    public BaseNestedScrollView(Context context) {
        super(context);
    }

    public BaseNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
