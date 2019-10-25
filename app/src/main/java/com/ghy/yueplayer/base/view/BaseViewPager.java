package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseViewPager
 */
public class BaseViewPager extends ViewPager {

    public BaseViewPager(Context context) {
        super(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
