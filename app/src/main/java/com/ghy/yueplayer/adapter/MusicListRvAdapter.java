package com.ghy.yueplayer.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.bean.MusicInfo;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.utils.ViewUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * @author GHY
 * @date 2015/8/6
 */
public class MusicListRvAdapter extends BaseQuickAdapter<MusicInfo, MusicListRvAdapter.ViewHolder> {

    private Context mContext;
    private ImageLoader mImageLoader;
    /**
     * 是否在播放喜欢列表
     */
    private boolean isPlayLike;

    private static String mArtworkUri = "content://media/external/audio/albumart";
    private DisplayImageOptions options;

    public MusicListRvAdapter(Context mContext, ImageLoader mImageLoader) {
        super(R.layout.item_music_list,new ArrayList<>());
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
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected void convert(ViewHolder helper, MusicInfo item) {
        //歌曲名
        helper.tvMusicName.setText(item.getTitle());
        //歌手名
        helper.tvMusicArtist.setText(item.getArtist());

        //专辑封面
        String uri = mArtworkUri + File.separator + item.getAlbumId();
        mImageLoader.displayImage(uri, helper.ivMusic, options);

        if (!isPlayLike) {
            if (item.isPlaying()) {
                helper.ivPlayHorn.setVisibility(View.VISIBLE);
            } else {
                helper.ivPlayHorn.setVisibility(View.GONE);
            }
        } else {
            helper.ivPlayHorn.setVisibility(View.GONE);
        }

        if (Global.DAY_MODE) {
            helper.ivPlayHorn.setImageDrawable(ViewUtils.getTintDrawable(mContext,
                    R.mipmap.icon_play_horn, R.color.dn_page_title));
        } else {
            helper.ivPlayHorn.setImageDrawable(ViewUtils.getTintDrawable(mContext,
                    R.mipmap.icon_play_horn, R.color.dn_page_title_night));
        }
    }

    public class ViewHolder extends BaseViewHolder {

        ImageView ivMusic;
        TextView tvMusicName;
        TextView tvMusicArtist;
        ImageView ivPlayHorn;

        public ViewHolder(View view) {
            super(view);
            ivMusic = view.findViewById(R.id.iv_music);
            tvMusicName = view.findViewById(R.id.tv_music_name);
            tvMusicArtist = view.findViewById(R.id.tv_music_artist);
            ivPlayHorn = view.findViewById(R.id.iv_play_horn);
        }
    }
}
