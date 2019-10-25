package com.ghy.yueplayer.base.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.common.iface.IDarkMode;
import com.ghy.yueplayer.constant.Global;

/**
 * @author HY
 * @date 2019-07-23
 * Desc:DarkMode-SwitchCompat
 */
public class DnSwitchCompat extends BaseSwitchCompat implements IDarkMode {

    public DnSwitchCompat(Context context) {
        super(context);
        init(context, null);
    }

    public DnSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DnSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        changeDarkModeUi(Global.DAY_MODE);
    }

    @Override
    public void darkModeChange(boolean isDayMode) {
        changeDarkModeUi(isDayMode);
    }

    private void changeDarkModeUi(boolean isDayMode) {
        int thumbColor = isDayMode ? ContextCompat.getColor(getContext(), R.color.dn_btn_1) :
                ContextCompat.getColor(getContext(), R.color.dn_btn_1_night);
        int unCheckColor = isDayMode ? ContextCompat.getColor(getContext(), R.color.dn_switch_bg) :
                ContextCompat.getColor(getContext(), R.color.dn_switch_bg_night);
        DrawableCompat.setTintList(getThumbDrawable(), new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        thumbColor,
                        unCheckColor
                }));

//        DrawableCompat.setTintList(getTrackDrawable(), new ColorStateList(
//                new int[][]{
//                        new int[]{android.R.attr.state_checked},
//                        new int[]{}
//                },
//                new int[]{
//                        thumbColor,
//                        unCheckColor
//                }));
    }

}
