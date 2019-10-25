package com.ghy.yueplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.utils.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

/**
 * @author GHY
 * @date 2015/8/6
 */
public class MusicListAdapter extends BaseAdapter {

    private List<MusicInfo> mMusicInfo;
    private Context mContext;
    private ImageLoader mImageLoader;
    /**
     * 是否在播放喜欢列表
     */
    private boolean isPlayLike;

    private static String mArtworkUri = "content://media/external/audio/albumart";
    private DisplayImageOptions options;

    public MusicListAdapter(List<MusicInfo> mMusicInfo, Context mContext, ImageLoader mImageLoader) {
        this.mMusicInfo = mMusicInfo;
        this.mContext = mContext;
        this.mImageLoader = mImageLoader;
        initLoader();
    }

    private void initLoader() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.default_artist)
                .showImageForEmptyUri(R.mipmap.default_artist)
                .showImageOnFail(R.mipmap.default_artist).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();
    }

    public void notifyDataSetChanged(boolean isPlayLike) {
        this.isPlayLike = isPlayLike;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMusicInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return mMusicInfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_music_list, null);
            // 加载动画
            view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.view_show_translate_scale_from_left));
        }

        ImageView ivMusic = ViewHolder.getView(view, R.id.iv_music);
        TextView tvMusicName = ViewHolder.getView(view, R.id.tv_music_name);
        TextView tvMusicArtist = ViewHolder.getView(view, R.id.tv_music_artist);
        ImageView ivPlayHorn = ViewHolder.getView(view, R.id.iv_play_horn);

        MusicInfo musicInfo = mMusicInfo.get(i);

        //歌曲名
        tvMusicName.setText(musicInfo.getTitle());
        //歌手名
        tvMusicArtist.setText(musicInfo.getArtist());

        //专辑封面
        String uri = mArtworkUri + File.separator + musicInfo.getAlbumId();
        mImageLoader.displayImage(uri, ivMusic, options);

        if (!isPlayLike) {
            if (musicInfo.isPlaying()) {
                ivPlayHorn.setVisibility(View.VISIBLE);
            } else {
                ivPlayHorn.setVisibility(View.GONE);
            }
        } else {
            ivPlayHorn.setVisibility(View.GONE);
        }

        return view;
    }
}
