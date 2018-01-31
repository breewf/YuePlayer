package com.ghy.yueplayer.util;

import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by GHY on 2018/1/6.
 * Modify by GHY on 2018/1/6.
 * ClassDesc:时间日期工具类
 **/
public class TimeUtils {

    /**
     * 获取日期-年月日时分秒
     *
     * @param time
     * @return
     */
    public static String getFullDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getDateStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getTimeStrHMS(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getTimeStrHMSSSS(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getTimeStrHM(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getDateStrToSeconds(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getDateStrToMinutes(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getDateCnStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getDateEnStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getDateMonthDayCnStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getDateMonthDayEnStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }
    public static String getDateStrSeconds(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    /**
     * 初始化当前日期是星期几
     *
     * @param weekday
     */
    public static void initWeekday(TextView textView, String weekday) {
        Calendar calendar = Calendar.getInstance();
        int weekDay;
        try {
            weekDay = Integer.valueOf(weekday);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            weekDay = getWeedDayIndex(calendar);
        }
        switch (weekDay) {
            case 1:
                textView.setText("星期一");
                break;
            case 2:
                textView.setText("星期二");
                break;
            case 3:
                textView.setText("星期三");
                break;
            case 4:
                textView.setText("星期四");
                break;
            case 5:
                textView.setText("星期五");
                break;
            case 6:
                textView.setText("星期六");
                break;
            case 7:
                textView.setText("星期日");
                break;
        }
    }

    private static int getWeedDayIndex(Calendar calendar) {//按照国内的习惯获取当天是一个星期的第几天（星期一:1 星期日:7）
        boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if (isFirstSunday) {
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }
        return weekDay;
    }

    /**
     * 获取当前时间 到秒
     *
     * @return
     */
    public static String getNowDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    public static String getDate2String(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(time);

    }

    public static String getDate2MonthString(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return format.format(time);

    }

    /**
     * 将当前时间转化为毫秒
     *
     * @param time
     * @return
     */
    public static long getNowTime2Long(String time) {
        long nowtime = 0;
        String[] a = time.split(":");
        if (a.length == 3) {
            try {
                long b = Long.parseLong(a[0]);
                nowtime += b * 60 * 60 * 1000;
                long c = Long.parseLong(a[1]);
                nowtime += c * 60 * 1000;
                long d = Long.parseLong(a[2]);
                nowtime += d * 1000;
            } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return nowtime;
    }

    /**
     * 获取当前day
     *
     * @return
     */
    public static String getNowDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());//可根据不同样式请求显示不同日期格式，要显示星期可以添加E参数
        return simpleDateFormat.format(new Date());
    }

    /**
     * 不能选择未来时间
     *
     * @param time
     * @return
     */
    public static boolean isFutureTime(String time) {
        long chooseTime = timeFromStr(time);
        long currTime = timeFromStr(getNowDay());
        return chooseTime <= currTime;
    }

    /**
     * 判断正确的时间格式
     * 1970年以后
     *
     * @param time
     * @return
     */
    public static boolean isAfter1970(String time) {
        long chooseTime = timeFromStr(time);
        long currTime = timeFromStr("1970-01-01");
        return chooseTime >= currTime;
    }

    public static long timeFromStr(String timeStr) {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(timeStr);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    /**
     * 获取当天日期
     *
     * @return 20170309
     */
    public static String getTodayDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    public static String getTimeStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getTimeStrShort(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static String getStepKey(String preffix) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return preffix + ":" + formater.format(Calendar.getInstance().getTime());
    }
}
