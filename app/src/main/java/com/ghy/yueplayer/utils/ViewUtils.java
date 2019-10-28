package com.ghy.yueplayer.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.tablayout.SlidingTabLayout;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.common.DarkModeManager;
import com.ghy.yueplayer.constant.Const;
import com.ghy.yueplayer.constant.Global;
import com.jaeger.recyclerviewdivider.RecyclerViewDivider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author HY
 * @date 2019-07-26
 * Desc:ViewUtils
 */
public class ViewUtils {

    /**
     * 获取Color资源Res，根据DarkMode返回相应颜色
     *
     * @param colorId 日间正常colorId
     */
    @ColorRes
    public static int getColorRes(Context context, @ColorRes int colorId) {
        try {
            if (Global.DAY_MODE) {
                return colorId;
            } else {
                String resName = context.getResources().getResourceEntryName(colorId);
                String nightResName = resName + Const.NIGHT_SUFFIX;
                @ColorRes int nightColorId = context.getResources().getIdentifier(
                        nightResName, "color", context.getPackageName());
                return nightColorId == 0 ? colorId : nightColorId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colorId;
    }

    /**
     * 获取Color颜色，根据DarkMode返回相应颜色
     *
     * @param colorId 日间正常colorId
     */
    @ColorInt
    public static int getColor(Context context, @ColorRes int colorId) {
        try {
            if (Global.DAY_MODE) {
                return ContextCompat.getColor(context, colorId);
            } else {
                String resName = context.getResources().getResourceEntryName(colorId);
                String nightResName = resName + Const.NIGHT_SUFFIX;
                @ColorRes int nightColorId = context.getResources().getIdentifier(
                        nightResName, "color", context.getPackageName());
                return nightColorId == 0 ? ContextCompat.getColor(context, colorId) :
                        ContextCompat.getColor(context, nightColorId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ContextCompat.getColor(context, colorId);
    }

    /**
     * 获取Resource，根据DarkMode返回相应Resource
     *
     * @param resId 日间正常resId
     */
    @DrawableRes
    public static int getResource(Context context, @DrawableRes int resId) {
        try {
            if (Global.DAY_MODE) {
                return resId;
            } else {
                String resName = context.getResources().getResourceEntryName(resId);
                String nightResName = resName + Const.NIGHT_SUFFIX;
                int nightResId = 0;
                // 资源类型--是color还是drawable
                String resourceName = context.getResources().getResourceTypeName(resId);
                if (resourceName.equals(Const.RESOURCE_NAME_COLOR)) {
                    nightResId = context.getResources().getIdentifier(
                            nightResName, "color", context.getPackageName());
                } else if (resourceName.equals(Const.RESOURCE_NAME_DRAWABLE)) {
                    nightResId = context.getResources().getIdentifier(
                            nightResName, "drawable", context.getPackageName());
                }
                return nightResId == 0 ? resId : nightResId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resId;
    }

    /**
     * 获取状态栏Color资源Res，根据DarkMode返回相应颜色
     */
    @ColorRes
    public static int getStatusBarColorRes() {
        return Global.DAY_MODE ? R.color.dn_status_bar_color :
                R.color.dn_status_bar_color_night;
    }

    /**
     * 获取导航栏Color资源Res，根据DarkMode返回相应颜色
     */
    @ColorRes
    public static int getNavigationBarColorRes() {
        return Global.DAY_MODE ? R.color.dn_navigation_bar_color :
                R.color.dn_navigation_bar_color_night;
    }

    /**
     * 获取默认分割线Color资源Res，根据DarkMode返回相应颜色
     */
    @ColorRes
    public static int getDividerColorRes() {
        return Global.DAY_MODE ? R.color.dn_gary_bg_1 :
                R.color.dn_gary_bg_1_night;
    }

    /**
     * 获取placeholder资源Res，根据DarkMode返回相应颜色
     */
    @ColorRes
    public static int getPlaceholderRes() {
        return Global.DAY_MODE ? R.color.dn_placeholder :
                R.color.dn_placeholder_night;
    }

    /**
     * 设置textView颜色，支持DarkMode自动变色
     *
     * @param textView textView
     * @param colorId  日间正常colorId
     */
    public static void setTextColor(TextView textView, @ColorRes int colorId) {
        try {
            if (textView == null) {
                return;
            }
            Context context = textView.getContext();
            if (context == null) {
                return;
            }
            if (Global.DAY_MODE) {
                textView.setTextColor(ContextCompat.getColor(context, colorId));
            } else {
                String resName = context.getResources().getResourceEntryName(colorId);
                String nightResName = resName + Const.NIGHT_SUFFIX;
                @ColorRes int nightColorId = context.getResources().getIdentifier(
                        nightResName, "color", textView.getContext().getPackageName());
                textView.setTextColor(nightColorId == 0 ? ContextCompat.getColor(context, colorId) :
                        ContextCompat.getColor(context, nightColorId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置View背景颜色，支持DarkMode自动变色
     *
     * @param view    view
     * @param colorId 日间正常colorId
     */
    public static void setBackgroundColor(View view, @ColorRes int colorId) {
        try {
            if (view == null) {
                return;
            }
            Context context = view.getContext();
            if (context == null) {
                return;
            }
            if (Global.DAY_MODE) {
                view.setBackgroundColor(ContextCompat.getColor(context, colorId));
            } else {
                String resName = context.getResources().getResourceEntryName(colorId);
                String nightResName = resName + Const.NIGHT_SUFFIX;
                @ColorRes int nightColorId = context.getResources().getIdentifier(
                        nightResName, "color", view.getContext().getPackageName());
                view.setBackgroundColor(nightColorId == 0 ? ContextCompat.getColor(context, colorId) :
                        ContextCompat.getColor(context, nightColorId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置View背景Resource，支持DarkMode自动变色
     *
     * @param view  view
     * @param resId 日间正常resId
     */
    public static void setBackgroundResource(View view, @DrawableRes int resId) {
        try {
            if (view == null) {
                return;
            }
            Context context = view.getContext();
            if (context == null) {
                return;
            }
            if (Global.DAY_MODE) {
                view.setBackgroundResource(resId);
            } else {
                String resName = context.getResources().getResourceEntryName(resId);
                String nightResName = resName + Const.NIGHT_SUFFIX;
                // 资源类型--是color还是drawable
                String resourceName = context.getResources().getResourceTypeName(resId);
                int nightResourceId = 0;
                if (resourceName.equals(Const.RESOURCE_NAME_COLOR)) {
                    nightResourceId = context.getResources().getIdentifier(
                            nightResName, "color", context.getPackageName());
                } else if (resourceName.equals(Const.RESOURCE_NAME_DRAWABLE)) {
                    nightResourceId = context.getResources().getIdentifier(
                            nightResName, "drawable", context.getPackageName());
                }
                view.setBackgroundResource(nightResourceId == 0 ? resId : nightResourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 让RecyclerView缓存在Pool中的item失效
     *
     * @param recyclerView recyclerView
     */
    public static void clearRecycledViewPool(RecyclerView recyclerView) {
        Class<RecyclerView> recyclerViewClass = RecyclerView.class;
        try {
            Field declaredField = recyclerViewClass.getDeclaredField("mRecycler");
            declaredField.setAccessible(true);
            Method declaredMethod = Class.forName(RecyclerView.Recycler.class.getName()).getDeclaredMethod("clear", (Class<?>[]) new Class[0]);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(declaredField.get(recyclerView), new Object[0]);
            RecyclerView.RecycledViewPool recycledViewPool = recyclerView.getRecycledViewPool();
            recycledViewPool.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新adapter
     *
     * @param adapter adapter
     */
    public static void notifyDataSetChanged(BaseQuickAdapter adapter) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 遍历header view
     *
     * @param adapter adapter
     */
    public static void traversingHeaderView(BaseQuickAdapter adapter) {
        if (adapter != null) {
            DarkModeManager.getInstance().traversingAllViews(adapter.getHeaderLayout());
        }
    }

    /**
     * RecyclerView--removeItemDecoration
     *
     * @param recyclerView recyclerView
     */
    public static void removeItemDecoration(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return;
        }
        for (int i = 0; i < recyclerView.getItemDecorationCount(); i++) {
            recyclerView.removeItemDecoration(recyclerView.getItemDecorationAt(i));
        }
    }

    /**
     * 全局统一的SlidingTabLayout变色
     * 适用于xml中SlidingTabLayout设置了白色的background
     *
     * @param tabLayout tabLayout
     */
    public static void initSlidingTabLayoutUi(SlidingTabLayout tabLayout) {
        initSlidingTabLayoutUi(tabLayout, true, R.color.dn_white, R.color.dn_white_night);
    }

    /**
     * 全局统一的SlidingTabLayout变色
     * 适用于xml中SlidingTabLayout设置了background
     *
     * @param tabLayout tabLayout
     */
    public static void initSlidingTabLayoutUi(SlidingTabLayout tabLayout, @ColorRes int bgColorId, @ColorRes int bgNightColorId) {
        initSlidingTabLayoutUi(tabLayout, true, bgColorId, bgNightColorId);
    }

    /**
     * 全局统一的SlidingTabLayout变色
     * 适用于xml中SlidingTabLayout没有设置background
     *
     * @param tabLayout tabLayout
     */
    public static void initSlidingTabLayoutUiNotBg(SlidingTabLayout tabLayout) {
        initSlidingTabLayoutUi(tabLayout, false, 0, 0);
    }

    /**
     * 全局统一的SlidingTabLayout变色
     *
     * @param tabLayout      tabLayout
     * @param needSetBg      是否需要设置Background
     * @param bgColorId      日间colorId
     * @param bgNightColorId 夜间colorId
     */
    private static void initSlidingTabLayoutUi(SlidingTabLayout tabLayout,
                                               boolean needSetBg, @ColorRes int bgColorId, @ColorRes int bgNightColorId) {
        if (tabLayout == null) {
            return;
        }
        Context context = tabLayout.getContext();
        if (context == null) {
            return;
        }
        if (needSetBg) {
            tabLayout.setBackgroundColor(ContextCompat.getColor(context,
                    Global.DAY_MODE ? bgColorId : bgNightColorId));
        }
        tabLayout.setTextUnselectColor(ContextCompat.getColor(context,
                Global.DAY_MODE ? R.color.dn_channel_name_2 : R.color.dn_channel_name_2_night));
        tabLayout.setTextSelectColor(ContextCompat.getColor(context,
                Global.DAY_MODE ? R.color.dn_channel_name : R.color.dn_channel_name_night));
        tabLayout.setIndicatorColor(ContextCompat.getColor(context,
                Global.DAY_MODE ? R.color.dn_channel_line : R.color.dn_channel_line_night));
    }

    /**
     * 全局统一的SlidingTabLayout变色--使用Resource
     *
     * @param tabLayout    tabLayout
     * @param bgResourceId 日间ResourceId
     */
    public static void initSlidingTabLayoutUiResource(SlidingTabLayout tabLayout,
                                                      @DrawableRes int bgResourceId) {
        if (tabLayout == null) {
            return;
        }
        Context context = tabLayout.getContext();
        if (context == null) {
            return;
        }
        tabLayout.setBackgroundResource(ViewUtils.getResource(context, bgResourceId));
        tabLayout.setTextUnselectColor(ContextCompat.getColor(context,
                Global.DAY_MODE ? R.color.dn_channel_name_2 : R.color.dn_channel_name_2_night));
        tabLayout.setTextSelectColor(ContextCompat.getColor(context,
                Global.DAY_MODE ? R.color.dn_channel_name : R.color.dn_channel_name_night));
        tabLayout.setIndicatorColor(ContextCompat.getColor(context,
                Global.DAY_MODE ? R.color.dn_channel_line : R.color.dn_channel_line_night));
    }

    /**
     * 全局统一的TabLayout变色
     *
     * @param tabLayout tabLayout
     */
    public static void initTabLayout(TabLayout tabLayout) {
        if (tabLayout == null) {
            return;
        }
        Context context = tabLayout.getContext();
        if (context == null) {
            return;
        }
        tabLayout.setTabTextColors(ViewUtils.getColor(context, R.color.dn_channel_name_2),
                ViewUtils.getColor(context, R.color.dn_channel_name));
        tabLayout.setSelectedTabIndicatorColor(ViewUtils.getColor(context, R.color.dn_channel_line));
    }

    /**
     * 全局统一的TabLayout变色
     *
     * @param tabLayout tabLayout
     */
    public static void initTabLayout2(TabLayout tabLayout) {
        if (tabLayout == null) {
            return;
        }
        Context context = tabLayout.getContext();
        if (context == null) {
            return;
        }
        tabLayout.setTabTextColors(ViewUtils.getColor(context, R.color.dn_channel_name_4),
                ViewUtils.getColor(context, R.color.dn_channel_name_3));
        tabLayout.setSelectedTabIndicatorColor(ViewUtils.getColor(context, R.color.dn_channel_line_2));
    }

    /**
     * 初始化recyclerView分割线
     * 默认RecyclerViewDivider.Style.BETWEEN
     * 默认1f
     * 默认全局灰底dn_gary_bg_1
     *
     * @param recyclerView recyclerView
     */
    public static void initRvDivider(RecyclerView recyclerView) {
        initRvDivider(recyclerView, RecyclerViewDivider.Style.BETWEEN, 1F);
    }

    /**
     * 初始化recyclerView分割线
     * 默认RecyclerViewDivider.Style.BETWEEN
     * 默认全局灰底dn_gary_bg_1
     *
     * @param recyclerView recyclerView
     */
    public static void initRvDivider(RecyclerView recyclerView, float size) {
        initRvDivider(recyclerView, RecyclerViewDivider.Style.BETWEEN, size);
    }

    /**
     * 初始化recyclerView分割线
     * 默认5f
     * 默认全局灰底dn_gary_bg_1
     *
     * @param recyclerView recyclerView
     */
    public static void initRvDivider(RecyclerView recyclerView, @RecyclerViewDivider.Style int style) {
        initRvDivider(recyclerView, style, 5f);
    }

    /**
     * 初始化recyclerView分割线
     * 默认全局灰底dn_gary_bg_1
     *
     * @param recyclerView recyclerView
     */
    public static void initRvDivider(RecyclerView recyclerView, @RecyclerViewDivider.Style int style, float size) {
        initRvDivider(recyclerView, style, size, R.color.dn_gary_bg_1, R.color.dn_gary_bg_1_night);
    }

    /**
     * 初始化recyclerView分割线
     *
     * @param recyclerView recyclerView
     */
    public static void initRvDivider(RecyclerView recyclerView, @RecyclerViewDivider.Style int style, float size,
                                     @ColorRes int colorRes, @ColorRes int colorNightRes) {
        if (recyclerView == null || recyclerView.getContext() == null) {
            return;
        }
        RecyclerViewDivider divider = new RecyclerViewDivider.Builder(recyclerView.getContext())
                .setStyle(style)
                .setColorRes(Global.DAY_MODE ? colorRes : colorNightRes)
                .setSize(size)
                .build();
        recyclerView.addItemDecoration(divider);
    }

    /**
     * 获取着色后的drawable
     */
    public static Drawable getTintDrawable(@NonNull Context context, @DrawableRes int resId, @ColorRes int colorId) {
        return AppUtils.tintDrawable(ContextCompat.getDrawable(context, resId),
                ContextCompat.getColor(context, colorId));
    }
}
