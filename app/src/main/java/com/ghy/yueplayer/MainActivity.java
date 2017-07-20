package com.ghy.yueplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ghy.yueplayer.activity.AboutActivity;
import com.ghy.yueplayer.activity.HelpActivity;
import com.ghy.yueplayer.activity.MusicPlayActivity;
import com.ghy.yueplayer.activity.SetActivity;
import com.ghy.yueplayer.activity.TimeActivity;
import com.ghy.yueplayer.adapter.MyPlayerAdapter;
import com.ghy.yueplayer.fragment.LikeListFragment;
import com.ghy.yueplayer.fragment.MusicListFragment;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.service.TimeService;
import com.ghy.yueplayer.view.HeroTextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener {

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
                } else if (position == 1) {
                    favour_music.setImageResource(R.mipmap.note_btn_loved_white);
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
        } else if (view == favour_music) {
            viewPager_main.setCurrentItem(1);
            favour_music.setImageResource(R.mipmap.note_btn_loved_white);
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
        if(MusicPlayService.MPSInstance != null) MusicPlayService.MPSInstance.stopSelf();
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
}
