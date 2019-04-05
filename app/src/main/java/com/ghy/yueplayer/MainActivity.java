package com.ghy.yueplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.activity.AboutActivity;
import com.ghy.yueplayer.activity.HelpActivity;
import com.ghy.yueplayer.activity.MusicPlayActivity;
import com.ghy.yueplayer.activity.OnLineMusicActivity;
import com.ghy.yueplayer.activity.SetActivity;
import com.ghy.yueplayer.activity.TimeActivity;
import com.ghy.yueplayer.adapter.MyPlayerAdapter;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.component.musicview.MusicNoteViewLayout;
import com.ghy.yueplayer.constant.UpdateTypeModel;
import com.ghy.yueplayer.fragment.LikeListFragment;
import com.ghy.yueplayer.fragment.MusicListFragment;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.main.PlayControlView;
import com.ghy.yueplayer.main.VDHLayout;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.service.TimeService;
import com.ghy.yueplayer.util.AnimHelper;
import com.ghy.yueplayer.util.AnimUtils;
import com.ghy.yueplayer.util.AppUtils;
import com.ghy.yueplayer.util.SPUtil;
import com.ghy.yueplayer.view.HeroTextView;
import com.john.waveview.WaveView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener, VDHLayout.TouchDirectionListener, VDHLayout.TouchReleasedListener {

    public static final String TAG = "MainActivity";

    @SuppressLint("StaticFieldLeak")
    public static MainActivity MA;
    ViewPager mViewPagerMain;
    ArrayList<Fragment> listFragments;
    MyPlayerAdapter playerAdapter;
    MusicListFragment musicListFragment;
    LikeListFragment likeListFragment;

    private ImageView local_music, favour_music;
    private ImageView app_icon;
    private HeroTextView tv_app_name;
    private HeroTextView tv_app_text;
    private TextView tvMusicTitle;
    private WaveView wave_view;
    private ProgressBar mProgressbar;

    private DrawerLayout mDrawerLayout;
    /**
     * 侧滑菜单布局
     */
    private RelativeLayout drawer_content;

    private MusicNoteViewLayout mMusicNoteViewLayout;
    private VDHLayout mVDHLayout;
    private ImageView mPlayerImageView;
    private PlayControlView mPlayControlView;
    private TextView mTvPlayTip;

    private FrameLayout mAnimLayout;
    private LinearLayout mYueLayout;
    private LinearLayout mMusicInfoLayout;

    /**
     * 0:未达到最大值，1:右滑至最大值，2:左滑至最大值
     */
    private int percentDirection = 0;
    private ObjectAnimator rotationAnim;
    private boolean isPlay = false;
    private long lastClickTime;
    private int toMovePosition;

    private com.nostra13.universalimageloader.core.ImageLoader mImageLoader;
    private DisplayImageOptions options;

    String musicUrl;
    String musicName;
    String musicArtist;
    String musicAlbumUri;
    int musicId;

    boolean isOnResume;

    private Context mContext;

    /**
     * YUE PLAYER
     */
    private String[] mYueSnakeStr = {"Y", "U", "E", "P", "L", "A", "Y", "E", "R"};

    /**
     * YUE PLAYER
     */
    private View[] mYueSnakeAnimView = new HeroTextView[9];

    private ScheduledExecutorService scheduledExecutorService;
    private TimerTask mCountTimeTimerTask;

    private int mYueAnimWidth;
    private int mYueAnimHeight;

    /**
     * 节拍器
     */
    private int mSnakeMetronome;

    private int mStepW;

    private int[] iW = new int[9];
    private int[] iW2 = new int[9];
    private int[] iW3 = new int[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MA = this;
        mContext = this;
        EventBus.getDefault().register(this);

        //启动音乐服务
        Intent service = new Intent(this, MusicPlayService.class);
        this.startService(service);
        //启动时间统计服务
        Intent timeService = new Intent(this, TimeService.class);
        this.startService(timeService);

        initLoader();

        initView();

        initViewPager();

        setOnclickListener();

        refreshPlayMusicData();

        mYueAnimWidth = AppUtils.getScreenWidth(this);
        mYueAnimHeight = AppUtils.dip2px(this, 50);
        Log.i("SnakeAnim", "mYueAnimWidth-->>" + mYueAnimWidth);
        Log.i("SnakeAnim", "mYueAnimHeight-->>" + mYueAnimHeight);
    }

    private void initLoader() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.default_artist)
                .showImageForEmptyUri(R.mipmap.default_artist)
                .showImageOnFail(R.mipmap.default_artist).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();
        mImageLoader = ImageLoader.getInstance();
    }

    private void initViewPager() {
        listFragments = new ArrayList<>();
        musicListFragment = new MusicListFragment();
        likeListFragment = new LikeListFragment();
        listFragments.add(musicListFragment);
        listFragments.add(likeListFragment);
        playerAdapter = new MyPlayerAdapter(getSupportFragmentManager(), listFragments);
        mViewPagerMain.setAdapter(playerAdapter);
        mViewPagerMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    favour_music.setImageResource(R.mipmap.note_btn_love);
                    local_music.setImageResource(R.mipmap.icon_music_selected);
                } else if (position == 1) {
                    favour_music.setImageResource(R.mipmap.note_btn_loved_white);
                    local_music.setImageResource(R.mipmap.icon_music_unselected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mViewPagerMain = findViewById(R.id.viewPager_main);

        local_music = findViewById(R.id.local_music);
        favour_music = findViewById(R.id.favour_music);
        app_icon = findViewById(R.id.app_icon);
        tv_app_name = findViewById(R.id.tv_app_name);
        tv_app_text = findViewById(R.id.tv_app_text);
        tvMusicTitle = findViewById(R.id.tv_music_title);
        wave_view = findViewById(R.id.wave_view);
        mProgressbar = findViewById(R.id.main_seek_bar);

        mMusicNoteViewLayout = findViewById(R.id.note_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawer_content = findViewById(R.id.drawer_content);

        mVDHLayout = findViewById(R.id.vdh_layout);
        mPlayerImageView = findViewById(R.id.iv_play);
        mPlayControlView = findViewById(R.id.play_control_view);
        mAnimLayout = findViewById(R.id.anim_layout);
        mYueLayout = findViewById(R.id.yue_layout);
        mMusicInfoLayout = findViewById(R.id.music_info_layout);
        mTvPlayTip = findViewById(R.id.tv_play_tip);
        mPlayControlView.setVisibility(View.INVISIBLE);
        mTvPlayTip.setVisibility(View.INVISIBLE);

        mVDHLayout.setTouchDirectionListener(this);
        mVDHLayout.setTouchReleasedListener(this);

        mPlayerImageView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
//            startActivity(intent);
//            Pair pair1 = new Pair(tvMusicTitle, getString(R.string.transition_title));
//            Pair pair2 = new Pair(tvMusicArtist, getString(R.string.transition_artist));
            Pair pair3 = new Pair(mProgressbar, getString(R.string.transition_progress));
            Pair pair4 = new Pair(mPlayerImageView, getString(R.string.transition_album));
            AnimUtils.makeSceneTransitionAnimationPair(this, intent,
                    pair3, pair4);
        });

        mMusicInfoLayout.setOnClickListener(view -> {
            if (isFastClick()) {
                AnimHelper.shakeAnimCycle(mMusicInfoLayout);
                boolean isLikeList = SPUtil.getBooleanSP(this, Constant.MUSIC_SP, "isLikeList");
                if (!isLikeList) {
                    if (musicListFragment != null) {
                        musicListFragment.fastClick(toMovePosition);
                    }
                }
            }
        });

        tv_app_text.setOnClickListener(view -> {
            if (isFastClick()) {
                AnimHelper.shakeAnimCycle(tv_app_text);
            }
        });
        tv_app_text.setOnLongClickListener(v -> {
            showToast(getString(R.string.say_hello));
            return false;
        });

        rotationAnim = ObjectAnimator.ofFloat(mPlayerImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(10000);
        rotationAnim.setInterpolator(new LinearInterpolator());
        rotationAnim.setRepeatCount(ValueAnimator.INFINITE);
//        rotationAnim.start();
//        rotationAnim.pause();
    }

    public synchronized boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ((time - lastClickTime < 500)) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 刷新界面播放状态及专辑封面
     */
    public void refreshPlayMusicData() {
        //从本地共享文件参数获取数据
        musicName = SPUtil.getStringSP(this,
                Constant.MUSIC_SP, "musicName");
        musicArtist = SPUtil.getStringSP(this,
                Constant.MUSIC_SP, "musicArtist");
        musicUrl = SPUtil.getStringSP(this,
                Constant.MUSIC_SP, "musicUrl");
        musicAlbumUri = SPUtil.getStringSP(this,
                Constant.MUSIC_SP, "musicAlbumUri");
        musicId = SPUtil.getIntSP(this,
                Constant.MUSIC_SP, "musicId");

        mImageLoader.displayImage(musicAlbumUri, mPlayerImageView, options);
        tvMusicTitle.setText(TextUtils.isEmpty(musicName) ? "UNKNOWN" : musicName);

        if (!isOnResume) {
            return;
        }

        if (MusicPlayService.MPS != null) {
            isPlay = MusicPlayService.MPS.isPlay();
            removeHandler();
            notifyAdapterChange();
            if (isPlay) {
                handler.sendEmptyMessage(0);
                openMusicNote();

                if (PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1) == 3) {
                    startYueAnimTimerTask();
                }
            } else {
                if (PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1) == 3) {
                    stopYueAnimTimerTask();
                }
            }
        }
        judgePlayRotationAlbum();
    }

    private void openMusicNote() {
        boolean isOpenMusicNote = PreferManager.getBoolean(PreferManager.MUSIC_NOTE, false);
        if (isOpenMusicNote) {
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    private void closeMusicNote() {
        boolean isOpenMusicNote = PreferManager.getBoolean(PreferManager.MUSIC_NOTE, false);
        if (isOpenMusicNote) {
            handler.removeMessages(1);
        }
    }

    public static class UI {
        static final Handler HANDLER = new Handler(Looper.getMainLooper());
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (MusicPlayService.player != null) {
                        mProgressbar.setMax(MusicPlayService.player.getDuration());
                        mProgressbar.setProgress(MusicPlayService.player.getCurrentPosition());

                        if (wave_view != null) {
                            wave_view.setProgress((int) (MusicPlayService.player.getCurrentPosition() / (float) MusicPlayService.player.getDuration() * 100) + 5);
                        }
                    } else {
                        mProgressbar.setMax(100);
                        mProgressbar.setProgress(0);
                    }
                    handler.sendEmptyMessageDelayed(0, 200);
                    break;
                case 1:
                    mMusicNoteViewLayout.addMusicNote();//添加一个音符
                    handler.sendEmptyMessageDelayed(1, 1000);
                    break;
                default:
                    break;
            }
        }
    };

    private void removeHandler() {
        if (handler != null) {
            handler.removeMessages(0);
            handler.removeMessages(1);
        }
    }

    private void setOnclickListener() {
        local_music.setOnClickListener(this);
        favour_music.setOnClickListener(this);
        app_icon.setOnClickListener(this);
        tv_app_name.setOnClickListener(this);

        app_icon.setOnLongClickListener(this);
        tv_app_name.setOnLongClickListener(this);
        local_music.setOnLongClickListener(this);
        favour_music.setOnLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    @Override
    public void onClick(View view) {
        if (view == local_music) {
            mViewPagerMain.setCurrentItem(0);
            favour_music.setImageResource(R.mipmap.note_btn_love);
            local_music.setImageResource(R.mipmap.icon_music_selected);
        } else if (view == favour_music) {
            mViewPagerMain.setCurrentItem(1);
            favour_music.setImageResource(R.mipmap.note_btn_loved_white);
            local_music.setImageResource(R.mipmap.icon_music_unselected);
        } else if (view == app_icon) {
            showDrawerLayout();
        } else if (view == tv_app_name) {
            showToast(getString(R.string.click_app_name));
        }
    }

    /**
     * 添加到喜欢列表：向外缩放到1.4倍大小
     */
    public void loveAnim1() {
        favour_music.setImageResource(R.mipmap.note_btn_loved_white);
        favour_music.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.insert_like_love_img_scale_out));
    }

    /**
     * 添加到喜欢列表：向内缩放恢复到原大小
     */
    public void loveAnim2() {
        favour_music.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.insert_like_love_img_scale_in));
        UI.HANDLER.postDelayed(() -> favour_music.setImageResource(R.mipmap.note_btn_love), 800);
    }

    /**
     * 喜欢列表删除：向外缩放到1.4倍大小
     */
    public void loveAnim3() {
        favour_music.setImageResource(R.mipmap.note_btn_love);
        favour_music.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.insert_like_love_img_scale_out));
    }

    /**
     * 喜欢列表删除：向内缩放恢复到原大小
     */
    public void loveAnim4() {
        favour_music.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.insert_like_love_img_scale_in));
        UI.HANDLER.postDelayed(() -> favour_music.setImageResource(R.mipmap.note_btn_loved_white), 800);
    }

    private void showDrawerLayout() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * 侧滑菜单点击事件
     *
     * @param view
     */
    public void DrawerLayoutClick(final View view) {

        if (mDrawerLayout.isDrawerOpen(drawer_content)) {
            UI.HANDLER.postDelayed(() -> {
                int id = view.getId();
                switch (id) {
                    case R.id.layout1:
                        startActivity(OnLineMusicActivity.class);
                        break;
                    case R.id.layout2:
                        startActivity(TimeActivity.class);
                        break;
                    case R.id.layout3:
                        startActivity(AboutActivity.class);
                        break;
                    case R.id.layout4:
                        startActivity(HelpActivity.class);
                        break;
                    case R.id.layout5:
                        startActivity(SetActivity.class);
                        break;
                    case R.id.layout6:
                        finish();
                        break;
                    default:
                        break;
                }
            }, 400);
            mDrawerLayout.closeDrawer(drawer_content);
        }

    }

    private void startActivity(Class<?> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }

    /**
     * 停止音乐播放服务和统计服务
     */
    private void stopService() {
        if (MusicPlayService.MPS != null) {
            MusicPlayService.MPS.stopSelf();
        }
        if (TimeService.TS != null) {
            TimeService.TS.stopSelf();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*
         * 按返回键不销毁activity
         * */
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == app_icon) {
            showToast(getString(R.string.open_menu));
        } else if (view == tv_app_name) {
            showToast(getString(R.string.say_hello));
        } else if (view == local_music) {
            showToast(getString(R.string.music_mine_list));
        } else if (view == favour_music) {
            showToast(getString(R.string.music_like_list));
        }
        return true;
    }

    @Override
    public void touchDirection(int direction, int percent) {
        switch (direction) {
            case VDHLayout.DIRECTION_RIGHT:
                mPlayControlView.setVisibility(View.VISIBLE);
                mPlayControlView.setSlidePercent(VDHLayout.DIRECTION_RIGHT, percent, isPlay);
                if (percent >= 20) {
                    if (mPlayControlView.getAlpha() != 1f) {
                        mPlayControlView.setAlpha(1f);
                    }
                    mTvPlayTip.setVisibility(View.VISIBLE);
                    mTvPlayTip.setText("滑动切歌");
                    mTvPlayTip.setAlpha(percent * 0.01f);
                } else {
                    mPlayControlView.setAlpha(percent * 0.01f * 5);
                    mTvPlayTip.setVisibility(View.INVISIBLE);
                }
                if (percent == 100) {
                    mTvPlayTip.setText("松手切歌");
                    percentDirection = 1;
                } else {
                    percentDirection = 0;
                }
                break;
            case VDHLayout.DIRECTION_LEFT:
                mPlayControlView.setVisibility(View.VISIBLE);
                mPlayControlView.setSlidePercent(VDHLayout.DIRECTION_LEFT, percent, isPlay);
                if (percent >= 20) {
                    if (mPlayControlView.getAlpha() != 1f) {
                        mPlayControlView.setAlpha(1f);
                    }
                    mTvPlayTip.setVisibility(View.VISIBLE);
                    if (isPlay) {
                        mTvPlayTip.setText("滑动暂停");
                    } else {
                        mTvPlayTip.setText("滑动播放");
                    }
                    mTvPlayTip.setAlpha(percent * 0.01f);
                } else {
                    mPlayControlView.setAlpha(percent * 0.01f * 5);
                    mTvPlayTip.setVisibility(View.INVISIBLE);
                }
                if (percent == 100) {
                    if (isPlay) {
                        mTvPlayTip.setText("松手暂停");
                    } else {
                        mTvPlayTip.setText("松手播放");
                    }
                    percentDirection = 2;
                } else {
                    percentDirection = 0;
                }
                break;
            case VDHLayout.DIRECTION_ORIGIN:
                mPlayControlView.setVisibility(View.INVISIBLE);
                mPlayControlView.setSlidePercent(VDHLayout.DIRECTION_ORIGIN, percent, isPlay);
                break;
            default:
                break;
        }
    }

    @Override
    public void touchReleased() {
        if (percentDirection == 1) {
            // 右滑至最大值释放--切歌
            if (MusicPlayService.MPS != null) {
                MusicPlayService.MPS.playNext();
            }
        } else if (percentDirection == 2) {
            // 左滑至最大值释放--播放/暂停
            if (MusicPlayService.MPS == null) {
                return;
            }
            isPlay = MusicPlayService.MPS.isPlay();
            if (checkMusicIsNull()) {
                return;
            }
            if (isPlay) {
                MusicPlayService.MPS.playOrPause(musicUrl, musicName, musicArtist, musicId);
                isPlay = MusicPlayService.MPS.isPlay();
                stopAlbumAnim();
                closeMusicNote();
                notifyAdapterChange();

                if (PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1) == 3) {
                    stopYueAnimTimerTask();
                }
            } else {
                MusicPlayService.MPS.playOrPause(musicUrl, musicName, musicArtist, musicId);
                isPlay = MusicPlayService.MPS.isPlay();
                startAlbumAnim();
                openMusicNote();
                notifyAdapterChange();

                if (PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1) == 3) {
                    startYueAnimTimerTask();
                }
            }
        }
    }

    /**
     * 要播放的音乐url是否为空
     *
     * @return
     */
    private boolean checkMusicIsNull() {
        if (TextUtils.isEmpty(musicUrl)) {
            Toast.makeText(this, R.string.song_url_null, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 判断是否在播放，如果是则旋转封面
     */
    private void judgePlayRotationAlbum() {
        if (isPlay) {
            resetAlbumAnim();
            startAlbumAnim();
        }
    }

    private void startAlbumAnim() {
        if (rotationAnim != null) {
            if (!rotationAnim.isRunning()) {
                UI.HANDLER.postDelayed(() -> rotationAnim.start(), 800);
            } else {
                UI.HANDLER.postDelayed(() -> rotationAnim.resume(), 800);
            }
        }
    }

    private void stopAlbumAnim() {
        if (rotationAnim != null) {
            rotationAnim.pause();
        }
    }

    private void resetAlbumAnim() {
        if (rotationAnim != null) {
            rotationAnim.start();//重置
            rotationAnim.pause();//暂停
        }
    }

    @Subscribe
    public void onEvent(UpdateTypeModel updateTypeModel) {
        switch (updateTypeModel.updateType) {
            // 切换播放歌曲
            case MUSIC_PALY_CHANGE:
                int musicId = updateTypeModel.dataInt;
                refreshPlayingMusic(musicId);
                break;
            default:
                break;
        }
    }

    private void refreshPlayingMusic(int musicId) {
        if (MusicPlayService.musicList.size() != 0) {
            for (int i = 0; i < MusicPlayService.musicList.size(); i++) {
                MusicInfo musicInfo = MusicPlayService.musicList.get(i);
                if (musicInfo.getId() == musicId) {
                    musicInfo.setPlaying(true);
                    toMovePosition = i;
                } else {
                    musicInfo.setPlaying(false);
                }
            }
            notifyAdapterChange();
        }
    }

    private void notifyAdapterChange() {
        boolean isLikeList = SPUtil.getBooleanSP(this, Constant.MUSIC_SP, "isLikeList");
        if (musicListFragment != null) {
            musicListFragment.notifyChange(isLikeList);
        }
    }

    /**
     * 底部 YUE PLAYER 动画
     *
     * @param isOnResume isOnResume
     */
    private void refreshYueAnim(Context context, boolean isOnResume) {

        int animMode = PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1);
        // 无动画
        if (animMode <= 1) {
            resetBottomStatus();
            // clear
            stopYueAnimTimerTask();
            mSnakeMetronome = 0;
            iW = new int[9];
            iW2 = new int[9];
            iW3 = new int[9];
            return;
        } else {
            if (!isOnResume) {
                stopYueAnimTimerTask();
            }
        }

        // 有动画
        if (mAnimLayout.getVisibility() == View.VISIBLE) {
            if (MusicPlayService.MPS.isPlay()) {
                startYueAnimTimerTask();
            }
            return;
        }

        setBottomStatusAnim();

        if (animMode == 3) {
            mAnimLayout.removeAllViews();
            // 添加view
            for (int i = 0; i < mYueSnakeStr.length; i++) {
                HeroTextView heroTextView = new HeroTextView(context);
                heroTextView.setText(mYueSnakeStr[i]);
                heroTextView.setTextSize(16);
                heroTextView.setTextColor(ContextCompat.getColor(context, R.color.gray_light));
                heroTextView.setVisibility(View.INVISIBLE);
                heroTextView.setGravity(Gravity.CENTER);
//                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        AppUtils.dip2px(mContext, 18), AppUtils.dip2px(mContext, 18));
                heroTextView.setLayoutParams(params);
                mYueSnakeAnimView[i] = heroTextView;
                mAnimLayout.addView(heroTextView);
            }
        }
    }

    /**
     * 底部状态为默认
     */
    private void resetBottomStatus() {
        if (mAnimLayout != null && mAnimLayout.getVisibility() != View.GONE) {
            mAnimLayout.setVisibility(View.GONE);
        }
        if (mYueLayout != null && mYueLayout.getVisibility() != View.VISIBLE) {
            mYueLayout.setVisibility(View.VISIBLE);
            AnimHelper.fadeIn(mYueLayout, 1000);
        }
        if (mMusicInfoLayout != null && mMusicInfoLayout.getVisibility() != View.VISIBLE) {
            mMusicInfoLayout.setVisibility(View.VISIBLE);
            AnimHelper.fadeIn(mMusicInfoLayout, 1000);
        }
    }

    /**
     * 底部状态为动画
     */
    private void setBottomStatusAnim() {
        if (mAnimLayout != null && mAnimLayout.getVisibility() != View.VISIBLE) {
            mAnimLayout.setVisibility(View.VISIBLE);
            AnimHelper.fadeIn(mAnimLayout, 1000);
        }
        if (mYueLayout != null && mYueLayout.getVisibility() != View.GONE) {
            mYueLayout.setVisibility(View.GONE);
        }
        if (mMusicInfoLayout != null && mMusicInfoLayout.getVisibility() != View.GONE) {
            mMusicInfoLayout.setVisibility(View.GONE);
        }
    }

    /**
     * TimerTask
     */
    private void startYueAnimTimerTask() {
        createExecutorService();
        if (mCountTimeTimerTask == null) {
            mCountTimeTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    runOnUiThread(() -> {
                        for (int i = 0; i <= mSnakeMetronome; i++) {

                            if (mYueSnakeAnimView[i].getTranslationX() == 0 && mYueSnakeAnimView[i].getTranslationY() == 0) {
                                if (mYueSnakeAnimView[i].getVisibility() != View.VISIBLE) {
                                    mYueSnakeAnimView[i].setVisibility(View.VISIBLE);
                                }
                            }

                            if (mYueSnakeAnimView[i].getTranslationY() == 0) {
                                mStepW = mYueAnimWidth / AppUtils.dip2px(mContext, 18);
                                int oneStepX = mYueAnimWidth / (mStepW);
                                if (iW[i] != 0) {
                                    mYueSnakeAnimView[i].setTranslationX(mYueSnakeAnimView[i].getTranslationX() + oneStepX);
                                }
                                iW[i]++;
                                if (iW[i] == mStepW + 1) {
                                    mYueSnakeAnimView[i].setTranslationX(mYueAnimWidth);
                                    int transY = mYueAnimHeight / 2 - mYueSnakeAnimView[i].getHeight() / 2;
                                    mYueSnakeAnimView[i].setTranslationY(transY);
                                    iW[i] = 0;
                                }
                            }

                            if (mYueSnakeAnimView[i].getTranslationY() == mYueAnimHeight / 2 - mYueSnakeAnimView[i].getHeight() / 2) {
                                mStepW = mYueAnimWidth / AppUtils.dip2px(mContext, 18);
                                int oneStepX = mYueAnimWidth / (mStepW);
                                if (iW2[i] != 0) {
                                    mYueSnakeAnimView[i].setTranslationX(mYueSnakeAnimView[i].getTranslationX() - oneStepX);
                                }
                                iW2[i]++;
                                if (iW2[i] == mStepW + 2) {
                                    mYueSnakeAnimView[i].setTranslationX(-oneStepX);
                                    mYueSnakeAnimView[i].setTranslationY(mYueAnimHeight - mYueSnakeAnimView[i].getHeight());
                                    iW2[i] = 0;
                                }
                            }

                            if (mYueSnakeAnimView[i].getTranslationY() == mYueAnimHeight - mYueSnakeAnimView[i].getHeight()) {
                                mStepW = mYueAnimWidth / AppUtils.dip2px(mContext, 18);
                                int oneStepX = mYueAnimWidth / (mStepW);
                                if (iW3[i] != 0) {
                                    mYueSnakeAnimView[i].setTranslationX(mYueSnakeAnimView[i].getTranslationX() + oneStepX);
                                }
                                iW3[i]++;
                                if (iW3[i] == mStepW + 2) {
                                    mYueSnakeAnimView[i].setTranslationX(0);
                                    mYueSnakeAnimView[i].setTranslationY(0);
                                    mYueSnakeAnimView[i].setVisibility(View.INVISIBLE);
                                    iW3[i] = 0;
                                }
                            }
                        }

                        mSnakeMetronome++;
                        if (mSnakeMetronome > mYueSnakeStr.length - 1) {
                            mSnakeMetronome = mYueSnakeStr.length - 1;
                        }
                    });
                }
            };
            scheduledExecutorService.scheduleAtFixedRate(mCountTimeTimerTask,
                    500, 150, TimeUnit.MILLISECONDS);
        }
    }

    private void createExecutorService() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = new ScheduledThreadPoolExecutor(3,
                    new BasicThreadFactory.Builder().namingPattern("scheduled-pool-%d").daemon(true).build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnResume = true;
        refreshPlayMusicData();

        refreshYueAnim(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnResume = false;
        stopAlbumAnim();
        closeMusicNote();
        UI.HANDLER.postDelayed(this::resetAlbumAnim, 400);

        refreshYueAnim(this, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        UI.HANDLER.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        stopService();
    }

    private void cancelCountTimeTask() {
        if (mCountTimeTimerTask != null) {
            mCountTimeTimerTask.cancel();
            mCountTimeTimerTask = null;
        }
    }

    private void stopYueAnimTimerTask() {
        cancelCountTimeTask();
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

}
