package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseCardView
 */
public class BaseCardView extends CardView {

    public BaseCardView(Context context) {
        super(context);
    }

    public BaseCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
