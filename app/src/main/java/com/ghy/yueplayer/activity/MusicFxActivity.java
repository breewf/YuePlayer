package com.ghy.yueplayer.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.component.verticalseekbar.VerticalSeekBar;
import com.ghy.yueplayer.service.MusicPlayService;

import static com.ghy.yueplayer.service.MusicPlayService.mBass;
import static com.ghy.yueplayer.service.MusicPlayService.mEqualizer;


public class MusicFxActivity extends AppCompatActivity {

    ImageView app_icon_back;
    TextView tv_activity_name;

    private TextView tvBottom1;
    private TextView tvBottom2;
    private TextView tvBottom3;
    private TextView tvBottom4;
    private TextView tvBottom5;

    private VerticalSeekBar mVerticalSeekBar1;
    private VerticalSeekBar mVerticalSeekBar2;
    private VerticalSeekBar mVerticalSeekBar3;
    private VerticalSeekBar mVerticalSeekBar4;
    private VerticalSeekBar mVerticalSeekBar5;

    private SeekBar mSeekBarBass;

    private TextView[] tvBottom = new TextView[5];
    private VerticalSeekBar[] verticalSeekBars = new VerticalSeekBar[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_fx);
        initToolBar();

        tvBottom1 = (TextView) findViewById(R.id.tv_bottom1);
        tvBottom2 = (TextView) findViewById(R.id.tv_bottom2);
        tvBottom3 = (TextView) findViewById(R.id.tv_bottom3);
        tvBottom4 = (TextView) findViewById(R.id.tv_bottom4);
        tvBottom5 = (TextView) findViewById(R.id.tv_bottom5);
        mVerticalSeekBar1 = (VerticalSeekBar) findViewById(R.id.ver_seek_bar1);
        mVerticalSeekBar2 = (VerticalSeekBar) findViewById(R.id.ver_seek_bar2);
        mVerticalSeekBar3 = (VerticalSeekBar) findViewById(R.id.ver_seek_bar3);
        mVerticalSeekBar4 = (VerticalSeekBar) findViewById(R.id.ver_seek_bar4);
        mVerticalSeekBar5 = (VerticalSeekBar) findViewById(R.id.ver_seek_bar5);
        mSeekBarBass = (SeekBar) findViewById(R.id.seek_bass);
        tvBottom[0] = tvBottom1;
        tvBottom[1] = tvBottom2;
        tvBottom[2] = tvBottom3;
        tvBottom[3] = tvBottom4;
        tvBottom[4] = tvBottom5;
        verticalSeekBars[0] = mVerticalSeekBar1;
        verticalSeekBars[1] = mVerticalSeekBar2;
        verticalSeekBars[2] = mVerticalSeekBar3;
        verticalSeekBars[3] = mVerticalSeekBar4;
        verticalSeekBars[4] = mVerticalSeekBar5;
        //设置音频流 - STREAM_MUSIC：音乐回放即媒体音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
     * 初始化均衡控制器
     */
    private void setupEqualizer() {
        // 以MediaPlayer的AudioSessionId创建Equalizer
        // 相当于设置Equalizer负责控制该MediaPlayer
//        mEqualizer = new Equalizer(0, mPlayer.getAudioSessionId());
//        // 启用均衡控制效果
//        mEqualizer.setEnabled(true);
        if (mEqualizer == null) {
            mEqualizer = new Equalizer(0, new MediaPlayer().getAudioSessionId());
        }
        // 获取均衡控制器支持最小值和最大值
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];//第一个下标为最低的限度范围 -1500
        short maxEQLevel = mEqualizer.getBandLevelRange()[1];  // 第二个下标为最高的限度范围 1500
        // 获取均衡控制器支持的所有频率
        short brands = mEqualizer.getNumberOfBands();
        for (short i = 0; i < brands; i++) {
            if (i > 4) return;
            tvBottom[i].setText("" + (mEqualizer.getCenterFreq(i) / 1000) + "Hz");
            int maxValue = maxEQLevel - minEQLevel;
            verticalSeekBars[i].setMax(maxValue);
            int bandLevel = getEQUALIZER(i);
            bandLevel = bandLevel == 0 ? (maxEQLevel - minEQLevel) / 2 : bandLevel;
//            verticalSeekBars[i].setProgress(mEqualizer.getBandLevel(i));
            verticalSeekBars[i].setProgress(bandLevel + maxEQLevel);
            if (mEqualizer != null) mEqualizer.setBandLevel(i, (short) bandLevel);
            final short brand = i;
            // 为SeekBar的拖动事件设置事件监听器
            verticalSeekBars[i].setOnSeekBarChangeListener(new SeekBar
                    .OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {
                    // 设置该频率的均衡值
//                    mEqualizer.setBandLevel(brand,
//                            (short) (progress + minEQLevel));
                    Log.i("musicFx", "brand-->>" + brand + "--level-->>" + (short) (progress + minEQLevel));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mEqualizer == null) return;
                    mEqualizer.setBandLevel(brand,
                            (short) (seekBar.getProgress() + minEQLevel));
                    saveEQUALIZER(brand, (seekBar.getProgress() + minEQLevel));
                    Log.i("musicFx", "brand--onStopTrackingTouch-->>" + brand + "--level-->>" + (short) (seekBar.getProgress() + minEQLevel));
                }
            });
        }
    }

    private void saveEQUALIZER(short brand, int level) {
        if (brand == 0) {
            PreferManager.setInt(PreferManager.EQUALIZER1, level);
        } else if (brand == 1) {
            PreferManager.setInt(PreferManager.EQUALIZER2, level);
        } else if (brand == 2) {
            PreferManager.setInt(PreferManager.EQUALIZER3, level);
        } else if (brand == 3) {
            PreferManager.setInt(PreferManager.EQUALIZER4, level);
        } else if (brand == 4) {
            PreferManager.setInt(PreferManager.EQUALIZER5, level);
        }
    }

    private int getEQUALIZER(short brand) {
        if (brand == 0) {
            return PreferManager.getInt(PreferManager.EQUALIZER1, 0);
        } else if (brand == 1) {
            return PreferManager.getInt(PreferManager.EQUALIZER2, 0);
        } else if (brand == 2) {
            return PreferManager.getInt(PreferManager.EQUALIZER3, 0);
        } else if (brand == 3) {
            return PreferManager.getInt(PreferManager.EQUALIZER4, 0);
        } else if (brand == 4) {
            return PreferManager.getInt(PreferManager.EQUALIZER5, 0);
        } else {
            return 0;
        }
    }

    /**
     * 初始化重低音控制器
     */
    private void setupBassBoost() {
        // 以MediaPlayer的AudioSessionId创建BassBoost
        // 相当于设置BassBoost负责控制该MediaPlayer
//        mBass = new BassBoost(0, mPlayer.getAudioSessionId());
//        // 设置启用重低音效果
//        mBass.setEnabled(true);
        // 重低音的范围为0～1000
        mSeekBarBass.setMax(1000);
        int bassLevel = PreferManager.getInt(PreferManager.BASS, 0);
        mSeekBarBass.setProgress(bassLevel);
        if (mBass != null) mBass.setStrength((short) bassLevel);
        // 为SeekBar的拖动事件设置事件监听器
        mSeekBarBass.setOnSeekBarChangeListener(new SeekBar
                .OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar
                    , int progress, boolean fromUser) {
                // 设置重低音的强度
//                mBass.setStrength((short) progress);
                Log.i("musicFx", "bass-->>" + (short) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MusicPlayService.mBass == null) return;
                mBass.setStrength((short) seekBar.getProgress());
                PreferManager.setInt(PreferManager.BASS, seekBar.getProgress());
                Log.i("musicFx", "bass--onStopTrackingTouch-->>" + (short) seekBar.getProgress());
            }
        });
    }

}
