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
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

/**
 * Created by GHY on 2015/8/6.
 */
public class LikeListAdapter extends BaseAdapter {

    private List<MusicInfo> mMusicInfo;
    private Context mContext;
    private ImageLoader mImageLoader;

    private static String mArtworkUri = "content://media/external/audio/albumart";
    private DisplayImageOptions options;

    public LikeListAdapter(List<MusicInfo> mMusicInfo, Context mContext, ImageLoader mImageLoader) {
        this.mMusicInfo = mMusicInfo;
        this.mContext = mContext;
        this.mImageLoader = mImageLoader;
        initLoader();
    }

    public void notifyAdapter(List<MusicInfo> mMusicInfo) {
        this.mMusicInfo = mMusicInfo;
        notifyDataSetChanged();
    }

    private void initLoader() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.default_artist)
                .showImageForEmptyUri(R.mipmap.default_artist)
                .showImageOnFail(R.mipmap.default_artist).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();
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


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_music_list, null);
        }

        ImageView iv_music = ViewHolder.getView(view, R.id.iv_music);
        TextView tv_music_name = ViewHolder.getView(view, R.id.tv_music_name);
        TextView tv_music_artist = ViewHolder.getView(view, R.id.tv_music_artist);

        MusicInfo musicInfo = mMusicInfo.get(i);

        //歌曲名
        tv_music_name.setText(musicInfo.getTitle());
        //歌手名
        tv_music_artist.setText(musicInfo.getArtist());

        //专辑封面
        String uri = mArtworkUri + File.separator + musicInfo.getAlbumId();
        mImageLoader.displayImage(uri, iv_music, options);

        // 加载动画
        boolean isOpenListAnim = PreferManager.getBoolean(PreferManager.LIST_ANIM, false);
        if (isOpenListAnim) {
            view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.view_show_translate_scale_from_left));
        }

        return view;
    }
}
