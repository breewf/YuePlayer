package com.ghy.yueplayer.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;

import com.flyco.tablayout.SlidingTabLayout;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.base.BaseActivity;
import com.ghy.yueplayer.fragment.RecommendFragment;
import com.ghy.yueplayer.fragment.SongListFragment;
import com.ghy.yueplayer.fragment.TopListFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 在线歌曲
 */
public class OnLineMusicActivity extends BaseActivity {

    @Bind(R.id.et_search)
    EditText mEtSearch;
    @Bind(R.id.tab_layout)
    SlidingTabLayout mTabLayout;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    private MyPagerAdapter mAdapter;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] mTitles = {"榜单", "推荐", "歌单"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_on_line_music;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mFragments.add(new TopListFragment());
        mFragments.add(new RecommendFragment());
        mFragments.add(new SongListFragment());
        //设置adapter
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);

        //设置关联
        mTabLayout.setViewPager(mViewPager);
    }

    @OnClick({R.id.iv_back})
    public void onLineMusicClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * ViewPager适配器
     */
    private class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

}
