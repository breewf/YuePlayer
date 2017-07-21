package com.ghy.yueplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.activity.AboutActivity;
import com.ghy.yueplayer.activity.HelpActivity;
import com.ghy.yueplayer.activity.MusicPlayActivity;
import com.ghy.yueplayer.activity.SetActivity;
import com.ghy.yueplayer.activity.TimeActivity;
import com.ghy.yueplayer.adapter.MyPlayerAdapter;
import com.ghy.yueplayer.fragment.LikeListFragment;
import com.ghy.yueplayer.fragment.MusicListFragment;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.main.ImageLoader;
import com.ghy.yueplayer.main.PlayControlView;
import com.ghy.yueplayer.main.VDHLayout;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.service.TimeService;
import com.ghy.yueplayer.util.SPUtil;
import com.ghy.yueplayer.view.HeroTextView;

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

    ImageView local_music, favour_music;
    ImageView app_icon;
    HeroTextView tv_app_name;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout drawer_content;//侧滑菜单布局

    private VDHLayout mVDHLayout;
    private ImageView mPlayerImageView;
    private PlayControlView mPlayControlView;
    private TextView mTvPlayTip;
    private int percentDirection = 0;//0:未达到最大值，1:右滑至最大值，2:左滑至最大值
    private ObjectAnimator rotationAnim;
    private boolean isPlay = false;

    String musicUrl;
    String musicName;
    String musicArtist;
    String musicAlbumUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainInstance = this;

        //启动音乐服务
        Intent service = new Intent(this, MusicPlayService.class);
        this.startService(service);
        //启动时间统计服务
        Intent timeService = new Intent(this, TimeService.class);
        this.startService(timeService);

        initView();

        refreshPlayMusicData();

        initViewPager();

        setOnclickListener();

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_content = (RelativeLayout) findViewById(R.id.drawer_content);

        mVDHLayout = (VDHLayout) findViewById(R.id.vdh_layout);
        mPlayerImageView = (ImageView) findViewById(R.id.iv_play);
        mPlayControlView = (PlayControlView) findViewById(R.id.play_control_view);
        mTvPlayTip = (TextView) findViewById(R.id.tv_play_tip);
        mPlayControlView.setVisibility(View.INVISIBLE);
        mTvPlayTip.setVisibility(View.INVISIBLE);

        mVDHLayout.setTouchDirectionListener(this);
        mVDHLayout.setTouchReleasedListener(this);

        mPlayerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MusicPlayActivity.class));
            }
        });

        rotationAnim = ObjectAnimator.ofFloat(mPlayerImageView, "rotation", 0f, 360f);
        rotationAnim.setDuration(10000);
        rotationAnim.setInterpolator(new LinearInterpolator());
        rotationAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnim.start();
        rotationAnim.pause();
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

        if (MusicPlayService.MPSInstance != null) {
            isPlay = MusicPlayService.MPSInstance.isPlay();
        }
        ImageLoader.getInstance().loadImageError(mPlayerImageView, musicAlbumUri, R.mipmap.default_artist);
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
            startActivity(new Intent(MainActivity.this, MusicPlayActivity.class));
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                favour_music.setImageResource(R.mipmap.note_btn_love);
            }
        }, 800);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                favour_music.setImageResource(R.mipmap.note_btn_loved_white);
            }
        }, 800);
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int id = view.getId();
                    switch (id) {
                        case R.id.layout1:
                            showToast("欢迎您使用YuePlayer");
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
                }
            }, 400);
            mDrawerLayout.closeDrawer(drawer_content);
        }

    }

    private void startActivity(Class<?> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }

    /*
    * 停止音乐播放服务和统计服务
    * */
    private void stopService() {
        if (MusicPlayService.MPSInstance != null) MusicPlayService.MPSInstance.stopSelf();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == app_icon) {
            showToast("侧滑菜单");
        } else if (view == tv_app_name) {
            showToast("播放页面");
        } else if (view == local_music) {
            showToast("音乐列表");
        } else if (view == favour_music) {
            showToast("喜欢列表");
        }
        return true;
    }

    @Override
    public void touchDirection(int direction, int percent) {
        switch (direction) {
            case VDHLayout.DIRECTION_RIGHT:
                Log.i(TAG, "右滑-->>" + percent + "%");
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
                Log.i(TAG, "左滑-->>" + percent + "%");
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
                Log.i(TAG, "原点-->>" + percent + "%");
                mPlayControlView.setVisibility(View.INVISIBLE);
                mPlayControlView.setSlidePercent(VDHLayout.DIRECTION_ORIGIN, percent, isPlay);
                break;
        }
    }

    @Override
    public void touchReleased() {
        if (percentDirection == 1) {
            //右滑至最大值释放--切歌
            if (MusicPlayService.MPSInstance != null) {
                MusicPlayService.MPSInstance.playNext();
                refreshPlayMusicData();
                Toast.makeText(this, musicArtist + "-" + musicName, Toast.LENGTH_SHORT).show();
                if (rotationAnim != null) rotationAnim.resume();
            }
        } else if (percentDirection == 2) {
            //左滑至最大值释放--播放/暂停
            if (MusicPlayService.MPSInstance == null) return;
            isPlay = MusicPlayService.MPSInstance.isPlay();
            if (isPlay) {
                if (TextUtils.isEmpty(musicUrl)) {
                    Toast.makeText(this, "请选择一首歌曲播放", Toast.LENGTH_SHORT).show();
                    return;
                }
                MusicPlayService.MPSInstance.playOrPause(musicUrl, musicName, musicArtist);
                isPlay = MusicPlayService.MPSInstance.isPlay();
                if (rotationAnim != null) rotationAnim.pause();
            } else {
                if (TextUtils.isEmpty(musicUrl)) {
                    Toast.makeText(this, "请选择一首歌曲播放", Toast.LENGTH_SHORT).show();
                    return;
                }
                MusicPlayService.MPSInstance.playOrPause(musicUrl, musicName, musicArtist);
                isPlay = MusicPlayService.MPSInstance.isPlay();
                if (rotationAnim != null) rotationAnim.resume();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPlayMusicData();
        if (isPlay) {
            if (rotationAnim != null) rotationAnim.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rotationAnim != null) rotationAnim.pause();
    }

}
