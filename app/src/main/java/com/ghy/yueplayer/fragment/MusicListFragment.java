package com.ghy.yueplayer.fragment;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.ghy.yueplayer.MainActivity;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.MusicListRvAdapter;
import com.ghy.yueplayer.base.BaseFragment;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.db.DBHelper;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.utils.MediaUtil;
import com.ghy.yueplayer.utils.SPUtil;
import com.ghy.yueplayer.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicListFragment extends BaseFragment {

    @SuppressLint("StaticFieldLeak")
    public static MusicListFragment MLF;

    private RecyclerView mRecyclerView;
    private MusicListRvAdapter mMusicListRvAdapter;

    private static String AlbumUri = "content://media/external/audio/albumart";

    private DBHelper helper;
    private SQLiteDatabase db;
    private ContentValues cv;
    private List<Map<String, Object>> list_like;
    boolean haveLike = false;

    public MusicListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MLF = this;
        AndPermission.with(getActivity())
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(data -> {
                    // 扫描音乐并设置adapter
                    scanMusic();
                })
                .onDenied(data -> showToast(getString(R.string.storage_tips))).start();
    }

    private void scanMusic() {
        //开启异步扫描
        new MusicLoaderTask().execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music_list;
    }

    @Override
    protected void initView() {
        mRecyclerView = getActivity().findViewById(R.id.music_rv);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMusicListRvAdapter = new MusicListRvAdapter(getActivity(), ImageLoader.getInstance());
        mMusicListRvAdapter.openLoadAnimation();
    }

    @Override
    protected void initData() {

    }

    @SuppressLint("StaticFieldLeak")
    private class MusicLoaderTask extends AsyncTask<Void, Void, List<MusicInfo>> {

        @Override
        protected List<MusicInfo> doInBackground(Void... voids) {
            return MediaUtil.getMusicInfoList(getActivity());
        }

        @Override
        protected void onPostExecute(List<MusicInfo> musicInfo) {
            super.onPostExecute(musicInfo);
            //加载列表
            inflateListView(musicInfo);
            //此处非常重要
            boolean isLikeList = SPUtil.getBooleanSP(getActivity(), Constant.MUSIC_SP, "isLikeList");
            if (!isLikeList) {
                MusicPlayService.musicList = musicInfo;
                SPUtil.saveSP(getActivity(),
                        Constant.MUSIC_SP,
                        "playListNumber", musicInfo.size());
            } else {
                MusicPlayService.musicList = LikeListFragment.likeMusicList;
                SPUtil.saveSP(getActivity(),
                        Constant.MUSIC_SP,
                        "playListNumber", LikeListFragment.likeMusicList.size());
            }
        }
    }

    private void inflateListView(final List<MusicInfo> musicInfo) {
        mRecyclerView.setAdapter(mMusicListRvAdapter);
        mMusicListRvAdapter.setNewData(musicInfo);

        ViewUtils.initRvDivider(mRecyclerView);

        mMusicListRvAdapter.setOnItemClickListener((adapter, view, i) -> {
            if (getActivity() == null) {
                return;
            }

            //普通列表
            SPUtil.saveSP(getActivity(), Constant.MUSIC_SP, "isLikeList", false);
            MusicPlayService.musicList = musicInfo;

            //保存当前正在播放的歌曲信息
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicName", musicInfo.get(i).getTitle());
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicArtist", musicInfo.get(i).getArtist());
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicAlbumUri", AlbumUri + File.separator + musicInfo.get(i).getAlbumId());
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicUrl", musicInfo.get(i).getUrl());
            //保存当前播放歌曲在列表中的位置
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "playListId", i);
            //保存列表中总歌曲数目
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "playListNumber", musicInfo.size());
            //保存歌曲id
            SPUtil.saveSP(getActivity(),
                    Constant.MUSIC_SP,
                    "musicId", musicInfo.get(i).getId());

            //播放
            MusicPlayService.MPS.playMusic(musicInfo.get(i).getUrl(),
                    musicInfo.get(i).getTitle(), musicInfo.get(i).getArtist(), musicInfo.get(i).getId());

            //启动音乐页面
//                Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
//                startActivity(intent);
        });

        mMusicListRvAdapter.setOnItemLongClickListener((adapter, view, i) -> {
            //动画1 item向内缩放
            view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.insert_like_item_scale_in));
            //动画2 爱心图片向外缩放
            MainActivity.MA.loveAnim1();
            //动画1 item向外缩放
            new Handler().postDelayed(() -> {
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        R.anim.insert_like_item_scale_out));
                //动画2 爱心图片向内缩放
                MainActivity.MA.loveAnim2();
            }, 600);

            //插入歌曲信息到喜欢列表数据库
            String musicName = musicInfo.get(i).getTitle();
            String musicArtist = musicInfo.get(i).getArtist();
            int musicAlbumId = musicInfo.get(i).getAlbumId();
            String musicUrl = musicInfo.get(i).getUrl();
            //插入数据前先对比数据库数据，防止重复插入
            list_like = LikeListFragment.list_like;
            if (list_like != null && list_like.size() != 0) {
                for (int k = 0; k < list_like.size(); k++) {
                    if (list_like.get(k).get("musicName").equals(musicName)) {
                        //该歌曲已在喜欢列表
                        haveLike = true;
                        break;
                    }
                }
                if (haveLike) {
                    showToast(getString(R.string.add_like_yes));
                    haveLike = false;
                } else {
                    //该歌曲还没有添加到喜欢列表，插入数据
                    insertLikeMusic(musicName, musicArtist, musicAlbumId, musicUrl);
                }
            } else {
                //数据库无数据则一定不会重复添加，插入数据
                insertLikeMusic(musicName, musicArtist, musicAlbumId, musicUrl);
            }
            return true;
        });
    }

    private void insertLikeMusic(String musicName, String musicArtist, int musicAlbumId, String musicUrl) {
        //数据库操作
        helper = new DBHelper(getActivity());
        db = helper.getWritableDatabase();
        cv = new ContentValues();
        cv.put("musicName", musicName);
        cv.put("musicArtist", musicArtist);
        cv.put("musicAlbumId", musicAlbumId);
        cv.put("musicUrl", musicUrl);
        db.insert("like_music_list", null, cv);
        showToast(getString(R.string.add_like_list));
        //重新查询数据库并更新喜欢列表界面
        LikeListFragment.LLF.queryLikeListInfo();
    }

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public void notifyChange(boolean isPlayLike) {
        if (mMusicListRvAdapter != null) {
            mMusicListRvAdapter.notifyDataSetChanged(isPlayLike);
        }
    }

    public void fastClick(int toMovePosition) {
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            mRecyclerView.getLayoutManager().scrollToPosition(toMovePosition);
        }
    }

    public void scrollTopPosition() {
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            mRecyclerView.getLayoutManager().scrollToPosition(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDarkModeChange(boolean isDayMode) {
        super.onDarkModeChange(isDayMode);
        ViewUtils.initRvDivider(mRecyclerView);
        ViewUtils.clearRecycledViewPool(mRecyclerView);
    }
}
