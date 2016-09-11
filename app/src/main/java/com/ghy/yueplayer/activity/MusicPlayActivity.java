package com.ghy.yueplayer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.MyPlayerAdapter;
import com.ghy.yueplayer.fragment.LyricFragment;
import com.ghy.yueplayer.fragment.PlayFragment;

import java.util.ArrayList;

public class MusicPlayActivity extends FragmentActivity {

    public static MusicPlayActivity MPAInstance;

    ViewPager viewPager;
    ArrayList<Fragment> listFragments;
    PlayFragment playFragment;
    LyricFragment lyricFragment;
    MyPlayerAdapter playerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        MPAInstance=this;

        initView();

        initViewPager();

    }


    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
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

    /*
    * 按home键进入后台
    * */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (PlayFragment.PFInstance!=null){
            PlayFragment.PFInstance.homeBackground();
        }
        if (LyricFragment.LYFInstance!=null){
            LyricFragment.LYFInstance.homeBackground();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PlayFragment.PFInstance!=null){
            PlayFragment.PFInstance.fromBackgroundBack();
        }
        if (LyricFragment.LYFInstance!=null){
            LyricFragment.LYFInstance.fromBackgroundBack();
        }
    }
}
