package com.ghy.yueplayer.common;

import android.annotation.SuppressLint;
import android.content.Context;

import com.ghy.yueplayer.common.securitysp.SecuritySharedPreference;


/**
 * Desc:SharedPreferences
 *
 * @author HY
 */
public class PreferManager {

    /**
     * 屏幕宽度
     */
    public static final String SCREEN_WIDTH = "screen_width";
    /**
     * 屏幕高度
     */
    public static final String SCREEN_HEIGHT = "screen_height";
    /**
     * 状态栏宽度
     */
    public static final String STATUS_BAR_HEIGHT = "status_bar_height";
    /**
     * IMEI号
     */
    public static final String IMEI_CODE = "imei";

    /**
     * 专辑图背景虚化
     */
    public static final String ALBUM_COLOR = "album_color";
    /**
     * 音乐音符
     */
    public static final String MUSIC_NOTE = "music_note";
    /**
     * 均衡器数值
     */
    public static final String EQUALIZER1 = "equalizer1";
    public static final String EQUALIZER2 = "equalizer2";
    public static final String EQUALIZER3 = "equalizer3";
    public static final String EQUALIZER4 = "equalizer4";
    public static final String EQUALIZER5 = "equalizer5";
    /**
     * 重低音数值
     */
    public static final String BASS = "bass";

    public static final String LIST_ANIM = "list_anim";

    /**
     * YUE 动画
     */
    public static final String MAIN_BOTTOM_ANIM = "main_bottom_anim";
    /**
     * 上一个动画类型--用于动画的暂停、恢复操作
     */
    public static final String MAIN_LAST_ANIM = "main_last_anim";

    /**
     * CIRCLE 动画
     */
    public static final String MAIN_CIRCLE_ANIM = "main_circle_anim";

    /**
     * 正在播放的歌曲id
     */
    public static final String PLAYING_ID = "playing_id";


    @SuppressLint("StaticFieldLeak")
    private static SecuritySharedPreference sp;
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
