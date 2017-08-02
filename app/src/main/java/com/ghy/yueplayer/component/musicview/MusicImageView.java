package com.ghy.yueplayer.component.musicview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.ghy.yueplayer.R;

/**
 * Created by GHY on 2017/8/2.
 * Desc: 音符图片
 */

public class MusicImageView extends android.support.v7.widget.AppCompatImageView {

    Bitmap bitmap_music1;
    Bitmap bitmap_music2;
    Bitmap bitmap_music3;

//    Bitmap bitmapInt[] = new Bitmap[3];

    public MusicImageView(Context context) {
        this(context, null);
    }

    public MusicImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bitmap_music1 = BitmapFactory.decodeResource(getResources(), R.mipmap.image_music_note1);
//        bitmap_music2 = BitmapFactory.decodeResource(getResources(), R.mipmap.image_music_note2);
//        bitmap_music3 = BitmapFactory.decodeResource(getResources(), R.mipmap.image_music_note3);
//        bitmapInt[0] = bitmap_music1;
//        bitmapInt[1] = bitmap_music2;
//        bitmapInt[2] = bitmap_music3;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(bitmap_music1.getWidth(), bitmap_music1.getHeight());
    }

    public void setColor(int color) {
        setImageBitmap(createColor(color));
    }

    private Bitmap createColor(int color) {
        int heartWidth = bitmap_music1.getWidth();
        int heartHeight = bitmap_music1.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(heartWidth, heartHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
//        canvas.drawBitmap(bitmapInt[new Random().nextInt(3)], 0, 0, paint);
        canvas.drawBitmap(bitmap_music1, 0, 0, paint);
        canvas.drawColor(color, PorterDuff.Mode.SRC_ATOP);
        canvas.setBitmap(null);
        return newBitmap;
    }

}