package com.ghy.yueplayer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

import com.ghy.yueplayer.BuildConfig;
import com.ghy.yueplayer.PlayerApplication;
import com.ghy.yueplayer.widget.FakeBoldSpan;
import com.gyf.barlibrary.ImmersionBar;

import timber.log.Timber;

/**
 * @author HY
 * @date 2019-07-11
 * Desc:Utils
 */
public class Utils {

    /**
     * dp转px
     *
     * @param dpValue
     * @return
     */
    public static int dip2px(float dpValue) {
        final float scale = PlayerApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) ((dpValue * scale) + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        final float scale = PlayerApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 拨打客服电话
     */
    public static void callServicePhone(Context context, String telPhone) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(telPhone)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telPhone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * EditText光标移动到文本框末尾
     *
     * @param editText
     */
    public static void moveEditLightToEnd(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.setSelection(editText.getText().length());
    }

    /**
     * 设置MediumBold字体
     *
     * @param s 文本
     * @return
     */
    public static SpannableStringBuilder setMediumBoldSpanText(String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(s);
        spannableString.setSpan(new FakeBoldSpan(), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * 13位时间戳转换为10位
     *
     * @param timeDate
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static long coverTimeLength13to10(long timeDate) {
        long time;
        if (String.valueOf(timeDate).length() == 13) {
            long timeSec = timeDate / 1000;
            time = Long.parseLong(String.format("%010d", timeSec));
        } else {
            time = timeDate;
        }
        return time;
    }

    /**
     * res转uri
     */
    public static String resTranslateUri(Activity activity, int resId) {
        String url = "";
        if (activity == null) {
            return url;
        }
        Resources r = activity.getResources();
        if (r == null) {
            return url;
        }
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/"
                + r.getResourceEntryName(resId));
        return uri.toString();
    }

    /**
     * 打印方法耗时
     *
     * @param simpleName TAG标签
     * @param methodName 方法名称
     * @param startTime  开始时间
     * @param endTime    结束时间
     */
    public static void printMethodConsumingTime(String simpleName, String methodName,
                                                long startTime, long endTime) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        long methodTime;
        methodTime = Math.abs(endTime - startTime);
        simpleName = TextUtils.isEmpty(simpleName) ? "MethodTime" : simpleName;
        methodName = TextUtils.isEmpty(methodName) ? "bind" : methodName;
        Timber.i(simpleName, "Method---->>" + methodName + "----耗时---->>" + methodTime + "毫秒");
    }

    /**
     * 获取进程名称
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    /**
     * 获取FileProvider名称
     *
     * @return FileProvider
     */
    public static String getFileProviderName(Context context) {
        return context == null ? "" : context.getPackageName() + ".fileProvider";
    }

    /**
     * 获取导航栏高度
     *
     * @return 导航栏高度
     */
    public static int getNavigationBarHeight(Activity activity) {
        int navigationBarHeight = 0;
        if (activity == null) {
            return navigationBarHeight;
        }
        // 是否存在导航栏
        boolean hasNavigationBar = ImmersionBar.hasNavigationBar(activity);
        if (hasNavigationBar) {
            navigationBarHeight = ImmersionBar.getNavigationBarHeight(activity);
        }
        return navigationBarHeight;
    }

    /**
     * 获取手机厂商
     */
    public static String getOsString() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 是否三星厂商
     */
    public static boolean isSamsungOs() {
        return getOsString().equals("samsung");
    }
}
