package com.ghy.yueplayer.fragment;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ghy.yueplayer.MainActivity;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.MusicListAdapter;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.db.DBHelper;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.util.MediaUtil;
import com.ghy.yueplayer.util.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicListFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static MusicListFragment MLFInstance;
    ListView lv_music;
    MusicListAdapter musicListAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MLFInstance = this;
        initView();

        //扫描音乐并设置adapter
        scanMusic();
    }

    private void scanMusic() {
        //开启异步扫描
        new MusicLoaderTask().execute();
    }

    private void initView() {
        lv_music = (ListView) getActivity().findViewById(R.id.lv_music);

    }

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

        musicListAdapter = new MusicListAdapter(musicInfo, getActivity(), ImageLoader.getInstance());
        lv_music.setAdapter(musicListAdapter);

        //音乐列表点击事件
        lv_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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

                //播放
                MusicPlayService.MPSInstance.playMusic(musicInfo.get(i).getUrl(),
                        musicInfo.get(i).getTitle(), musicInfo.get(i).getArtist());

                //启动音乐页面
//                Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
//                startActivity(intent);

            }
        });

        //长按喜欢该音乐
        lv_music.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                //动画1 item向内缩放
                view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        R.anim.insert_like_item_scale_in));
                //动画2 爱心图片向外缩放
                MainActivity.MainInstance.loveAnim1();
                //动画1 item向外缩放
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                                R.anim.insert_like_item_scale_out));
                        //动画2 爱心图片向内缩放
                        MainActivity.MainInstance.loveAnim2();
                    }
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
                        showToast("已经添加到喜欢列表啦");
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
            }
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
        showToast("添加喜欢成功");
        //重新查询数据库并更新喜欢列表界面
        LikeListFragment.LLFInstance.queryLikeListInfo();
        LikeListFragment.LLFInstance.notifyAdapter();
    }

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
