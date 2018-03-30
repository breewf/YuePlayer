package com.ghy.yueplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.util.SPUtil;

import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service {
    public static TimeService TS;

    private Timer timer;
    private CountPlayTimerTask timerTask;

    public static int timeThis;

    public TimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TS = this;
        timeThis=0;
        startTimeCount();
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    /*
    * 开始计时统计
    * */
    private void startTimeCount() {
        if (timer == null) {
            timer = new Timer();
            timerTask = new CountPlayTimerTask();
            timer.schedule(timerTask, 0, 1000);
        }
    }

    class CountPlayTimerTask extends TimerTask {

        @Override
        public void run() {
            timeThis = timeThis + 1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveThisTimeToAllTime();
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    /*
    * 退出时保存这次启动时间，并加到总时间
    * */
    private void saveThisTimeToAllTime() {
        int timeAll= SPUtil.getIntSP(this, Constant.MUSIC_SP,"useTimeAll");
        if (timeAll==-1){
            timeAll=0;
        }
        timeAll=timeAll+timeThis;
        //保存
        SPUtil.saveSP(this,Constant.MUSIC_SP,"useTimeAll",timeAll);
    }
}
