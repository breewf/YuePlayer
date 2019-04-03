package com.ghy.yueplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.service.TimeService;
import com.ghy.yueplayer.util.SPUtil;
import com.ghy.yueplayer.view.HeroTextView;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class TimeActivity extends AppCompatActivity {

    ImageView app_icon_back;
    TextView tv_activity_name;

    HeroTextView tv_time_this;
    HeroTextView tv_time_this_hour;
    HeroTextView tv_time_all_use;
    HeroTextView tv_time_all_hour;
    TextView tv_nick;

    Timer timer;
    CountTimerTask timerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        initToolBar();
        initView();
        initData();
    }

    private void initData() {

        if (timer == null) {
            timer = new Timer();
            timerTask = new CountTimerTask();
            timer.schedule(timerTask, 0, 1000);
        }

        int timeAll = SPUtil.getIntSP(this, Constant.MUSIC_SP, "useTimeAll");
        if (timeAll == -1) {
            timeAll = 0;
        }
        tv_time_all_use.setText(getTime2(timeAll));

        if (timeAll >= 360000) {
            //100个小时以上
            tv_nick.setText("无人能及");
        } else if (timeAll >= 180000 && timeAll < 360000) {
            //50个小时-100个小时
            tv_nick.setText("居高临下");
        } else if (timeAll >= 144000 && timeAll < 180000) {
            //40个小时-50个小时
            tv_nick.setText("音乐王者");
        } else if (timeAll >= 108000 && timeAll < 144000) {
            //30个小时-40个小时
            tv_nick.setText("音乐狂人");
        } else if (timeAll >= 72000 && timeAll < 108000) {
            //20个小时-30个小时
            tv_nick.setText("音乐发烧");
        } else if (timeAll >= 36000 && timeAll < 72000) {
            //10个小时-20个小时
            tv_nick.setText("音乐达人");
        } else if (timeAll >= 18000 && timeAll < 36000) {
            //5个小时-10个小时
            tv_nick.setText("爱上音乐");
        } else if (timeAll >= 14400 && timeAll < 18000) {
            //4个小时-5个小时
            tv_nick.setText("喜欢音乐");
        } else if (timeAll >= 10800 && timeAll < 14400) {
            //3个小时-4个小时
            tv_nick.setText("悦听粉丝");
        } else if (timeAll >= 7200 && timeAll < 10800) {
            //2个小时-3个小时
            tv_nick.setText("悦听粉丝");
        } else if (timeAll >= 3600 && timeAll < 7200) {
            //1个小时-2个小时
            tv_nick.setText("我喜欢听");
        } else if (timeAll >= 1800 && timeAll < 3600) {
            //30分钟-1个小时
            tv_nick.setText("随便听听");
        } else if (timeAll < 1800) {
            //30分钟以下
            tv_nick.setText("初入茅庐");
        }
    }

    class CountTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_time_this.setText(getTime(TimeService.timeThis));
                }
            });
        }
    }

    private void initView() {

        tv_time_this = findViewById(R.id.tv_time_this);
        tv_time_this_hour = findViewById(R.id.tv_time_this_hour);
        tv_time_all_use = findViewById(R.id.tv_time_all_use);
        tv_time_all_hour = findViewById(R.id.tv_time_all_hour);
        tv_nick = findViewById(R.id.tv_nick);

    }

    private void initToolBar() {
        app_icon_back = (ImageView) findViewById(R.id.app_icon_back);
        tv_activity_name = (TextView) findViewById(R.id.tv_activity_name);
        tv_activity_name.setText("Time");
        app_icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String getTime(int timeSecond) {
        int hour = timeSecond / 3600;//获得小时
        String hourStr = "";
        if (String.valueOf(hour).length() == 1) {
            hourStr = "0" + hour;
        }
        tv_time_this_hour.setText(hourStr + ":");
        int time = timeSecond % 3600;//获得分秒
        return getFormatTime(time * 1000);//单位为毫秒，time单位是秒
    }

    private String getTime2(int timeSecond) {
        int hour = timeSecond / 3600;//获得小时
        String hourStr = "";
        if (String.valueOf(hour).length() == 1) {
            hourStr = "0" + hour;
        }
        tv_time_all_hour.setText(hourStr + ":");
        int time = timeSecond % 3600;//获得分秒
        return getFormatTime(time * 1000);//单位为毫秒，time单位是秒
    }

    private String getFormatTime(int time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式
        String format = formatter.format(time);
        return format;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
