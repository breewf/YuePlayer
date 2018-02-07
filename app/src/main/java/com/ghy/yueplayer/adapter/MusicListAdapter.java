package com.ghy.yueplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

import io.gresse.hugo.vumeterlibrary.VuMeterView;

/**
 * Created by GHY on 2015/8/6.
 */
public class MusicListAdapter extends BaseAdapter {

    private List<MusicInfo> mMusicInfo;
    private Context mContext;
    private ImageLoader mImageLoader;
    private boolean isPlayLike;//是否在播放喜欢列表

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


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_music_list, null);
        }

        ImageView iv_music = ViewHolder.getView(view, R.id.iv_music);
        TextView tv_music_name = ViewHolder.getView(view, R.id.tv_music_name);
        TextView tv_music_artist = ViewHolder.getView(view, R.id.tv_music_artist);
        VuMeterView vumeter = ViewHolder.getView(view, R.id.vumeter);

        MusicInfo musicInfo = mMusicInfo.get(i);

        //歌曲名
        tv_music_name.setText(musicInfo.getTitle());
        //歌手名
        tv_music_artist.setText(musicInfo.getArtist());

        //专辑封面
        String uri = mArtworkUri + File.separator + musicInfo.getAlbumId();
        mImageLoader.displayImage(uri, iv_music, options);

        if (!isPlayLike) {
            if (musicInfo.isPlaying()) {
                vumeter.setVisibility(View.VISIBLE);
                if (MusicPlayService.MPSInstance.isPlay()) {
                    vumeter.resume(true);
                } else {
                    vumeter.stop(true);
                }
            } else {
                vumeter.setVisibility(View.GONE);
            }
        } else {
            vumeter.setVisibility(View.GONE);
        }

        //加载动画
//        view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.view_show_translate_scale_from_left));

        return view;
    }
}
