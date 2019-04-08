package com.ghy.yueplayer.main;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ghy.yueplayer.PlayerApplication;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @author HY
 * @date 2017/7/14
 * 图片加载类
 */
public class ImageLoader {

    private Context context;

    private OnProgressListener onProgressListener;

    private ImageLoader(Context context) {
        this.context = context;
    }

    private static class ImageLoaderHolder {
        static ImageLoader instance = new ImageLoader(PlayerApplication.getInstance().getApplicationContext());
    }

    public static ImageLoader getInstance() {
        return ImageLoaderHolder.instance;
    }

    public interface OnProgressListener {
        void onProgress(String url, int progress);
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    /**
     * 使用Glide加载网络图片
     */
    public void loadImage(ImageView imageView, String url) {
        Glide.with(context).load(url).crossFade().into(imageView);
    }

    /**
     * 加载网络图片
     */
    public void loadImage(ImageView imageView, String url, int placeholderResId) {
        Glide.with(context).load(url).crossFade().placeholder(placeholderResId).into(imageView);
    }

    /**
     * 加载网络图片
     */
    public void loadImageUri(ImageView imageView, Uri uri) {
        Glide.with(context).load(uri).crossFade().into(imageView);
    }

    /**
     * 加载网络图片
     */
    public void loadImage(ImageView imageView, String url, int placeholderResId, int errorResId) {
        Glide.with(context).load(url).crossFade().placeholder(placeholderResId).error(errorResId).into(imageView);
    }

    /**
     * 加载网络图片
     */
    public void loadImageError(ImageView imageView, String url, int errorResId) {
        Glide.with(context).load(url).crossFade().error(errorResId).into(imageView);
    }

    /**
     * 加载网络-圆角图片
     */
    public void loadRoundImage(ImageView imageView, String url, int placeholderResId, int radius) {
        Glide.with(context).load(url).crossFade().placeholder(placeholderResId).bitmapTransform(new RoundedCornersTransformation(context, radius, 0)).into(imageView);
    }

    /**
     * 加载网络-圆形图片
     */
    public void loadCircleImage(ImageView imageView, String url) {
        Glide.with(context).load(url).crossFade().bitmapTransform(new CropCircleTransformation(context)).into(imageView);
    }

    /**
     * 加载本地-圆形图片
     */
    public void loadCircleImage(ImageView imageView, int resId) {
        Glide.with(context).load(resId).crossFade().bitmapTransform(new CropCircleTransformation(context)).into(imageView);
    }

    /**
     * 加载网络-圆形图片
     */
    public void loadCircleImage(ImageView imageView, String url, int placeholderResId) {
        Glide.with(context).load(url).crossFade().placeholder(placeholderResId).bitmapTransform(new CropCircleTransformation(context)).into(imageView);
    }

    /**
     * 加载网络-方形图片
     */
    public void loadSquareImage(ImageView imageView, String url) {
        Glide.with(context).load(url).crossFade().bitmapTransform(new CropSquareTransformation(context)).into(imageView);
    }

    /**
     * 加载网络-方形图片
     */
    public void loadSquareImage(ImageView imageView, String url, int placeholderResId) {
        Glide.with(context).load(url).crossFade().placeholder(placeholderResId).bitmapTransform(new CropSquareTransformation(context)).into(imageView);
    }

    /**
     * 加载网络-高斯模糊图片
     */
    public void loadBlurImage(ImageView imageView, String url) {
        Glide.with(context).load(url).crossFade().bitmapTransform(new BlurTransformation(context, 40, 10)).into(imageView);
    }

    /**
     * 加载URI-高斯模糊图片
     */
    public void loadBlurImage(ImageView imageView, Uri uri) {
        Glide.with(context).load(uri).crossFade().bitmapTransform(new BlurTransformation(context, 40, 10)).into(imageView);
    }

    /**
     * 加载网络-高斯模糊图片
     */
    public void loadBlurImage(ImageView imageView, String url, int placeholderResId) {
        Glide.with(context).load(url).crossFade().placeholder(placeholderResId).bitmapTransform(new BlurTransformation(context)).into(imageView);
    }

    /**
     * 加载网络gif图片
     */
    public void loadGifImage(ImageView imageView, String url) {
        Glide.with(context).load(url).asGif().into(imageView);
    }

    /**
     * 加载本地gif图片
     */
    public void loadGifImage(ImageView imageView, int resId) {
        Glide.with(context).load(resId).asGif().into(imageView);
    }

    /**
     * 加载网络图片
     * 自定义加载动画
     */
    public void loadImageAnim(ImageView imageView, String url, int animateId) {
        Glide.with(context).load(url).animate(animateId).into(imageView);
    }

    /**
     * 加载网络图片
     * 自定义加载动画
     */
    public void loadImageAnim(ImageView imageView, String url, int placeholderResId, int animateId) {
        Glide.with(context).load(url).placeholder(placeholderResId).animate(animateId).into(imageView);
    }

}
