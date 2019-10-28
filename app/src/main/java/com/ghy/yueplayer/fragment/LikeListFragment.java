package com.ghy.yueplayer.fragment;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.MainActivity;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.LikeListRvAdapter;
import com.ghy.yueplayer.base.BaseFragment;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.db.DBHelper;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.utils.SPUtil;
import com.ghy.yueplayer.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeListFragment extends BaseFragment {

    @SuppressLint("StaticFieldLeak")
    public static LikeListFragment LLF;

    private TextView tv_like_remind;

    private RecyclerView mRecyclerView;
    private LikeListRvAdapter mLikeListRvAdapter;

    private static String AlbumUri = "content://media/external/audio/albumart";

    /**
     * 喜欢歌曲列表
     */
    public static List<MusicInfo> likeMusicList;

    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor c;
    public static List<Map<String, Object>> list_like;
    private Map<String, Object> map_like;

    public LikeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LLF = this;
        //数据库操作
        helper = new DBHelper(getActivity());
        db = helper.getWritableDatabase();
        //查询喜欢列表数据库
        queryLikeListInfo();
        //设置Adapter
        setAdapter();
        //设置点击事件
        setOnClickListener();
    }

    private void setOnClickListener() {
        mLikeListRvAdapter.setOnItemClickListener((adapter, view, i) -> {
            if (getActivity() == null) {
                return;
            }

            //喜欢列表
            SPUtil.saveSP(getActivity(), Constant.MUSIC_SP, "isLikeList", true);
            MusicPlayService.musicList = likeMusicList;

            //保存当前正在播放的歌曲信息
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicName", likeMusicList.get(i).getTitle());
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicArtist", likeMusicList.get(i).getArtist());
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicAlbumUri", AlbumUri + File.separator + likeMusicList.get(i).getAlbumId());
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicUrl", likeMusicList.get(i).getUrl());
            //保存当前播放歌曲在喜欢列表中的位置
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "playListId", i);
            //保存喜欢列表中的总歌曲数目
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "playListNumber", likeMusicList.size());

            //播放
            MusicPlayService.MPS.playMusic(likeMusicList.get(i).getUrl(),
                    likeMusicList.get(i).getTitle(), likeMusicList.get(i).getArtist(), 0);

            //启动音乐页面
//                Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
//                startActivity(intent);
        });

        mLikeListRvAdapter.setOnItemLongClickListener((adapter, view, i) -> {
            //取消喜欢
            //动画1 item向内缩放
            view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.insert_like_item_scale_in));
            //动画2 爱心图片向外缩放
            MainActivity.MA.loveAnim3();
            //动画1 item向左侧消失
            new Handler().postDelayed(() -> {
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        R.anim.delete_like_item_translate_to_left));
                //动画2 爱心图片向内缩放
                MainActivity.MA.loveAnim4();
            }, 600);

            Toast.makeText(getActivity(), R.string.remove_like_list, Toast.LENGTH_SHORT).show();

            final String musicName = likeMusicList.get(i).getTitle();
            new Handler().postDelayed(() -> {
                db.execSQL("delete from like_music_list where musicName=?",
                        new Object[]{musicName});
                //重新查询设置adapter
                queryLikeListInfo();
            }, 1600);
            return true;
        });
    }

    private void setAdapter() {
        mRecyclerView.setAdapter(mLikeListRvAdapter);
        mLikeListRvAdapter.setNewData(likeMusicList);

        ViewUtils.initRvDivider(mRecyclerView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_like_list;
    }

    @Override
    protected void initView() {
        mRecyclerView = getActivity().findViewById(R.id.music_rv_like);
        tv_like_remind = getActivity().findViewById(R.id.tv_like_remind);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLikeListRvAdapter = new LikeListRvAdapter(getActivity(), ImageLoader.getInstance());
        mRecyclerView.setItemAnimator(null);
    }

    @Override
    protected void initData() {

    }

    /**
     * 查询数据库操作
     */
    public void queryLikeListInfo() {

        tv_like_remind.setVisibility(View.GONE);

        c = db.query("like_music_list", new String[]{"_id", "musicName", "musicArtist",
                "musicAlbumId", "musicUrl"}, null, null, null, null, null);
        list_like = new ArrayList<>();
        likeMusicList = new ArrayList<>();
        c.moveToFirst();
        // 数据库无数据时c.getCount()==0
        if (c.getCount() != 0) {
            do {
                //添加到集合，用于插入数据时判断是否重复插入
                map_like = new HashMap<>();
                map_like.put("musicName", c.getString(c.getColumnIndex("musicName")));
                map_like.put("musicArtist", c.getString(c.getColumnIndex("musicArtist")));
                map_like.put("musicAlbumId", c.getInt(c.getColumnIndex("musicAlbumId")));
                map_like.put("musicUrl", c.getString(c.getColumnIndex("musicUrl")));
                list_like.add(map_like);
                //添加到列表
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setTitle(c.getString(c.getColumnIndex("musicName")));
                musicInfo.setArtist(c.getString(c.getColumnIndex("musicArtist")));
                musicInfo.setAlbumId(c.getInt(c.getColumnIndex("musicAlbumId")));
                musicInfo.setUrl(c.getString(c.getColumnIndex("musicUrl")));
                likeMusicList.add(musicInfo);
            } while (c.moveToNext());
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //保存新的喜欢歌曲数目和歌曲列表，若添加后不保存，则下一曲上一曲功能将不适用于最新添加的歌曲
            //非常重要！前提是当前正在播放喜欢的歌曲列表
            boolean isLikeList = SPUtil.getBooleanSP(getActivity(), Constant.MUSIC_SP, "isLikeList");
            if (isLikeList) {
                SPUtil.saveSP(getActivity(),
                        Constant.MUSIC_SP,
                        "playListNumber", likeMusicList.size());
                MusicPlayService.musicList = likeMusicList;
            }

        } else {
            tv_like_remind.setVisibility(View.VISIBLE);
            tv_like_remind.setText(R.string.no_like_tips);
        }

        // 设置新的数据源
        mLikeListRvAdapter.setNewData(likeMusicList);
        scrollRvToPosition(likeMusicList.size());
    }

    private void scrollRvToPosition(int position) {
        if (mLikeListRvAdapter == null) {
            return;
        }
        int goPosition = position + mLikeListRvAdapter.getHeaderLayoutCount();
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null && goPosition >= 0) {
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(goPosition, 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (LLF != null) {
            LLF = null;
        }
    }

    @Override
    public void onDarkModeChange(boolean isDayMode) {
        super.onDarkModeChange(isDayMode);
        ViewUtils.initRvDivider(mRecyclerView);
        ViewUtils.clearRecycledViewPool(mRecyclerView);
    }

}
