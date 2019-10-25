package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:BaseEditText
 */
public class BaseEditText extends android.support.v7.widget.AppCompatEditText {

    public BaseEditText(Context context) {
        super(context);
    }

    public BaseEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs) {

    }

}
