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
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
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
import com.ghy.yueplayer.base.BaseActivity;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.common.CircleAnimManager;
import com.ghy.yueplayer.common.DarkModeConfig;
import com.ghy.yueplayer.common.DarkModeManager;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.common.YueAnimManager;
import com.ghy.yueplayer.common.event.Actions;
import com.ghy.yueplayer.common.event.Event;
import com.ghy.yueplayer.component.musicview.MusicNoteViewLayout;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.constant.UpdateTypeModel;
import com.ghy.yueplayer.fragment.LikeListFragment;
import com.ghy.yueplayer.fragment.MusicListFragment;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.helper.AnimHelper;
import com.ghy.yueplayer.main.PlayControlView;
import com.ghy.yueplayer.main.VDHLayout;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.service.TimeService;
import com.ghy.yueplayer.utils.AnimUtils;
import com.ghy.yueplayer.utils.SPUtil;
import com.ghy.yueplayer.utils.ViewUtils;
import com.ghy.yueplayer.view.HeroTextView;
import com.john.waveview.WaveView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener,
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
    private WaveView wave_view;
    private ProgressBar mProgressbar;

    private DrawerLayout mDrawerLayout;
    /**
     * 侧滑菜单布局
     */
    private RelativeLayout mDrawerContent;

    private ImageView mIvDarkMode;
    private TextView mTvDarkMode;

    private ImageView mIvOnline;
    private ImageView mIvTime;
    private ImageView mIvAbout;
    private ImageView mIvHelp;
    private ImageView mIvSet;
    private ImageView mIvExit;

    private MusicNoteViewLayout mMusicNoteViewLayout;
    private VDHLayout mVDHLayout;
    private ImageView mPlayerImageView;
    private ImageView mPlayerIvAnim;
    private PlayControlView mPlayControlView;
    private TextView mTvPlayTip;

    private FrameLayout mMainLayout;
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

    private String musicUrl;
    private String musicName;
    private String musicArtist;
    private String musicAlbumUri;
    private int musicId;

    private boolean isOnResume;

    /**
     * YUE动画管理
     */
    private YueAnimManager mYueAnimManager;

    /**
     * CIRCLE动画管理
     */
    private CircleAnimManager mCircleAnimManager;

    private int mPagerPosition;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MA = this;

        //启动音乐服务
        Intent service = new Intent(this, MusicPlayService.class);
        this.startService(service);
        //启动时间统计服务
//        Intent timeService = new Intent(this, TimeService.class);
//        this.startService(timeService);

        initLoader();

        initView();

        initViewPager();

        initManager();

        setOnclickListener();

        refreshPlayMusicData();
    }

    private void initManager() {
        mYueAnimManager = new YueAnimManager(this);
        mYueAnimManager.initView(mAnimLayout, mYueLayout, mMusicInfoLayout);

        mCircleAnimManager = new CircleAnimManager(this);
        mCircleAnimManager.initView(mMainLayout, mPlayerImageView, mPlayerIvAnim);
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
                mPagerPosition = position;
                setMusicLoveIcon();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setMusicLoveIcon() {
        if (mPagerPosition == 0) {
            if (Global.DAY_MODE) {
                local_music.setImageDrawable(ViewUtils.getTintDrawable(this,
                        R.mipmap.icon_music_selected, R.color.dn_page_title));
                favour_music.setImageDrawable(ViewUtils.getTintDrawable(this,
                        R.mipmap.note_btn_love, R.color.dn_page_title));
            } else {
                local_music.setImageDrawable(ViewUtils.getTintDrawable(this,
                        R.mipmap.icon_music_selected, R.color.dn_page_title_night));
                favour_music.setImageDrawable(ViewUtils.getTintDrawable(this,
                        R.mipmap.note_btn_love, R.color.dn_page_title_night));
            }
        } else if (mPagerPosition == 1) {
            if (Global.DAY_MODE) {
                local_music.setImageDrawable(ViewUtils.getTintDrawable(this,
                        R.mipmap.icon_music_unselected, R.color.dn_page_title));
                favour_music.setImageDrawable(ViewUtils.getTintDrawable(this,
                        R.mipmap.note_btn_loved_white, R.color.dn_page_title));
            } else {
                local_music.setImageDrawable(ViewUtils.getTintDrawable(this,
                        R.mipmap.icon_music_unselected, R.color.dn_page_title_night));
                favour_music.setImageDrawable(ViewUtils.getTintDrawable(this,
                        R.mipmap.note_btn_loved_white, R.color.dn_page_title_night));
            }
        }
    }

    @Override
    protected void initView() {
        mViewPagerMain = findViewById(R.id.viewPager_main);

        local_music = findViewById(R.id.local_music);
        favour_music = findViewById(R.id.favour_music);
        app_icon = findViewById(R.id.app_icon);
        tv_app_name = findViewById(R.id.tv_app_name);
        tv_app_text = findViewById(R.id.tv_app_text);
        wave_view = findViewById(R.id.wave_view);
        mProgressbar = findViewById(R.id.main_seek_bar);

        mMusicNoteViewLayout = findViewById(R.id.note_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerContent = findViewById(R.id.drawer_content);
        mIvDarkMode = findViewById(R.id.iv_dark_mode);
        mTvDarkMode = findViewById(R.id.tv_dark_mode);
        mIvOnline = findViewById(R.id.iv_online);
        mIvTime = findViewById(R.id.iv_time);
        mIvAbout = findViewById(R.id.iv_about);
        mIvHelp = findViewById(R.id.iv_help);
        mIvSet = findViewById(R.id.iv_set);
        mIvExit = findViewById(R.id.iv_exit);

        mVDHLayout = findViewById(R.id.vdh_layout);
        mPlayerImageView = findViewById(R.id.iv_play);
        mPlayerIvAnim = findViewById(R.id.iv_play_anim);
        mPlayControlView = findViewById(R.id.play_control_view);
        mMainLayout = findViewById(R.id.main);
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
//            Pair pair3 = new Pair(mProgressbar, getString(R.string.transition_progress));
            Pair pair4 = new Pair(mPlayerImageView, getString(R.string.transition_album));
            AnimUtils.makeSceneTransitionAnimationPair(this, intent,
                    pair4);
        });

        mMusicInfoLayout.setOnClickListener(view -> {

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

        setDarkModeUi();

    }

    @Override
    protected void initData() {
        initIconSetting(Global.DAY_MODE);
    }

    private void setDarkModeUi() {
        if (!Global.DAY_MODE) {
            mIvDarkMode.setImageResource(R.mipmap.icon_day_mode);
            mTvDarkMode.setText(getString(R.string.dark_mode_day));
        } else {
            mIvDarkMode.setImageResource(R.mipmap.icon_night_mode);
            mTvDarkMode.setText(getString(R.string.dark_mode_night));
        }
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

        if (!isOnResume) {
            return;
        }

        if (MusicPlayService.MPS != null) {
            isPlay = MusicPlayService.MPS.isPlay();
            removeHandler();
            notifyAdapterChange();
            if (isPlay) {
                handler.sendEmptyMessage(0);
                // 如果在播放则旋转封面
                startAlbumAnim();
                // 音符
                openMusicNote();

                setYueAnimManager(true);
                setCircleAnimManager(true);
            } else {
                setYueAnimManager(false);
                setCircleAnimManager(false);
            }
        }
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

                        int progress = (int) (MusicPlayService.player.getCurrentPosition() / (float) MusicPlayService.player.getDuration() * 100);
                        if (wave_view != null) {
                            wave_view.setProgress(progress + 5);
                        }
                        setYueShakeTransAnim(progress);
                    } else {
                        mProgressbar.setMax(100);
                        mProgressbar.setProgress(0);

                        if (wave_view != null) {
                            wave_view.setProgress(0);
                        }
                        setYueShakeTransAnim(0);
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

            if (mViewPagerMain != null && mViewPagerMain.getCurrentItem() == 0 && isFastClick()) {
                AnimHelper.shakeAnimCycle(local_music);
                if (musicListFragment != null) {
                    musicListFragment.fastClick(toMovePosition);
                }
            }

        } else if (view == favour_music) {
            mViewPagerMain.setCurrentItem(1);
            favour_music.setImageResource(R.mipmap.note_btn_loved_white);
            local_music.setImageResource(R.mipmap.icon_music_unselected);
        } else if (view == app_icon) {
            showDrawerLayout();
        } else if (view == tv_app_name) {
//            showToast(getString(R.string.click_app_name));

            if (mViewPagerMain != null && mViewPagerMain.getCurrentItem() == 0 && isFastClick()) {
                AnimHelper.shakeAnimCycle(tv_app_name);
                if (musicListFragment != null) {
                    musicListFragment.scrollTopPosition();
                }
            }
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
    public void drawerLayoutClick(final View view) {
        if (mDrawerLayout == null) {
            return;
        }
        if (mDrawerLayout.isDrawerOpen(mDrawerContent)) {
            boolean clickCloseDrawer = true;
            int id = view.getId();
            UI.HANDLER.postDelayed(() -> {
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

            if (id == R.id.layout7) {
                changeDarkMode();
                clickCloseDrawer = false;
            }

            if (clickCloseDrawer) {
                mDrawerLayout.closeDrawer(mDrawerContent);
            }
        }
    }

    private void changeDarkMode() {
        if (!Global.DAY_MODE) {
            DarkModeManager.getInstance().setDarkMode(DarkModeConfig.DARK_MODE_DAY);
        } else {
            DarkModeManager.getInstance().setDarkMode(DarkModeConfig.DARK_MODE_NIGHT);
        }
        setDarkModeUi();
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

                setYueAnimManager(false);
                setCircleAnimManager(false);
            } else {
                MusicPlayService.MPS.playOrPause(musicUrl, musicName, musicArtist, musicId);
                isPlay = MusicPlayService.MPS.isPlay();
                startAlbumAnim();
                openMusicNote();
                notifyAdapterChange();

                setYueAnimManager(true);
                setCircleAnimManager(true);
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
     * 重要--切换动画效果和生命周期改变时，根据此布局是否可见来判断是否重新创建view
     */
    public void setYueAnimLayoutGone() {
        if (mAnimLayout != null) {
            mAnimLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 底部 YUE PLAYER 动画
     *
     * @param isOnResume isOnResume
     */
    private void refreshYueAnim(Context context, boolean isOnResume) {
        if (mYueAnimManager != null) {
            mYueAnimManager.refreshYueAnim(context, isOnResume);
        }
    }

    /**
     * YUE 动画管理
     *
     * @param isStart true开始动画 false停止动画
     */
    private void setYueAnimManager(boolean isStart) {
        if (mYueAnimManager != null) {
            mYueAnimManager.setYueAnimManager(isStart);
        }
    }

    /**
     * 晃晃漂流--平移动画
     *
     * @param progress
     */
    private void setYueShakeTransAnim(int progress) {
        if (mYueAnimManager != null) {
            mYueAnimManager.setYueShakeTransAnim(progress);
        }
    }

    /**
     * 底部 Circle 动画
     *
     * @param isOnResume isOnResume
     */
    private void refreshCircleAnim(Context context, boolean isOnResume) {
        if (mCircleAnimManager != null) {
            mCircleAnimManager.refreshCircleAnim(context, isOnResume);
        }
    }

    /**
     * Circle 动画管理
     *
     * @param isStart true开始动画 false停止动画
     */
    private void setCircleAnimManager(boolean isStart) {
        if (mCircleAnimManager != null) {
            mCircleAnimManager.setCircleAnimManager(isStart);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnResume = true;
        refreshYueAnim(this, true);
        refreshCircleAnim(this, true);
        refreshPlayMusicData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnResume = false;
        stopAlbumAnim();
        closeMusicNote();
        refreshYueAnim(this, false);
        refreshCircleAnim(this, false);
        UI.HANDLER.postDelayed(this::resetAlbumAnim, 800);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        UI.HANDLER.removeCallbacksAndMessages(null);
        stopService();
    }

    @Override
    public void onEvent(Event event) {
        if (event == null) {
            return;
        }
        // DarkMode
        if (Actions.ACTION_DARK_MODE_CHANGE.equals(event.getAction())) {
            DarkModeManager.getInstance().publishDarkModeEvent();
        }
        super.onEvent(event);
    }

    @Override
    public void onDarkModeChange(boolean isDayMode) {
        super.onDarkModeChange(isDayMode);
        initImmersionBar();

        initIconSetting(isDayMode);
    }

    private void initIconSetting(boolean isDayMode) {
        if (isDayMode) {
            app_icon.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_app_white, R.color.dn_page_title));

            setMusicLoveIcon();

            mIvOnline.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_alb, R.color.dn_page_title));
            mIvTime.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_time, R.color.dn_page_title));
            mIvAbout.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_about, R.color.dn_page_title));
            mIvHelp.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_help, R.color.dn_page_title));
            mIvSet.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_set, R.color.dn_page_title));
            mIvExit.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_exit, R.color.dn_page_title));

            mIvDarkMode.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_night_mode, R.color.dn_page_title));

        } else {
            app_icon.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_app_white, R.color.dn_page_title_night));

            setMusicLoveIcon();

            mIvOnline.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_alb, R.color.dn_page_title_night));
            mIvTime.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_time, R.color.dn_page_title_night));
            mIvAbout.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_about, R.color.dn_page_title_night));
            mIvHelp.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_help, R.color.dn_page_title_night));
            mIvSet.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_set, R.color.dn_page_title_night));
            mIvExit.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icn_exit, R.color.dn_page_title_night));

            mIvDarkMode.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_day_mode, R.color.dn_page_title_night));
        }
    }
}
