package com.ghy.yueplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.IBinder;
import android.widget.Toast;

import com.ghy.yueplayer.MainActivity;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.activity.MusicPlayActivity;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.fragment.LyricFragment;
import com.ghy.yueplayer.fragment.PlayFragment;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.util.SPUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MusicPlayService extends Service {
    public MusicPlayService() {
    }

    public static MusicPlayService MPSInstance;
    public static MediaPlayer player;
    // 定义系统的均衡器
    public static Equalizer mEqualizer;
    // 定义系统的重低音控制器
    public static BassBoost mBass;

    private static String AlbumUri = "content://media/external/audio/albumart";

    public int playListId;//当前播放歌曲在listView中的位置
    public int playListNumber;//listView中所有歌曲数目
    public static List<MusicInfo> musicList;//歌曲列表


    public static int time;//总时间
    public static int duration;//播放时间

    NotificationManager notificationManager;//通知
    Notification notification;
    int notification_id = 100144;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MPSInstance = this;

        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    /*
    * 播放音乐，path参数播放使用，musicName参数通知使用，artist参数通知使用
    * */
    public void playMusic(String path, String musicName, String artist) {
        if (player != null) {
            player.release();
            player = null;
        }
        player = new MediaPlayer();
        // 初始化均衡控制器
        setupEqualizer();
        // 初始化重低音控制器
        setupBassBoost();
        try {
            player.setDataSource(path);
            player.prepare();
            player.start();
            time = player.getDuration();//获取歌曲时长，毫秒
            //保存歌曲总时长
            SPUtil.saveSP(MusicPlayService.this, Constant.MUSIC_SP, "musicAllDuration", time);

            //设置通知
            showNotification("播放歌曲：" + musicName, "YuePlayer", "正在播放：" + artist + " - " + musicName);

            //刷新主界面播放状态及专辑封面
            if (MainActivity.MainInstance != null) {
                MainActivity.MainInstance.refreshPlayMusicData();
            }

            //设置播放界面背景
            if (MusicPlayActivity.MPAInstance != null) {
                String musicAlbumUri = SPUtil.getStringSP(this,
                        Constant.MUSIC_SP, "musicAlbumUri");
                MusicPlayActivity.MPAInstance.setPlayBackgroundImage(musicAlbumUri);
            }

            //加载歌词
            if (LyricFragment.LYFInstance != null) {
                LyricFragment.LYFInstance.startInitLyric();
            }

            //监听歌曲播放完毕
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (PlayFragment.PFInstance != null) {
                        PlayFragment.PFInstance.MusicPlayOver();
                    }
                    //player置为空
                    if (player != null) {
                        player.release();
                        player = null;
                    }
                    //播放下一曲
                    playNext();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupEqualizer() {
        // 以MediaPlayer的AudioSessionId创建Equalizer
        // 相当于设置Equalizer负责控制该MediaPlayer
        if (player == null) return;
        mEqualizer = new Equalizer(0, player.getAudioSessionId());
        // 启用均衡控制效果
        mEqualizer.setEnabled(true);
        // 获取均衡控制器支持最小值和最大值
        short minEQLevel = mEqualizer.getBandLevelRange()[0];//第一个下标为最低的限度范围 -1500
        short maxEQLevel = mEqualizer.getBandLevelRange()[1];//第二个下标为最高的限度范围 1500
        short brands = mEqualizer.getNumberOfBands();
        for (short i = 0; i < brands; i++) {
            int bandLevel = getEQUALIZER(i);
            bandLevel = bandLevel == 0 ? (maxEQLevel - minEQLevel) / 2 : bandLevel;
            mEqualizer.setBandLevel(i, (short) bandLevel);
        }
    }

    private void setupBassBoost() {
        // 以MediaPlayer的AudioSessionId创建BassBoost
        // 相当于设置BassBoost负责控制该MediaPlayer
        if (player == null) return;
        mBass = new BassBoost(0, player.getAudioSessionId());
        // 设置启用重低音效果
        mBass.setEnabled(true);
        int bassLevel = PreferManager.getInt(PreferManager.BASS, 0);
        mBass.setStrength((short) bassLevel);
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

    /*
    * 判断是播放还是暂停状态 true：播放 false:暂停
    * */
    public boolean isPlay() {
        return player != null && player.isPlaying();
    }

    /*
    * 播放或暂停方法
    * */
    public void playOrPause(String musicUrl, String musicName, String artist) {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.start();
            }
        } else {
            //播放上次记忆的歌曲
            if (musicUrl.equals("")) {
                showToast("没有正在播放的歌曲");
            } else {
                playMusic(musicUrl, musicName, artist);
                //重新加载播放界面数据
                if (PlayFragment.PFInstance != null) {
                    PlayFragment.PFInstance.initData();
                }
            }
        }
    }

    /*
    * 停止播放方法
    * */
    public void stopPlay() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    /*
    * 下一曲方法
    * */
    public void playNext() {
        if (musicList != null && musicList.size() != 0) {
            haveMusicAndPlayNext();
        } else {
            showToast("没有发现歌曲");
        }
    }

    private void haveMusicAndPlayNext() {
        //获取保存的当前播放歌曲在列表中的位置和歌曲总数目
        playListId = SPUtil.getIntSP(MusicPlayService.this, Constant.MUSIC_SP, "playListId");
        playListNumber = SPUtil.getIntSP(MusicPlayService.this, Constant.MUSIC_SP, "playListNumber");
        //根据播放模式获取下一首歌曲在列表中的位置
        int getPlayMode = SPUtil.getIntSP(this, Constant.MUSIC_SP, "playMode");
        int nextMusicId = 0;
        switch (getPlayMode) {
            case -1:
                //默认列表循环模式
                nextMusicId = getDefaultNextMusicId();
                break;
            case 1:
                //列表循环模式
                nextMusicId = getDefaultNextMusicId();
                break;
            case 2:
                //随机播放模式
                nextMusicId = getRandomNextMusicId();
                break;
            case 3:
                //单曲循环模式
                nextMusicId = playListId;
                break;
        }

        //保存下一首歌曲相关信息，加载界面使用
        String nextMusicName = musicList.get(nextMusicId).getTitle();
        String nextMusicArtist = musicList.get(nextMusicId).getArtist();
        String nextMusicAlbumUri = AlbumUri + File.separator + musicList.get(nextMusicId).getAlbumId();
        String nextMusicUrl = musicList.get(nextMusicId).getUrl();

        saveMusicInfoSP(nextMusicName, nextMusicArtist, nextMusicAlbumUri, nextMusicUrl, nextMusicId);

        playMusic(nextMusicUrl, nextMusicName, nextMusicArtist);
        //重新加载播放界面数据
        if (PlayFragment.PFInstance != null) {
            PlayFragment.PFInstance.initData();
        }
    }

    /*
    * 获取列表循环下一首歌在列表中的位置
    * */
    private int getDefaultNextMusicId() {
        int nextMusicId = playListId + 1;
        if (nextMusicId >= playListNumber) {
            nextMusicId = 0;
        }
        return nextMusicId;
    }

    /*
    * 获取随机播放下一首歌在列表中的位置
    * */
    private int getRandomNextMusicId() {
        return RandomNumber();
    }

    /*
    * 生成一个随机数
    * */
    private int RandomNumber() {
        Random random = new Random();
        return random.nextInt(playListNumber);
    }

    /*
    * 保存上一曲、下一曲 歌曲的相关信息
    * */
    private void saveMusicInfoSP(String musicName, String musicArtist,
                                 String musicAlbumUri, String musicUrl,
                                 int musicId) {
        SPUtil.saveSP(MusicPlayService.this,
                Constant.MUSIC_SP,
                "musicName", musicName);
        SPUtil.saveSP(MusicPlayService.this,
                Constant.MUSIC_SP,
                "musicArtist", musicArtist);
        SPUtil.saveSP(MusicPlayService.this,
                Constant.MUSIC_SP,
                "musicAlbumUri", musicAlbumUri);
        SPUtil.saveSP(MusicPlayService.this,
                Constant.MUSIC_SP,
                "musicUrl", musicUrl);
        SPUtil.saveSP(MusicPlayService.this,
                Constant.MUSIC_SP,
                "playListId", musicId);
    }

    /*
    * 上一曲方法
    * */
    public void playPre() {
        if (musicList != null && musicList.size() != 0) {
            haveMusicAndPlayPre();
        } else {
            showToast("没有发现歌曲");
        }

    }

    private void haveMusicAndPlayPre() {
        //获取保存的当前播放歌曲在列表中的位置和歌曲总数目
        playListId = SPUtil.getIntSP(MusicPlayService.this, Constant.MUSIC_SP, "playListId");
        playListNumber = SPUtil.getIntSP(MusicPlayService.this, Constant.MUSIC_SP, "playListNumber");
        //获取下一首歌曲在列表中的位置
        int preMusicId = playListId - 1;
        if (preMusicId < 0) {
            preMusicId = playListNumber - 1;
        }
        //保存下一首歌曲相关信息，加载界面使用
        String preMusicName = musicList.get(preMusicId).getTitle();
        String preMusicArtist = musicList.get(preMusicId).getArtist();
        String preMusicAlbumUri = AlbumUri + File.separator + musicList.get(preMusicId).getAlbumId();
        String preMusicUrl = musicList.get(preMusicId).getUrl();

        saveMusicInfoSP(preMusicName, preMusicArtist, preMusicAlbumUri, preMusicUrl, preMusicId);

        playMusic(preMusicUrl, preMusicName, preMusicArtist);
        //重新加载播放界面数据
        if (PlayFragment.PFInstance != null) {
            PlayFragment.PFInstance.initData();
        }
    }


    /*
    * 快进快退到指定位置播放
    * */
    public void seekPositionPlay(int position) {
        if (player != null) {
            player.seekTo(position);
        } else {
            showToast("没有正在播放的歌曲");
        }
    }

    /*
    * 获取当前播放时长
    * */
    public int getCurrentDuration() {
        if (player != null) {
            duration = player.getCurrentPosition();
            return duration;
        } else {
            return 0;
        }
    }

    /*
    * 显示后台运行通知
    * */
    private void showNotification(String tickerText, String title, String content) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, MusicPlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            notification = new Notification();
        notification = new Notification.Builder(this)
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_app_blue))
                .setSmallIcon(R.mipmap.icon_app_notify_white)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(contentIntent)
                .setTicker(tickerText)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .build();
//        notification.icon = R.mipmap.icon_app_notify_white;
//        notification.tickerText = tickerText;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        //使用如下的Intent，便不会调用对应的Activity，而是调用Task中的栈顶Activity
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setClass(this, MusicPlayActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(this, title, content, contentIntent);
        notificationManager.notify(notification_id, notification);
    }

    private void showToast(String s) {
        Toast.makeText(MusicPlayService.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消通知
        if (notificationManager != null) {
            notificationManager.cancel(notification_id);
            notificationManager = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

}
