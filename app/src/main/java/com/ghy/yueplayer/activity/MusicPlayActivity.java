package com.ghy.yueplayer.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.MyPlayerAdapter;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.fragment.LyricFragment;
import com.ghy.yueplayer.fragment.PlayFragment;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.main.ImageLoader;
import com.ghy.yueplayer.util.AppUtils;
import com.ghy.yueplayer.util.SPUtil;

import java.util.ArrayList;

public class MusicPlayActivity extends FragmentActivity {

    @SuppressLint("StaticFieldLeak")
    public static MusicPlayActivity MPAInstance;

    View positionView;
    ImageView ivBg;
    ViewPager viewPager;
    ArrayList<Fragment> listFragments;
    PlayFragment playFragment;
    LyricFragment lyricFragment;
    MyPlayerAdapter playerAdapter;

    boolean isOpenAlbumColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        MPAInstance = this;

        initView();

        initStatusBar();

        initViewPager();

    }


    private void initView() {
        positionView = findViewById(R.id.position_view);
        ivBg = (ImageView) findViewById(R.id.iv_bg);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        String musicAlbumUri = SPUtil.getStringSP(this,
                Constant.MUSIC_SP, "musicAlbumUri");
        isOpenAlbumColor = PreferManager.getBoolean(PreferManager.ALBUM_COLOR, false);
        if (isOpenAlbumColor && !TextUtils.isEmpty(musicAlbumUri)) {
            setPlayBackgroundImage(musicAlbumUri);
        }
    }


    private void initViewPager() {

        listFragments = new ArrayList<>();
        playFragment = new PlayFragment();
        lyricFragment = new LyricFragment();
        listFragments.add(playFragment);
        listFragments.add(lyricFragment);

        playerAdapter = new MyPlayerAdapter(getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(playerAdapter);
    }

    private void initStatusBar() {
        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow()
                        .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        ViewGroup.LayoutParams lp = positionView.getLayoutParams();
        lp.height = AppUtils.getStatusBarHeight(this);
        positionView.setLayoutParams(lp);
    }

    public void setPlayBackgroundImage(String uri) {
        if (isOpenAlbumColor && !TextUtils.isEmpty(uri)) {
            ImageLoader.getInstance().loadBlurImage(ivBg, uri);
        }
    }

    /*
    * 按home键进入后台
    * */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (PlayFragment.PFInstance != null) {
            PlayFragment.PFInstance.homeBackground();
        }
        if (LyricFragment.LYFInstance != null) {
            LyricFragment.LYFInstance.homeBackground();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PlayFragment.PFInstance != null) {
            PlayFragment.PFInstance.fromBackgroundBack();
        }
        if (LyricFragment.LYFInstance != null) {
            LyricFragment.LYFInstance.fromBackgroundBack();
        }
    }
}
