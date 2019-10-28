package com.ghy.yueplayer.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.MyPlayerAdapter;
import com.ghy.yueplayer.base.BaseActivity;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.component.blurlibrary.EasyBlur;
import com.ghy.yueplayer.fragment.PlayFragment;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.utils.AppUtils;
import com.ghy.yueplayer.utils.SPUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author HY
 */
public class MusicPlayActivity extends BaseActivity {

    @SuppressLint("StaticFieldLeak")
    public static MusicPlayActivity MPA;

    View positionView;
    ImageView ivBg;
    ViewPager viewPager;
    ArrayList<Fragment> listFragments;
    PlayFragment playFragment;
    MyPlayerAdapter playerAdapter;

    boolean isOpenAlbumColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MPA = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            viewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                startPostponedEnterTransition();
                            }
                            return true;
                        }
                    });
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_music_play;
    }

    @Override
    protected void initView() {
        positionView = findViewById(R.id.position_view);
        ivBg = findViewById(R.id.iv_bg);
        viewPager = findViewById(R.id.viewPager);

        initStatusBar();
        initViewPager();

        String musicAlbumUri = SPUtil.getStringSP(this,
                Constant.MUSIC_SP, "musicAlbumUri");
        isOpenAlbumColor = PreferManager.getBoolean(PreferManager.ALBUM_COLOR, false);
        if (isOpenAlbumColor && !TextUtils.isEmpty(musicAlbumUri)) {
            setPlayBackgroundImage(musicAlbumUri);
        }
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    @Override
    protected void initData() {

    }

    private void initViewPager() {
        listFragments = new ArrayList<>();
        playFragment = new PlayFragment();
        listFragments.add(playFragment);

        playerAdapter = new MyPlayerAdapter(getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(playerAdapter);
    }

    private void initStatusBar() {
        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.TRANSPARENT);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setNavigationBarColor(Color.TRANSPARENT);
            } else {
                getWindow()
                        .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        ViewGroup.LayoutParams lp = positionView.getLayoutParams();
        lp.height = AppUtils.getStatusBarHeight(this);
        positionView.setLayoutParams(lp);
    }

    public void setPlayBackgroundImage(String url) {
        if (isOpenAlbumColor && !TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            // 读取uri所在的图片
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Bitmap finalBitmap = EasyBlur.with(MusicPlayActivity.this)
                        .bitmap(bitmap)
                        .radius(20)
                        .scale(15)
                        .blur();
                ivBg.setImageBitmap(finalBitmap);
            } catch (IOException e) {
                ivBg.setImageResource(R.mipmap.ic_background);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (PlayFragment.PF != null) {
            if (PlayFragment.PF.isLyricVisibility()) {
                return;
            } else {
                PlayFragment.PF.backPressed();
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (PlayFragment.PF != null) {
            PlayFragment.PF.homeBackground();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PlayFragment.PF != null) {
            PlayFragment.PF.fromBackgroundBack();
        }
    }
}
