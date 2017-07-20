package com.ghy.yueplayer.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by GHY on 2017/7/18.
 * Desc:
 */

public class PlayControlView extends View {

    private Paint mPaint;
    private Paint mPaintColor;
    private Paint mPaintControl;

    private Xfermode mXfermode;
    private PorterDuff.Mode mPorterDuffMode = PorterDuff.Mode.DST_IN;

    private float centerX, centerY;
    private float leftCenterX, rightCenterX;
    private float radius;//半径
    private float diameter;//直径
    private float rate;//变化百分比小数值

    private int direction;//滑动方向
    private int percent;//滑动百分比
    private boolean canvasColor = false;

    private boolean isPlay = false;

    public PlayControlView(Context context) {
        this(context, null);
    }

    public PlayControlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
//        mPaint.setColor(Color.WHITE);
        mPaint.setColor(Color.parseColor("#999999"));
        mPaint.setStyle(Paint.Style.FILL);

        mPaintColor = new Paint();
        mPaintColor.setAntiAlias(true);
        mPaintColor.setColor(Color.parseColor("#ffffff"));
        mPaintColor.setStyle(Paint.Style.FILL);

        mPaintControl = new Paint();
        mPaintControl.setAntiAlias(true);
        mPaintControl.setColor(Color.parseColor("#000000"));
        mPaintControl.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintControl.setStrokeWidth(10);
        mPaintControl.setStrokeCap(Paint.Cap.ROUND);
        mPaintControl.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        radius = getWidth() / 2;
        diameter = radius * 2;
        leftCenterX = centerX - diameter;
        rightCenterX = centerX + diameter;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, radius, mPaint);
        if (canvasColor) {
            rate = percent * 0.01f;
            switch (direction) {
                case 1://右滑
                    mPaintColor.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                    canvas.drawCircle(leftCenterX + diameter * rate, centerY, radius, mPaintColor);
                    mPaintColor.setXfermode(null);//将画笔去除Xfermode
//                    canvas.drawCircle(centerX - radius + radius * rate, centerY, radius * rate, mPaintColor);
                    drawNextIcon(canvas);
                    break;
                case 2://左滑
                    mPaintColor.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                    canvas.drawCircle(rightCenterX - diameter * rate, centerY, radius, mPaintColor);
//                    canvas.drawCircle(leftCenterX + diameter * rate, centerY, radius * rate, mPaintColor);
                    mPaintColor.setXfermode(null);//将画笔去除Xfermode
                    if (!isPlay) {
                        drawPlayIcon(canvas);
                    } else {
                        drawPauseIcon(canvas);
                    }
                    break;
                case 0://原点
                    break;
            }
        }
    }

    private void drawNextIcon(Canvas canvas) {
        Path path = new Path();
        //画三角形
        path.moveTo(centerX - 30, centerY - 30);
        path.lineTo(centerX - 30, centerY + 30);
        path.lineTo(centerX + 15, centerY);
        path.close();
        //画竖线
        path.moveTo(centerX + 30, centerY - 30);
        path.lineTo(centerX + 30, centerY + 30);
        mPaintControl.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawPath(path, mPaintControl);
        mPaintControl.setXfermode(null);
    }

    private void drawPlayIcon(Canvas canvas) {
        Path path = new Path();
        path.moveTo(centerX - 20, centerY - 35);
        path.lineTo(centerX - 20, centerY + 35);
        path.lineTo(centerX + 35, centerY);
        path.close();
        mPaintControl.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawPath(path, mPaintControl);
        mPaintControl.setXfermode(null);
    }

    private void drawPauseIcon(Canvas canvas) {
        Path path = new Path();
        //画竖线
        path.moveTo(centerX - 20, centerY - 30);
        path.lineTo(centerX - 20, centerY + 30);
        path.moveTo(centerX + 20, centerY - 30);
        path.lineTo(centerX + 20, centerY + 30);
        mPaintControl.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawPath(path, mPaintControl);
        mPaintControl.setXfermode(null);
    }

    /**
     * 设置滑动百分比
     *
     * @param direction 方向
     * @param percent   百分比
     */
    public void setSlidePercent(int direction, int percent, boolean isPlay) {
        this.direction = direction;
        this.percent = percent;
        this.isPlay = isPlay;
        canvasColor = percent != 0;
        invalidate();
    }

}
