package com.ghy.yueplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.bean.OnLineListInfo;
import com.ghy.yueplayer.main.ImageLoader;
import com.ghy.yueplayer.util.ViewHolder;

/**
 * Created by GHY on 2017/7/31.
 * 歌曲榜单列表适配器
 */
public class OnLineMusicListAdapter extends BaseAdapter {

    private OnLineListInfo mMusicInfo;
    private Context mContext;

    public OnLineMusicListAdapter(Context mContext, OnLineListInfo mMusicInfo) {
        this.mContext = mContext;
        this.mMusicInfo = mMusicInfo;
    }

    @Override
    public int getCount() {
        return mMusicInfo.getSong_list().size();
    }

    @Override
    public Object getItem(int i) {
        return mMusicInfo.getSong_list().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_music_list_ol, null);
        }

        ImageView iv_music = ViewHolder.getView(view, R.id.iv_music);
        TextView tv_music_name = ViewHolder.getView(view, R.id.tv_music_name);
        TextView tv_music_num = ViewHolder.getView(view, R.id.tv_music_num);
        TextView tv_music_artist = ViewHolder.getView(view, R.id.tv_music_artist);

        OnLineListInfo.SongListBean musicInfo = mMusicInfo.getSong_list().get(i);

        //歌曲名
        tv_music_name.setText(musicInfo.getTitle());
        //歌手名
        tv_music_artist.setText(musicInfo.getAuthor());

        //专辑封面
        String url = musicInfo.getPic_small();
        ImageLoader.getInstance().loadImage(iv_music, url);

        String position = i + 1 + ".";
        tv_music_num.setText(position);

        //加载动画
        view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.view_show_translate_scale_from_left));

        return view;
    }

}
