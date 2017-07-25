package com.ghy.yueplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * 根据Visualizer传来的数据动态绘制波形效果，分别为：
 * 块状波形、柱状波形、曲线波形
 */
public class MyVisualizerView extends View {
    // bytes数组保存了波形抽样点的值
    private byte[] bytes;
    private float[] points;
    private Paint paint = new Paint();
    private Rect rect = new Rect();
    private byte type = 0;

    public MyVisualizerView(Context context) {
        super(context);
        bytes = null;
        // 设置画笔的属性
        paint.setStrokeWidth(1f);
        paint.setAntiAlias(true);//抗锯齿
        paint.setColor(Color.WHITE);//画笔颜色
        paint.setStyle(Paint.Style.FILL);
    }

    public void updateVisualizer(byte[] ftt) {
        bytes = ftt;
        // 通知该组件重绘自己。
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        // 当用户触碰该组件时，切换波形类型
        if (me.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        type++;
        if (type >= 3) {
            type = 0;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bytes == null) {
            return;
        }
        // 绘制白色背景
        canvas.drawColor(Color.parseColor("#444444"));
        // 使用rect对象记录该组件的宽度和高度
        rect.set(0, 0, getWidth(), getHeight());
        switch (type) {
            // -------绘制块状的波形图-------
            case 0:
                for (int i = 0; i < bytes.length - 1; i++) {
                    float left = getWidth() * i / (bytes.length - 1);
                    // 根据波形值计算该矩形的高度
                    float top = rect.height() - (byte) (bytes[i + 1] + 128)
                            * rect.height() / 128;
                    float right = left + 1;
                    float bottom = rect.height();
                    canvas.drawRect(left, top, right, bottom, paint);
                }
                break;
            // -------绘制柱状的波形图（每隔18个抽样点绘制一个矩形）-------
            case 1:
                for (int i = 0; i < bytes.length - 1; i += 18) {
                    float left = rect.width() * i / (bytes.length - 1);
                    // 根据波形值计算该矩形的高度
                    float top = rect.height() - (byte) (bytes[i + 1] + 128)
                            * rect.height() / 128;
                    float right = left + 6;
                    float bottom = rect.height();
                    canvas.drawRect(left, top, right, bottom, paint);
                }
                break;
            // -------绘制曲线波形图-------
            case 2:
                // 如果point数组还未初始化
                if (points == null || points.length < bytes.length * 4) {
                    points = new float[bytes.length * 4];
                }
                for (int i = 0; i < bytes.length - 1; i++) {
                    // 计算第i个点的x坐标
                    points[i * 4] = rect.width() * i / (bytes.length - 1);
                    // 根据bytes[i]的值（波形点的值）计算第i个点的y坐标
                    points[i * 4 + 1] = (rect.height() / 2)
                            + ((byte) (bytes[i] + 128)) * 128
                            / (rect.height() / 2);
                    // 计算第i+1个点的x坐标
                    points[i * 4 + 2] = rect.width() * (i + 1)
                            / (bytes.length - 1);
                    // 根据bytes[i+1]的值（波形点的值）计算第i+1个点的y坐标
                    points[i * 4 + 3] = (rect.height() / 2)
                            + ((byte) (bytes[i + 1] + 128)) * 128
                            / (rect.height() / 2);
                }
                // 绘制波形曲线
                canvas.drawLines(points, paint);
                break;
        }
    }
}
