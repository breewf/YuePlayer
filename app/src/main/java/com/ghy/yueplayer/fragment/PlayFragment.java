package com.ghy.yueplayer.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.api.ApiHelper;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.lrcview.LrcView;
import com.ghy.yueplayer.network.ApiRequestCallBackString;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.util.FileUtil;
import com.ghy.yueplayer.util.SPUtil;
import com.ghy.yueplayer.view.MarqueeTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends RxFragment implements View.OnClickListener {


    public PlayFragment() {
        // Required empty public constructor
    }

    @SuppressLint("StaticFieldLeak")
    public static PlayFragment PF;

    /*
     * 加载动画使用
     * */
    private LinearLayout play_layout1;
    private LinearLayout play_layout2;
    private RelativeLayout play_layout3;

    private FrameLayout lrc_layout;
    private FrameLayout cover_layout;
    private LrcView lrcView;
    private TextView tv_down_lrc;

    private ImageView ivBack;
    private ImageView ivNeedle;
    private MarqueeTextView tvMusicName;
    private TextView tvSinger;

    private SeekBar mSeekBar;
    private CircleImageView iv_music_album;
    boolean isRotate = false;//专辑封面是否转动
    private ObjectAnimator rotationAnim;
    private ObjectAnimator needleAnimationOn, needleAnimationOff;

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
    String musicAlbumUri;
    int musicId;

    //歌词路径
    private String lyricPath;
    private String lyricName;
    private String lrcText;

    private PowerManager.WakeLock wakeLock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PF = this;

        //创建歌词文件夹
        lyricPath = FileUtil.createFilePath("Lyric");

        initView();

        initAnim();

        initData();

        setOnClickListener();

    }

    private void initAnim() {
        rotationAnim = ObjectAnimator.ofFloat(iv_music_album, "rotation", 0f, 360f);
        rotationAnim.setDuration(16000);
        rotationAnim.setInterpolator(new LinearInterpolator());
        rotationAnim.setRepeatCount(ValueAnimator.INFINITE);
//        rotationAnim.start();
//        rotationAnim.pause();

        needleAnimationOn = ObjectAnimator.ofFloat(ivNeedle, "rotation", -25, 0);
        ivNeedle.setPivotX(0);
        ivNeedle.setPivotY(0);
        needleAnimationOn.setDuration(600);
        needleAnimationOn.setInterpolator(new LinearInterpolator());

        needleAnimationOff = ObjectAnimator.ofFloat(ivNeedle, "rotation", 0, -25);
        ivNeedle.setPivotX(0);
        ivNeedle.setPivotY(0);
        needleAnimationOff.setDuration(600);
        needleAnimationOff.setInterpolator(new LinearInterpolator());
    }

    private void setOnClickListener() {
        ivBack.setOnClickListener(this);
        iv_music_album.setOnClickListener(this);

        iv_control_pre.setOnClickListener(this);
        iv_control_start_pause.setOnClickListener(this);
        iv_control_next.setOnClickListener(this);
    }

    private void initView() {
        ivBack = (ImageView) getActivity().findViewById(R.id.iv_back);
        ivNeedle = (ImageView) getActivity().findViewById(R.id.iv_needle);
        tvMusicName = (MarqueeTextView) getActivity().findViewById(R.id.tvMusicName);
        tvSinger = (TextView) getActivity().findViewById(R.id.tvSinger);

        mSeekBar = (SeekBar) getActivity().findViewById(R.id.play_seek_bar);
        iv_music_album = (CircleImageView) getActivity().findViewById(R.id.iv_music_album);
        //设置专辑图片宽高
//        ViewGroup.LayoutParams params = iv_music_album.getLayoutParams();
//        params.width = getDisplayWidth() / 7 * 4;
//        params.height = getDisplayWidth() / 7 * 4;
//        iv_music_album.setLayoutParams(params);


        tv_time_duration = (TextView) getActivity().findViewById(R.id.tv_time_duration);
        tv_time_all = (TextView) getActivity().findViewById(R.id.tv_time_all);

        //控制按钮
        iv_control_pre = (ImageView) getActivity().findViewById(R.id.iv_control_pre);
        iv_control_start_pause = (ImageView) getActivity().findViewById(R.id.iv_control_start_pause);
        iv_control_next = (ImageView) getActivity().findViewById(R.id.iv_control_next);

        //加载动画
        play_layout1 = (LinearLayout) getActivity().findViewById(R.id.play_layout1);
        play_layout2 = (LinearLayout) getActivity().findViewById(R.id.play_layout2);
        play_layout3 = (RelativeLayout) getActivity().findViewById(R.id.play_layout3);

        lrc_layout = (FrameLayout) getActivity().findViewById(R.id.lrc_layout);
        cover_layout = (FrameLayout) getActivity().findViewById(R.id.cover_layout);
        lrcView = (LrcView) getActivity().findViewById(R.id.lrc_view);
        tv_down_lrc = (TextView) getActivity().findViewById(R.id.tv_down_lrc);

        play_layout1.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), R.anim.view_show_translate_from_right
        ));
//        play_layout2.startAnimation(AnimationUtils.loadAnimation(
//                getActivity(), R.anim.view_show_scale_from_center
//        ));
//        play_layout3.startAnimation(AnimationUtils.loadAnimation(
//                getActivity(), R.anim.control_view_show_translate_scale_from_bottom
//        ));

        cover_layout.setOnClickListener(view -> setLrcViewVisibility());

        lrc_layout.setOnClickListener(view -> setLrcViewInvisibility());

        tv_down_lrc.setOnClickListener(view -> showDownLrcDialog());

        if (MusicPlayService.player != null && MusicPlayService.player.isPlaying()) {
            handler.post(runnable);
        }

    }

    private void setLrcViewVisibility() {
//        cover_layout.setVisibility(View.INVISIBLE);
//        lrc_layout.setVisibility(View.VISIBLE);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(cover_layout, "alpha", 1f, 0f);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cover_layout.setVisibility(View.INVISIBLE);
                lrc_layout.setVisibility(View.VISIBLE);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(lrc_layout, "alpha", 0f, 1f);
                animator2.setDuration(200);
                animator2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.setDuration(200);
        animator1.start();
        screenLightOnAllTime();
    }

    private void setLrcViewInvisibility() {
//        lrc_layout.setVisibility(View.INVISIBLE);
//        cover_layout.setVisibility(View.VISIBLE);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(lrc_layout, "alpha", 1f, 0f);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lrc_layout.setVisibility(View.INVISIBLE);
                cover_layout.setVisibility(View.VISIBLE);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(cover_layout, "alpha", 0f, 1f);
                animator2.setDuration(200);
                animator2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.setDuration(200);
        animator1.start();
        screenLightOffAllTime();
    }

    /**
     * 关闭屏幕背景常量
     */
    private void screenLightOffAllTime() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /**
     * 屏幕背景常量
     */
    private void screenLightOnAllTime() {
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }
    }

    public static class UI {
        static final Handler HANDLER = new Handler(Looper.getMainLooper());
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://刷新进度条
                    if (MusicPlayService.player != null) {
                        mSeekBar.setMax(MusicPlayService.player.getDuration());
                        mSeekBar.setProgress(MusicPlayService.player.getCurrentPosition());
                    } else {
                        mSeekBar.setMax(100);
                        mSeekBar.setProgress(0);
                    }
                    handler.sendEmptyMessageDelayed(0, 200);
                    break;
                case 1://读取歌词成功
                    //显示歌词
                    if (TextUtils.isEmpty(lrcText)) {
                        showToast("歌词信息读取失败");
                        return;
                    }
                    lrcView.loadLrc(lrcText);
                    lrcView.setOnPlayClickListener(time -> {
                        if (time == -1) {
                            setLrcViewInvisibility();
                        } else {
                            if (MusicPlayService.player == null) return true;
                            MusicPlayService.player.seekTo((int) time);
                            playOrPause();
                        }
                        return true;
                    });
                    break;
                case 2://写入歌词成功
                    //加载歌词
                    loadLrcFile();
                    break;
            }
        }
    };

    /**
     * 歌词runnable
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (MusicPlayService.player == null) return;
            if (MusicPlayService.player.isPlaying()) {
                long time = MusicPlayService.player.getCurrentPosition();
                lrcView.updateTime(time);
            }
            handler.postDelayed(this, 300);
        }
    };

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

        //设置音频流 - STREAM_MUSIC：音乐回放即媒体音量
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //从本地共享文件参数获取数据
        musicName = SPUtil.getStringSP(getActivity(),
                Constant.MUSIC_SP, "musicName");
        musicArtist = SPUtil.getStringSP(getActivity(),
                Constant.MUSIC_SP, "musicArtist");
        musicAlbumUri = SPUtil.getStringSP(getActivity(),
                Constant.MUSIC_SP, "musicAlbumUri");
        musicUrl = SPUtil.getStringSP(getActivity(),
                Constant.MUSIC_SP, "musicUrl");
        musicId = SPUtil.getIntSP(getActivity(),
                Constant.MUSIC_SP, "musicId");

        String lrcArtistTemp;
        lrcArtistTemp = musicArtist;
        //  /storage/emulated/0/YuePlayer/Lyric/小时姑娘/孙悠然-望.lrc
        if (lrcArtistTemp.contains("/")) {//防止文件名错误
            lrcArtistTemp = lrcArtistTemp.substring(0, lrcArtistTemp.indexOf("/"));
        }
        //歌词的命名格式为：歌手名-歌曲名.lrc
        lyricName = lrcArtistTemp + "-" + musicName + ".lrc";

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
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int i, boolean b) {
//                if (b) {
//                    int mSecond = (int) ((i / 100.0) * (MusicPlayService.time));
//                    MusicPlayService.MPS.seekPositionPlay(mSecond);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
                if (handler != null) handler.removeMessages(0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
                if (MusicPlayService.player != null) {
                    MusicPlayService.player.seekTo(bar.getProgress());
                }
                lrcView.updateTime(bar.getProgress());
                handler.sendEmptyMessageDelayed(0, 200);
                checkLrcRunnable();
            }
        });

        playOrPause();

        loadLrcFile();
    }

    /**
     * 加载歌词
     */
    private void loadLrcFile() {
        if (!TextUtils.isEmpty(lyricPath)) {
            File lrcFile = new File(lyricPath + lyricName);
            if (lrcFile.exists()) {
                //加载本地歌词文件
                initLyric(lrcFile);
                tv_down_lrc.setVisibility(View.GONE);
            } else {
                lrcView.loadLrc("");//歌词置空
                tv_down_lrc.setVisibility(View.VISIBLE);
            }
        } else {
            tv_down_lrc.setVisibility(View.VISIBLE);
        }
    }

    private void initLyric(File musicFile) {
        new Thread(() -> {
            lrcText = FileUtil.readFile(musicFile.getAbsolutePath());
            handler.sendEmptyMessage(1);
        }).start();
    }

    private void showDownLrcDialog() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_down_lrc_tips, null);
        EditText editText = view.findViewById(R.id.et_input_number);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.theme(Theme.LIGHT);
        builder.title("下载歌词");
        builder.customView(view, false);
        builder.positiveText("下载")
                .positiveColorRes(R.color.red)
                .onPositive((dialog, which) -> downLoadLrc(editText.getText().toString()));
        builder.negativeText("取消")
                .negativeColorRes(R.color.black_99)
                .onNegative((dialog, which) -> dialog.dismiss());
        builder.cancelable(true);
        builder.canceledOnTouchOutside(false);
        builder.show();
    }

    private void downLoadLrc(String musicId) {
        if (TextUtils.isEmpty(musicId)) {
            showToast("歌曲id不能为空");
            return;
        }

        String requestUrl = "http://music.163.com/api/song/media?id=" + musicId;
        LinkedHashMap<String, Object> requestParams = new LinkedHashMap<>();
        ApiHelper.requestApi(this, requestParams, requestUrl, null,
                new ApiRequestCallBackString() {
                    @Override
                    public void requestCallback(String s, boolean success) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int code = jsonObject.optInt("code");
                            int songStatus = jsonObject.optInt("songStatus");
                            int lyricVersion = jsonObject.optInt("lyricVersion");
                            String lyric = jsonObject.optString("lyric");
                            if (code != 200) return;
                            if (TextUtils.isEmpty(lyric)) return;
                            //写文件
                            new Thread(() -> {
                                FileUtil.writeFile(lyric, lyricPath + lyricName);
                                handler.sendEmptyMessage(2);
                            }).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

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
            setLrcViewVisibility();
        } else if (view == iv_control_pre) {
            MusicPlayService.MPS.stopPlay();
            MusicPlayOver();
            MusicPlayService.MPS.playPre();
        } else if (view == iv_control_start_pause) {
            //播放or暂停
            //参数为保存的上次播放的歌曲路径，如果是直接到播放页面然后点击播放（不是点击音乐列表），
            //则播放上次记忆的歌曲
            MusicPlayService.MPS.playOrPause(musicUrl, musicName, musicArtist, musicId);
            playOrPause();
        } else if (view == iv_control_next) {
            MusicPlayService.MPS.stopPlay();
            MusicPlayOver();
            MusicPlayService.MPS.playNext();
        } else if (view == ivBack) {
            clearAlbumAnim();
            getActivity().onBackPressed();
        }
    }

    /**
     * 播放或暂停刷新界面
     */
    private void playOrPause() {
        if (MusicPlayService.MPS != null && MusicPlayService.MPS.isPlay()) {
            handler.sendEmptyMessage(0);
            iv_control_start_pause.setImageResource(R.mipmap.ic_pause);
            startAlbumAnim();
        } else {
            handler.removeMessages(0);
            iv_control_start_pause.setImageResource(R.mipmap.ic_play);
            stopAlbumAnim();
        }

        checkLrcRunnable();
    }

    /**
     * 歌词滚动Runnable
     */
    private void checkLrcRunnable() {
        if (MusicPlayService.player != null && MusicPlayService.player.isPlaying()) {
            handler.post(runnable);
        } else {
            handler.removeCallbacks(runnable);
        }
    }

    private void startAlbumAnim() {
        if (rotationAnim != null) {
            if (!rotationAnim.isRunning()) {
                UI.HANDLER.postDelayed(() -> rotationAnim.start(), 800);
            } else {
                rotationAnim.resume();
            }
        }
        if (needleAnimationOn != null) needleAnimationOn.start();
        isRotate = true;
    }

    private void stopAlbumAnim() {
        if (rotationAnim != null) rotationAnim.pause();
        if (needleAnimationOff != null) needleAnimationOff.start();
        isRotate = false;
    }

    private void clearAlbumAnim() {
        if (rotationAnim != null) {
            rotationAnim.start();//重置
            rotationAnim.pause();//暂停
            rotationAnim.cancel();//取消
        }
        isRotate = false;
    }

    /*
     * 把歌曲总时长毫秒数转换为时间格式
     * */
    private String getFormatTime(int time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式
        return formatter.format(time);
    }

    /*
     * 计时任务
     * */
    private class CurrentPlayTimerTask extends TimerTask {

        @Override
        public void run() {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                        tv_time_duration.setText(getFormatTime(
                                MusicPlayService.MPS.getCurrentDuration()));
                    }
            );
        }
    }

    private int getSeekBarProgress() {
        int timeAll = MusicPlayService.time;
        int timeCurrent = MusicPlayService.MPS.getCurrentDuration();
        double percent = (double) timeCurrent / timeAll;
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
        mSeekBar.setProgress(0);
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

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public void backPressed() {
        clearAlbumAnim();
    }

    /*
     * 按Home键进入后台
     * */
    public void homeBackground() {
        UI.HANDLER.postDelayed(() -> {
            if (isRotate) {
                stopAlbumAnim();
            }
        }, 600);
        //进入后台取消常亮
        screenLightOffAllTime();
    }

    /*
     * 从后台返回
     * */
    public void fromBackgroundBack() {
        UI.HANDLER.postDelayed(() -> {
            if (!isRotate) {
                if (MusicPlayService.MPS.isPlay()) {
                    startAlbumAnim();
                }
            }
        }, 600);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (PF != null) PF = null;
        if (handler != null) handler.removeCallbacksAndMessages(null);
        if (UI.HANDLER != null) UI.HANDLER.removeCallbacksAndMessages(null);
        screenLightOffAllTime();
    }

}
