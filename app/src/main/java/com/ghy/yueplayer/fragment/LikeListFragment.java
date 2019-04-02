package com.ghy.yueplayer.fragment;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.MainActivity;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.LikeListAdapter;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.db.DBHelper;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.util.SPUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeListFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static LikeListFragment LLF;

    private ListView lv_like_music;
    private TextView tv_like_remind;

    private static String AlbumUri = "content://media/external/audio/albumart";

    public static List<MusicInfo> likeMusicList;//喜欢歌曲列表

    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor c;
    public static List<Map<String, Object>> list_like;
    private Map<String, Object> map_like;
    private LikeListAdapter likeAdapter;

    public LikeListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_like_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LLF = this;
        initView();

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
        lv_like_music.setOnItemClickListener((adapterView, view, i, l) -> {

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

        lv_like_music.setOnItemLongClickListener((adapterView, view, i, l) -> {
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
                notifyAdapter();
            }, 1600);
            return true;
        });
    }

    private void setAdapter() {
        likeAdapter = new LikeListAdapter(likeMusicList, getActivity(), ImageLoader.getInstance());
        lv_like_music.setAdapter(likeAdapter);
    }

    public void notifyAdapter() {
        likeAdapter.notifyAdapter(likeMusicList);
    }

    private void initView() {
        lv_like_music = (ListView) getActivity().findViewById(R.id.lv_like_music);
        tv_like_remind = (TextView) getActivity().findViewById(R.id.tv_like_remind);
    }

    /*
     * 查询数据库操作
     * */
    public void queryLikeListInfo() {

        lv_like_music.setVisibility(View.VISIBLE);
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
            lv_like_music.setVisibility(View.GONE);
            tv_like_remind.setVisibility(View.VISIBLE);
            tv_like_remind.setText(R.string.no_like_tips);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (LLF != null) {
            LLF = null;
        }
    }

}
