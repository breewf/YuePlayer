package com.ghy.yueplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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
import com.ghy.yueplayer.util.SPUtil;
import com.ghy.yueplayer.view.HeroTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener, VDHLayout.TouchDirectionListener, VDHLayout.TouchReleasedListener {

    public static final String TAG = "MainActivity";

    @SuppressLint("StaticFieldLeak")
    public static MainActivity MainInstance;
    ViewPager viewPager_main;
    ArrayList<Fragment> listFragments;
    MyPlayerAdapter playerAdapter;
    MusicListFragment musicListFragment;
    LikeListFragment likeListFragment;

    private ImageView local_music, favour_music;
    private ImageView app_icon;
    private HeroTextView tv_app_name;
    private TextView tvMusicTitle;
    private TextView tvMusicArtist;
    private ProgressBar mProgressbar;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout drawer_content;//侧滑菜单布局

    private MusicNoteViewLayout mMusicNoteViewLayout;
    private VDHLayout mVDHLayout;
    private ImageView mPlayerImageView;
    private PlayControlView mPlayControlView;
    private TextView mTvPlayTip;
    private LinearLayout music_info_layout;
    private int percentDirection = 0;//0:未达到最大值，1:右滑至最大值，2:左滑至最大值
    private ObjectAnimator rotationAnim;
    private boolean isPlay = false;
    private long lastClickTime;
    private int toMovePosition = 0;
    private com.nostra13.universalimageloader.core.ImageLoader mImageLoader;
    private DisplayImageOptions options;

    String musicUrl;
    String musicName;
    String musicArtist;
    String musicAlbumUri;
    int musicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainInstance = this;
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
        viewPager_main.setAdapter(playerAdapter);
        viewPager_main.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);

        local_music = (ImageView) findViewById(R.id.local_music);
        favour_music = (ImageView) findViewById(R.id.favour_music);
        app_icon = (ImageView) findViewById(R.id.app_icon);
        tv_app_name = (HeroTextView) findViewById(R.id.tv_app_name);
        tvMusicTitle = (TextView) findViewById(R.id.tv_music_title);
        tvMusicArtist = (TextView) findViewById(R.id.tv_music_artist);
        mProgressbar = (ProgressBar) findViewById(R.id.main_seek_bar);

        mMusicNoteViewLayout = (MusicNoteViewLayout) findViewById(R.id.note_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_content = (RelativeLayout) findViewById(R.id.drawer_content);

        mVDHLayout = (VDHLayout) findViewById(R.id.vdh_layout);
        mPlayerImageView = (ImageView) findViewById(R.id.iv_play);
        mPlayControlView = (PlayControlView) findViewById(R.id.play_control_view);
        music_info_layout = (LinearLayout) findViewById(R.id.music_info_layout);
        mTvPlayTip = (TextView) findViewById(R.id.tv_play_tip);
        mPlayControlView.setVisibility(View.INVISIBLE);
        mTvPlayTip.setVisibility(View.INVISIBLE);

        mVDHLayout.setTouchDirectionListener(this);
        mVDHLayout.setTouchReleasedListener(this);

        mPlayerImageView.setOnClickListener(view -> {
//                startPlayActivityTransIntent();
            Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
            startActivity(intent);
        });

        music_info_layout.setOnClickListener(view -> {
            if (isFastClick()) {
                boolean isLikeList = SPUtil.getBooleanSP(this, Constant.MUSIC_SP, "isLikeList");
                if (!isLikeList) {
                    if (musicListFragment != null) musicListFragment.fastClick(toMovePosition);
                }
            }
        });

        rotationAnim = ObjectAnimator.ofFloat(mPlayerImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(10000);
        rotationAnim.setInterpolator(new LinearInterpolator());
        rotationAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnim.start();
        rotationAnim.pause();
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

        if (MusicPlayService.MPS != null) {
            isPlay = MusicPlayService.MPS.isPlay();
            removeHandler();
            if (isPlay) {
                handler.sendEmptyMessage(0);
                openMusicNote();
            }
        }
        judgePlayRotationAlbum();
        mImageLoader.displayImage(musicAlbumUri, mPlayerImageView, options);
        tvMusicTitle.setText(TextUtils.isEmpty(musicName) ? "未知歌曲" : musicName);
        tvMusicArtist.setText(TextUtils.isEmpty(musicArtist) ? "未知艺术家" : musicArtist);
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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (MusicPlayService.player != null) {
                        mProgressbar.setMax(MusicPlayService.player.getDuration());
                        mProgressbar.setProgress(MusicPlayService.player.getCurrentPosition());
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
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            viewPager_main.setCurrentItem(0);
            favour_music.setImageResource(R.mipmap.note_btn_love);
            local_music.setImageResource(R.mipmap.icon_music_selected);
        } else if (view == favour_music) {
            viewPager_main.setCurrentItem(1);
            favour_music.setImageResource(R.mipmap.note_btn_loved_white);
            local_music.setImageResource(R.mipmap.icon_music_unselected);
        } else if (view == app_icon) {
            showDrawerLayout();
        } else if (view == tv_app_name) {
//            showToast("欢迎您使用YuePlayer");
        }
    }

    /*
    * 添加到喜欢列表：向外缩放到1.4倍大小
    * */
    public void loveAnim1() {
        favour_music.setImageResource(R.mipmap.note_btn_loved_white);
        favour_music.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.insert_like_love_img_scale_out));
    }

    /*
    * 添加到喜欢列表：向内缩放恢复到原大小
    * */
    public void loveAnim2() {
        favour_music.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.insert_like_love_img_scale_in));
        new Handler().postDelayed(() -> favour_music.setImageResource(R.mipmap.note_btn_love), 800);
    }

    /*
    * 喜欢列表删除：向外缩放到1.4倍大小
    * */
    public void loveAnim3() {
        favour_music.setImageResource(R.mipmap.note_btn_love);
        favour_music.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.insert_like_love_img_scale_out));
    }

    /*
    * 喜欢列表删除：向内缩放恢复到原大小
    * */
    public void loveAnim4() {
        favour_music.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.insert_like_love_img_scale_in));
        new Handler().postDelayed(() -> favour_music.setImageResource(R.mipmap.note_btn_loved_white), 800);
    }

    private void showDrawerLayout() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /*
    * 侧滑菜单点击事件
    * */
    public void DrawerLayoutClick(final View view) {

        if (mDrawerLayout.isDrawerOpen(drawer_content)) {
            new Handler().postDelayed(() -> {
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
     * 转场动画
     */
    private void startPlayActivityTransIntent() {
        Intent intent = new Intent(this, MusicPlayActivity.class);
        intent.putExtra("key", "value");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
//                            new Pair(mPlayerImageView, getString(R.string.transition_album)),
                            new Pair(tvMusicTitle, getString(R.string.transition_title)),
                            new Pair(tvMusicArtist, getString(R.string.transition_artist)));
            ActivityCompat.startActivity(this, intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /*
    * 停止音乐播放服务和统计服务
    * */
    private void stopService() {
        if (MusicPlayService.MPS != null) MusicPlayService.MPS.stopSelf();
        if (TimeService.TSInstance != null) TimeService.TSInstance.stopSelf();
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
            showToast("点我打开菜单哦");
        } else if (view == tv_app_name) {
            showToast("听见好音乐~");
        } else if (view == local_music) {
            showToast("我的音乐列表");
        } else if (view == favour_music) {
            showToast("我的喜欢列表");
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
                    if (mPlayControlView.getAlpha() != 1f) mPlayControlView.setAlpha(1f);
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
                    if (mPlayControlView.getAlpha() != 1f) mPlayControlView.setAlpha(1f);
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
        }
    }

    @Override
    public void touchReleased() {
        if (percentDirection == 1) {
            //右滑至最大值释放--切歌
            if (MusicPlayService.MPS != null) {
                MusicPlayService.MPS.playNext();
//                String toastString;
//                if (TextUtils.isEmpty(musicArtist) || musicArtist.contains("unknown")) {
//                    toastString = musicName;
//                } else {
//                    toastString = musicArtist + "-" + musicName;
//                }
//                Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
            }
        } else if (percentDirection == 2) {
            //左滑至最大值释放--播放/暂停
            if (MusicPlayService.MPS == null) return;
            isPlay = MusicPlayService.MPS.isPlay();
            if (checkMusicIsNull()) return;
            if (isPlay) {
                MusicPlayService.MPS.playOrPause(musicUrl, musicName, musicArtist, musicId);
                isPlay = MusicPlayService.MPS.isPlay();
                if (rotationAnim != null) rotationAnim.pause();
                closeMusicNote();
                notifyAdapterChange();
            } else {
                MusicPlayService.MPS.playOrPause(musicUrl, musicName, musicArtist, musicId);
                isPlay = MusicPlayService.MPS.isPlay();
                if (rotationAnim != null) rotationAnim.resume();
                openMusicNote();
                notifyAdapterChange();
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
            Toast.makeText(this, "请选择一首歌曲播放", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * 判断是否在播放，如果是则旋转封面
     */
    private void judgePlayRotationAlbum() {
        if (isPlay) {
            if (rotationAnim != null) {
                new Handler().postDelayed(() -> rotationAnim.resume(), 800);
            }
        }
    }

    @Subscribe
    public void onEvent(UpdateTypeModel updateTypeModel) {
        switch (updateTypeModel.updateType) {
            case MUSIC_PALY_CHANGE://切换播放歌曲
                int musicId = updateTypeModel.dataInt;
                refreshPlayingMusic(musicId);
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
        if (musicListFragment != null) musicListFragment.notifyChange(isLikeList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPlayMusicData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rotationAnim != null) rotationAnim.pause();
        closeMusicNote();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        stopService();
    }

}
