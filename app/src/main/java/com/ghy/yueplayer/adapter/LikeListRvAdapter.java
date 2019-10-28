package com.ghy.yueplayer.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.bean.MusicInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * @author GHY
 * @date 2015/8/6
 */
public class LikeListRvAdapter extends BaseQuickAdapter<MusicInfo, LikeListRvAdapter.ViewHolder> {

    private Context mContext;
    private ImageLoader mImageLoader;

    private static String mArtworkUri = "content://media/external/audio/albumart";
    private DisplayImageOptions options;

    public LikeListRvAdapter(Context mContext, ImageLoader mImageLoader) {
        super(R.layout.item_music_list, new ArrayList<>());
        this.mContext = mContext;
        this.mImageLoader = mImageLoader;
        initLoader();
    }

    public void notifyAdapter() {
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
    protected void convert(ViewHolder helper, MusicInfo item) {
        if (item != null) {
            // 歌曲名
            helper.tvMusicName.setText(item.getTitle());
            // 歌手名
            helper.tvMusicArtist.setText(item.getArtist());

            // 专辑封面
            String uri = mArtworkUri + File.separator + item.getAlbumId();
            mImageLoader.displayImage(uri, helper.ivMusic, options);
        }
    }

    public class ViewHolder extends BaseViewHolder {

        ImageView ivMusic;
        TextView tvMusicName;
        TextView tvMusicArtist;

        public ViewHolder(View view) {
            super(view);
            ivMusic = view.findViewById(R.id.iv_music);
            tvMusicName = view.findViewById(R.id.tv_music_name);
            tvMusicArtist = view.findViewById(R.id.tv_music_artist);
        }
    }
}
