package com.ghy.yueplayer.activity;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.view.MyVisualizerView;


public class MusicFxActivity extends AppCompatActivity {

    ImageView app_icon_back;
    TextView tv_activity_name;

    // 定义播放声音的MediaPlayer
    private MediaPlayer mPlayer;
    // 定义系统的频谱
    private Visualizer mVisualizer;
    // 定义系统的均衡器
    private Equalizer mEqualizer;
    // 定义系统的重低音控制器
    private BassBoost mBass;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_fx);
        initToolBar();

        layout = (LinearLayout) findViewById(R.id.fx_content_layout);
        //设置音频流 - STREAM_MUSIC：音乐回放即媒体音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (MusicPlayService.player != null) {
            mPlayer = MusicPlayService.player;
        } else {
            mPlayer = new MediaPlayer();
        }

        // 初始化示波器
        setupVisualizer();
        // 初始化均衡控制器
        setupEqualizer();
        // 初始化重低音控制器
        setupBassBoost();
    }

    private void initToolBar() {
        app_icon_back = (ImageView) findViewById(R.id.app_icon_back);
        tv_activity_name = (TextView) findViewById(R.id.tv_activity_name);
        tv_activity_name.setText("MusicFx");
        app_icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 初始化频谱
     */
    private void setupVisualizer() {
        // 创建MyVisualizerView组件，用于显示波形图
        final MyVisualizerView mVisualizerView =
                new MyVisualizerView(this);
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (120f * getResources().getDisplayMetrics().density)));
        // 将MyVisualizerView组件添加到layout容器中
        layout.addView(mVisualizerView);
        // 以MediaPlayer的AudioSessionId创建Visualizer
        // 相当于设置Visualizer负责显示该MediaPlayer的音频数据
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        //设置需要转换的音乐内容长度，专业的说这就是采样，该采样值一般为2的指数倍，如64,128,256,512,1024。
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 为mVisualizer设置监听器
        /*
         * Visualizer.setDataCaptureListener(OnDataCaptureListener listener, int rate, boolean waveform, boolean fft
         *
         *      listener，表监听函数，匿名内部类实现该接口，该接口需要实现两个函数
                rate， 表示采样的周期，即隔多久采样一次，联系前文就是隔多久采样128个数据
                iswave，是波形信号
                isfft，是FFT信号，表示是获取波形信号还是频域信号

         */
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    //这个回调应该采集的是快速傅里叶变换有关的数据
                    @Override
                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] fft, int samplingRate) {
                    }

                    //这个回调应该采集的是波形数据
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] waveform, int samplingRate) {
                        // 用waveform波形数据更新mVisualizerView组件
                        mVisualizerView.updateVisualizer(waveform);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
    }

    /**
     * 初始化均衡控制器
     */
    private void setupEqualizer() {
        // 以MediaPlayer的AudioSessionId创建Equalizer
        // 相当于设置Equalizer负责控制该MediaPlayer
        mEqualizer = new Equalizer(0, mPlayer.getAudioSessionId());
        // 启用均衡控制效果
        mEqualizer.setEnabled(true);
        TextView eqTitle = new TextView(this);
        eqTitle.setText("均衡器：");
        eqTitle.setTextColor(Color.WHITE);
        layout.addView(eqTitle);
        // 获取均衡控制器支持最小值和最大值
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];//第一个下标为最低的限度范围
        short maxEQLevel = mEqualizer.getBandLevelRange()[1];  // 第二个下标为最高的限度范围
        // 获取均衡控制器支持的所有频率
        short brands = mEqualizer.getNumberOfBands();
        for (short i = 0; i < brands; i++) {
            TextView eqTextView = new TextView(this);
            // 创建一个TextView，用于显示频率
            eqTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            eqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            // 设置该均衡控制器的频率
            eqTextView.setText((mEqualizer.getCenterFreq(i) / 1000) + " Hz");
            layout.addView(eqTextView);
            // 创建一个水平排列组件的LinearLayout
            LinearLayout tmpLayout = new LinearLayout(this);
            tmpLayout.setOrientation(LinearLayout.HORIZONTAL);
            // 创建显示均衡控制器最小值的TextView
            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            // 显示均衡控制器的最小值
            minDbTextView.setText((minEQLevel / 100) + " dB");
            // 创建显示均衡控制器最大值的TextView
            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            // 显示均衡控制器的最大值
            maxDbTextView.setText((maxEQLevel / 100) + " dB");
            LinearLayout.LayoutParams layoutParams = new
                    LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            // 定义SeekBar做为调整工具
            SeekBar bar = new SeekBar(this);
            bar.setLayoutParams(layoutParams);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(mEqualizer.getBandLevel(i));
            final short brand = i;
            // 为SeekBar的拖动事件设置事件监听器
            bar.setOnSeekBarChangeListener(new SeekBar
                    .OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {
                    // 设置该频率的均衡值
                    mEqualizer.setBandLevel(brand,
                            (short) (progress + minEQLevel));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            // 使用水平排列组件的LinearLayout“盛装”3个组件
            tmpLayout.addView(minDbTextView);
            tmpLayout.addView(bar);
            tmpLayout.addView(maxDbTextView);
            // 将水平排列组件的LinearLayout添加到myLayout容器中
            layout.addView(tmpLayout);
        }
    }

    /**
     * 初始化重低音控制器
     */
    private void setupBassBoost() {
        // 以MediaPlayer的AudioSessionId创建BassBoost
        // 相当于设置BassBoost负责控制该MediaPlayer
        mBass = new BassBoost(0, mPlayer.getAudioSessionId());
        // 设置启用重低音效果
        mBass.setEnabled(true);
        TextView bbTitle = new TextView(this);
        bbTitle.setText("重低音：");
        bbTitle.setTextColor(Color.WHITE);
        layout.addView(bbTitle);
        // 使用SeekBar做为重低音的调整工具
        SeekBar bar = new SeekBar(this);
        // 重低音的范围为0～1000
        bar.setMax(1000);
        bar.setProgress(0);
        // 为SeekBar的拖动事件设置事件监听器
        bar.setOnSeekBarChangeListener(new SeekBar
                .OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar
                    , int progress, boolean fromUser) {
                // 设置重低音的强度
                mBass.setStrength((short) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        layout.addView(bar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放所有对象
        if (mVisualizer != null) mVisualizer.release();
        if (mEqualizer != null) mEqualizer.release();
        if (mBass != null) mBass.release();
    }
}
