package com.ghy.yueplayer.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.util.SPUtil;
import com.ghy.yueplayer.view.SeekArc;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment implements View.OnClickListener {


    public PlayFragment() {
        // Required empty public constructor
    }

    public static PlayFragment PFInstance;

    /*
    * 加载动画使用
    * */
    private RelativeLayout play_layout1;
    private RelativeLayout play_layout2;
    private RelativeLayout play_layout3;

    private TextView tvMusicName;
    private TextView tvSinger;

    private SeekArc mSeekArc;
    private CircleImageView iv_music_album;
    boolean isRotate = false;//专辑封面是否转动

    private Timer timer;
    private CurrentPlayTimerTask timerTask;
    private TextView tv_time_duration;
    private TextView tv_time_all;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private ImageView iv_control_pre;
    private ImageView iv_control_start_pause;
    private ImageView iv_control_next;

    String musicUrl;
    String musicName;
    String musicArtist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PFInstance = this;

        initView();

        initData();

        setOnClickListener();
    }

    private void setOnClickListener() {
        iv_music_album.setOnClickListener(this);

        iv_control_pre.setOnClickListener(this);
        iv_control_start_pause.setOnClickListener(this);
        iv_control_next.setOnClickListener(this);

    }

    private void startAlbumAnim() {
        Animation rotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.image_rotate);
        LinearInterpolator lin = new LinearInterpolator();//匀速转动
        rotateAnim.setInterpolator(lin);
        iv_music_album.startAnimation(rotateAnim);
        isRotate = true;
    }

    private void initView() {
        tvMusicName = (TextView) getActivity().findViewById(R.id.tvMusicName);
        tvSinger = (TextView) getActivity().findViewById(R.id.tvSinger);

        mSeekArc = (SeekArc) getActivity().findViewById(R.id.seekArc);
        iv_music_album = (CircleImageView) getActivity().findViewById(R.id.iv_music_album);
        //设置专辑图片宽高
        ViewGroup.LayoutParams params = iv_music_album.getLayoutParams();
        params.width = getDisplayWidth()/7*4;
        params.height = getDisplayWidth()/7*4;
        iv_music_album.setLayoutParams(params);


        tv_time_duration = (TextView) getActivity().findViewById(R.id.tv_time_duration);
        tv_time_all = (TextView) getActivity().findViewById(R.id.tv_time_all);

        //控制按钮
        iv_control_pre = (ImageView) getActivity().findViewById(R.id.iv_control_pre);
        iv_control_start_pause = (ImageView) getActivity().findViewById(R.id.iv_control_start_pause);
        iv_control_next = (ImageView) getActivity().findViewById(R.id.iv_control_next);

        //加载动画
        play_layout1 = (RelativeLayout) getActivity().findViewById(R.id.play_layout1);
        play_layout2 = (RelativeLayout) getActivity().findViewById(R.id.play_layout2);
        play_layout3 = (RelativeLayout) getActivity().findViewById(R.id.play_layout3);

        play_layout1.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), R.anim.view_show_translate_scale_from_top
        ));
        play_layout2.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), R.anim.view_show_scale_from_center
        ));
        play_layout3.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), R.anim.control_view_show_translate_scale_from_bottom
        ));

    }

    /*
    * 获取手机屏幕宽度
    * */
    private int getDisplayWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels; //当前分辨率宽度
    }


    /*
    * 加载歌曲播放数据方法
    * */
    public void initData() {

        //从本地共享文件参数获取数据
        musicName = SPUtil.getStringSP(getActivity(),
                Constant.MUSIC_SP, "musicName");
        musicArtist = SPUtil.getStringSP(getActivity(),
                Constant.MUSIC_SP, "musicArtist");
        String musicAlbumUri = SPUtil.getStringSP(getActivity(),
                Constant.MUSIC_SP, "musicAlbumUri");
        musicUrl = SPUtil.getStringSP(getActivity(),
                Constant.MUSIC_SP, "musicUrl");


        tvMusicName.setText(musicName.equals("") ? "未知歌曲" : musicName);
        tvSinger.setText(musicArtist.equals("") ? "未知艺术家" : musicArtist);

        //加载专辑封面
        imageLoader = ImageLoader.getInstance();
        initLoader();
        imageLoader.displayImage(musicAlbumUri,
                iv_music_album, options);

        //加载保存的歌曲总时长
        int allDuration = SPUtil.getIntSP(getActivity(),
                Constant.MUSIC_SP, "musicAllDuration");
        tv_time_all.setText(allDuration == -1 ? "00:00" : getFormatTime(allDuration));

        //计时任务
        if (timer == null) {
            timer = new Timer();
            timerTask = new CurrentPlayTimerTask();
            timer.schedule(timerTask, 0, 1000);
        }

        //监听事件
        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

                if (fromUser) {
                    int position = (int) (((double) progress / 1.5));
                    int mSecond = (int) ((position / 100.0) * (MusicPlayService.time));
                    MusicPlayService.MPSInstance.seekPositionPlay(mSecond);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }
        });

        playOrPause();

    }

    private void initLoader() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.default_artist)
                .showImageForEmptyUri(R.mipmap.default_artist)
                .showImageOnFail(R.mipmap.default_artist).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();
    }

    @Override
    public void onClick(View view) {
        if (view == iv_music_album) {
            if (isRotate) {
                stopAlbumAnim();
            } else {
                startAlbumAnim();
            }
        } else if (view == iv_control_pre) {
            MusicPlayService.MPSInstance.stopPlay();
            MusicPlayOver();
            MusicPlayService.MPSInstance.playPre();
        } else if (view == iv_control_start_pause) {
            //播放or暂停
            //参数为保存的上次播放的歌曲路径，如果是直接到播放页面然后点击播放（不是点击音乐列表），
            //则播放上次记忆的歌曲
            MusicPlayService.MPSInstance.playOrPause(musicUrl,musicName,musicArtist);
            playOrPause();
        } else if (view == iv_control_next) {
            MusicPlayService.MPSInstance.stopPlay();
            MusicPlayOver();
            MusicPlayService.MPSInstance.playNext();
        }
    }

    /*
    * 播放或暂停界面显示
    * */
    private void playOrPause() {
        //播放or暂停按钮显示
        playOrPauseControlView();
        //专辑封面旋转
        albumAnimJudge();
    }

    private void albumAnimJudge() {
        if (MusicPlayService.MPSInstance.isPlay()) {
            startAlbumAnimDelay();
        } else {
            stopAlbumAnim();
        }
    }

    private void startAlbumAnimDelay() {
        //封面转动
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAlbumAnim();
            }
        }, 1000);
    }

    private void playOrPauseControlView() {
        if (MusicPlayService.MPSInstance.isPlay()) {
            iv_control_start_pause.setImageResource(R.mipmap.icon_play_pause);
        } else {
            iv_control_start_pause.setImageResource(R.mipmap.icon_play_play);
        }
    }

    private void stopAlbumAnim() {
        iv_music_album.clearAnimation();
        isRotate = false;
    }

    /*
    * 把歌曲总时长毫秒数转换为时间格式
    * */
    private String getFormatTime(int time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式
        String format = formatter.format(time);
        return format;
    }

    /*
    * 计时任务
    * */
    class CurrentPlayTimerTask extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            tv_time_duration.setText(getFormatTime(
                                    MusicPlayService.MPSInstance.getCurrentDuration()));
                            //seekBar
                            mSeekArc.setProgress(
                                    getSeekBarProgress());
                        }
                    }
            );
        }
    }

    private int getSeekBarProgress() {
        int timeAll = MusicPlayService.time;
        int timeCurrent = MusicPlayService.MPSInstance.getCurrentDuration();
        double percent = (double) timeCurrent / timeAll * 1.5;//mSeekArc的progress值为150
        percent = ((int) (percent * 100)) / 100.0;
        return (int) (percent * 100);
    }

    /*
    * 单首歌曲播放完毕
    * */
    public void MusicPlayOver() {
        //控制按钮置为播放
        iv_control_start_pause.setImageResource(R.mipmap.icon_play_play);
        //播放时间清零，seekBar清零
        tv_time_duration.setText("00:00");
        mSeekArc.setProgress(0);
        //若封面转动则停止转动
        if (isRotate) {
            stopAlbumAnim();
        }
        //停止计时任务
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /*
    * 按Home键进入后台
    * */
    public void homeBackground(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRotate){
                    stopAlbumAnim();
                }
            }
        },600);
    }

    /*
   * 从后台返回
   * */
    public void fromBackgroundBack(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRotate){
                    if (MusicPlayService.MPSInstance.isPlay()){
                        startAlbumAnim();
                    }
                }
            }
        },600);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (PFInstance != null) {
            PFInstance = null;
        }
    }

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}
