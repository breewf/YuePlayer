package com.ghy.yueplayer.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * 名称：AppUtils.java
 * 描述：应用工具类
 */
public class AppUtils {

    /**
     * 回到Home页，后台运行
     *
     * @param context
     */
    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

    /**
     * need < uses-permission android:name =“android.permission.GET_TASKS” />
     * 判断是否前台运行
     *
     * @param context
     * @return if application is in foreground return true, otherwise return false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName componentName = taskList.get(0).topActivity;
            if (componentName != null && componentName.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * need < uses-permission android:name =“android.permission.GET_TASKS” />
     * 判断是否后台运行
     *
     * @param context
     * @return if application is in background return true, otherwise return false
     */
    public static boolean isRunningBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName componentName = taskList.get(0).topActivity;
            if (componentName != null && !componentName.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取App包信息
     *
     * @param context
     * @return
     */
    public PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 描述：打开并安装文件.
     *
     * @param context the context
     * @param file    apk文件路径
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 描述：卸载程序.
     *
     * @param context     the context
     * @param packageName 包名
     */
    public static void uninstallApk(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        context.startActivity(intent);
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param context   the context
     * @param className 判断的服务名字 "com.xxx.xx..XXXService"
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator<RunningServiceInfo> l = servicesList.iterator();
        while (l.hasNext()) {
            RunningServiceInfo si = (RunningServiceInfo) l.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 停止服务.
     *
     * @param context   the context
     * @param className the class name
     * @return true, if successful
     */
    public static boolean stopRunningService(Context context, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(context, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = context.stopService(intent_service);
        }
        return ret;
    }

    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Gps是否打开
     * 需要<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />权限
     *
     * @param context the context
     * @return true, if is gps enabled
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    /**
     * 判断当前网络是否是移动数据网络.
     *
     * @param context the context
     * @return boolean
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int statusHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();
        } else {
            mResources = context.getResources();
        }
        return mResources.getDisplayMetrics();
    }

    /**
     * dip2px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 显示或隐藏软键盘
     *
     * @param view
     * @param isShow true = show , false = hide
     */
    public static void toggleSoftKeyboard(View view, boolean isShow) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    public static void showSoftKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hideSoftKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 着色
     *
     * @param drawable
     * @param color
     * @return
     */
    public static Drawable tintDrawable(Drawable drawable, int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        ColorStateList colors = ColorStateList.valueOf(color);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }


    /**
     * 获取应用版本名称 如：V3.2.0
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (TextUtils.isEmpty(versionName)) return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取应用版本号 如：64
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取手机安卓版本号，如23
     *
     * @return
     */
    public static int getAndVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机安卓版本号名称，如6.0
     *
     * @return
     */
    public static String getAndVersionName() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机厂商名称
     *
     * @return
     */
    public static String getPhoneType() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取设备ID
     *
     * @param context
     * @return
     */
    public static String getDeviceID(Context context) {
        String deviceId = "";
        try {
            deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        return deviceId;
    }

    /**
     * 判断是否安装了微信
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断新浪微博是否可用
     *
     * @param context
     * @return
     */
    public static boolean isSinaWeiboClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }
        return false;
    }

    /*-------------------------Flyme状态栏--------------------------*/

    /**
     * 判断是魅族操作系统（Flyme）
     *
     * @return
     */
    public static boolean isMeizuFlymeOS() {
        String name = getMeizuFlymeOSFlag();
        return !TextUtils.isEmpty(name) && name.toLowerCase().contains("flyme");
    }

    /**
     * 获取魅族系统版本标识
     */
    public static String getMeizuFlymeOSFlag() {
        return getSystemProperty("ro.build.display.id", "");
    }

    /**
     * 获取MIUI系统版本标识
     */
    public static String getMIUIVersionName() {
        return getSystemProperty("ro.miui.ui.version.name", "");
    }

    /**
     * 获取系统标识
     */
    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
    /*-------------------------Flyme状态栏--------------------------*/

    /*-------------------------MIUI状态栏--------------------------*/

    /**
     * 如何检测小米设备
     * 请使用android.os.Build对象，查询MANUFACTURER和MODEL的值，MANUFACTURER值为Xiaomi即为小米设备
     * AppUtils.getPhoneType().equals("Xiaomi")
     */

    /**
     * 是否为Miui系统 (V6以上)
     *
     * @return
     */
    public static boolean isMiUiOS() {
        String name = getMIUIVersionName();
        return !TextUtils.isEmpty(name) && name.toLowerCase().matches("v[6-9]");
    }

    /**
     * MIUI v6支持4.4及以上版本，调用状态栏透明的方法可以直接用原生的安卓方法
     *
     * @param activity
     * @param on
     */
    @TargetApi(19)
    public static void setMiuiTranslucentStatus(Activity activity, boolean on) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 下面是调用状态栏 是否为darkmode
     *
     * @param activity
     * @param darkmode
     */
    public static void setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*-------------------------MIUI状态栏--------------------------*/

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean getRootPermission(Context context) {
        String path = context.getPackageCodePath();
        return getRootPermission(path);
    }

    /**
     * 修改文件权限
     *
     * @return 文件路径
     */
    public static boolean getRootPermission(String path) {
        Process process = null;
        DataOutputStream os = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
            String cmd = "chmod 777 " + path;
            // 切换到root帐号
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 描述：获取可用内存.
     *
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {
        //获取android当前可用内存大小  
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        //当前系统可用内存 ,将获得的内存大小规格化  
        return memoryInfo.availMem;
    }

    /**
     * 描述：总内存.
     *
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context) {
        //系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            //读取meminfo第一行，系统内存大小 
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            //获得系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue() * 1024;
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Byte转位KB或MB
        return memory;
    }

    /**
     * 获取mac地址.
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info.getMacAddress() == null) {
            return null;
        } else {
            return info.getMacAddress();
        }
    }

    /**
     * 获取SSID地址.
     *
     * @param context
     * @return
     */
    public static String getSSID(Context context) {

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info.getSSID() == null) {
            return null;
        } else {
            return info.getSSID();
        }
    }

    /**
     * 获取IMEI.
     *
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getDeviceId() == null) {
            return null;
        } else {
            return telephonyManager.getDeviceId();
        }
    }

    /**
     * 查询手机内所有应用（包括系统应用）
     *
     * @return
     */
    public static List<PackageInfo> getAllApps(Context context) {
        PackageManager pManager = context.getApplicationContext().getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> pakList = pManager.getInstalledPackages(0);
        return pakList;
    }

    /**
     * 查询手机内非系统应用
     *
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllUserApps(Context context) {
        List<PackageInfo> apps = new ArrayList<>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> pakList = pManager.getInstalledPackages(0);
        for (int i = 0; i < pakList.size(); i++) {
            PackageInfo pak = pakList.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                apps.add(pak);
            }
        }
        return apps;
    }

    /**
     * 手机号码
     *
     * @return
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getLine1Number() == null || telephonyManager.getLine1Number().length() < 11) {
            return null;
        } else {
            return telephonyManager.getLine1Number();
        }
    }

    /**
     * 获取QQ号.
     *
     * @return
     */
    public static String getQQNumber(Context context) {
        String path = "/data/data/com.tencent.mobileqq/shared_prefs/Last_Login.xml";
        getRootPermission(context);
        File file = new File(path);
        getRootPermission(path);
        boolean flag = file.canRead();
        String qq = null;
        if (flag) {
            try {
                FileInputStream is = new FileInputStream(file);
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(is, "UTF-8");
                int event = parser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {

                    switch (event) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            if ("map".equals(parser.getName())) {
                            }
                            if ("string".equals(parser.getName())) {
                                String uin = parser.getAttributeValue(null, "name");
                                if (uin.equals("uin")) {
                                    qq = parser.nextText();
                                    return qq;
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    event = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取WEIXIN号.
     *
     * @return
     */
    public static String getWeiXinNumber(Context context) {
        String path = "/data/data/com.tencent.mm/shared_prefs/com.tencent.mm_preferences.xml";
        getRootPermission(context);
        File file = new File(path);
        getRootPermission(path);
        boolean flag = file.canRead();
        String weixin = null;
        if (flag) {
            try {
                FileInputStream is = new FileInputStream(file);
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(is, "UTF-8");
                int event = parser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {

                    switch (event) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            if ("map".equals(parser.getName())) {
                            }
                            if ("string".equals(parser.getName())) {
                                String nameString = parser.getAttributeValue(null, "name");
                                if (nameString.equals("login_user_name")) {
                                    weixin = parser.nextText();
                                    return weixin;
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        default:
                            break;
                    }
                    event = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取剪贴板内容
     *
     * @param context
     * @return
     */
    public static String getClipboardContent(Context context) {
        if (context == null) {
            return null;
        }
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if (cm == null) {
            return null;
        }
        ClipData data = cm.getPrimaryClip();
        if (data == null) {
            return null;
        }
        ClipData.Item item = data.getItemAt(0);
        return item == null || item.getText() == null ? null : item.getText().toString();
    }

    /**
     * 清除剪贴板内容
     *
     * @param context
     * @return
     */
    public static void clearClipboardContent(Context context) {
        if (context == null) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm == null) {
            return;
        }
        ClipData mClipData = ClipData.newPlainText("clip", "");
        cm.setPrimaryClip(mClipData);
    }

}
