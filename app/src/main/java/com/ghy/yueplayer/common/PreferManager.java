package com.ghy.yueplayer.common;

import android.annotation.SuppressLint;
import android.content.Context;

import com.ghy.yueplayer.common.securitysp.SecuritySharedPreference;


/**
 * Desc:   SharedPreferences 管理类
 */
public class PreferManager {

    public static final String SCREEN_WIDTH = "screen_width";//屏幕宽度
    public static final String SCREEN_HEIGHT = "screen_height";//屏幕高度
    public static final String STATUS_BAR_HEIGHT = "status_bar_height";//状态栏宽度
    public static final String IMEI_CODE = "imei";//IMEI号

    public static final String ALBUM_COLOR = "album_color";//专辑图背景虚化


    @SuppressLint("StaticFieldLeak")
    private static SecuritySharedPreference sp;//使用加密的SharedPreference
    private static SecuritySharedPreference.SecurityEditor edit;

    @SuppressLint("CommitPrefEdits")
    public static void init(Context context) {
        if (sp == null) {
            sp = new SecuritySharedPreference(context, "YuePlayer", Context.MODE_PRIVATE);
            edit = sp.edit();
        }
    }

    public static void setString(String key, String value) {
        edit.putString(key, value);
        edit.apply();
    }

    public static String getString(String key) {
        return sp.getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public static void setLong(String key, long value) {
        edit.putLong(key, value);
        edit.apply();
    }

    public static long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public static void setBoolean(String key, boolean value) {
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public static void setInt(String key, int value) {
        edit.putInt(key, value);
        edit.apply();
    }

    public static int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public static void setFloat(String key, float value) {
        edit.putFloat(key, value);
        edit.apply();
    }

    public static float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

}
