package com.ghy.yueplayer.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.base.BaseActivity;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.fragment.RecommendFragment;
import com.ghy.yueplayer.fragment.SongListFragment;
import com.ghy.yueplayer.fragment.TopListFragment;
import com.ghy.yueplayer.utils.AnimUtils;
import com.ghy.yueplayer.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 在线歌曲
 */
public class OnLineMusicActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
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
        initDarkModeIcon(Global.DAY_MODE);
        ViewUtils.initSlidingTabLayoutUi(mTabLayout);
    }

    private void initDarkModeIcon(boolean isDayMode) {
        if (isDayMode) {
            mIvBack.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_back, R.color.dn_page_title));
        } else {
            mIvBack.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_back, R.color.dn_page_title_night));
        }
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

        mTvTitle.setOnClickListener(mClickListener);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    mTvTitle.setText("榜单：点我查看其它榜单");
                    mTvTitle.setOnClickListener(mClickListener);
                } else if (i == 1) {
                    mTvTitle.setText("推荐");
                    mTvTitle.setOnClickListener(null);
                } else if (i == 2) {
                    mTvTitle.setText("歌单");
                    mTvTitle.setOnClickListener(null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //设置关联
        mTabLayout.setViewPager(mViewPager);

        mTabLayout.setCurrentTab(0);
    }

    private View.OnClickListener mClickListener = v -> AnimUtils.toLeftAnim(OnLineMusicActivity.this,
            new Intent(OnLineMusicActivity.this, AllTopListActivity.class));

    @OnClick({R.id.iv_back})
    public void onLineMusicClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
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
