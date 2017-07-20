package com.ghy.yueplayer.main;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by GHY on 2017/7/18.
 * Desc:拖拽播放布局
 */

public class VDHLayout extends RelativeLayout {

    private ViewDragHelper mDragHelper;

    private View mAutoBackView;
    private Point mAutoBackOriginPos = new Point();

    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_ORIGIN = 0;

    private TouchDirectionListener mTouchDirectionListener;
    private TouchReleasedListener mTouchReleasedListener;

    public interface TouchDirectionListener {
        /**
         * @param direction 移动方向
         * @param percent   移动百分比
         */
        void touchDirection(int direction, int percent);
    }

    public interface TouchReleasedListener {
        void touchReleased();
    }

    public void setTouchDirectionListener(TouchDirectionListener touchDirectionListener) {
        this.mTouchDirectionListener = touchDirectionListener;
    }

    public void setTouchReleasedListener(TouchReleasedListener touchReleasedListener) {
        this.mTouchReleasedListener = touchReleasedListener;
    }

    public VDHLayout(Context context) {
        this(context, null);
    }

    public VDHLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VDHLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

            /**
             * 如何返回true则表示可以捕获该view
             */
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            /**
             *
             * 可以在该方法中对child移动的边界进行控制，left , top 分别为即将移动到的位置，
             * 比如横向的情况下，我希望只在ViewGroup的内部移动，
             * 即：最小>=paddingleft，最大<=ViewGroup.getWidth()-paddingright-child.getWidth。
             * 若直接返回left则会拖到边界外(针对于所有的View)。
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - mAutoBackView.getWidth() - leftBound;
                return Math.min(Math.max(left, leftBound), rightBound);
//                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                int topBound = getPaddingTop();
                int bottomBound = getHeight() - mAutoBackView.getHeight() - topBound;
                return Math.min(Math.max(top, topBound), bottomBound);
//                return top;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                Log.i("viewPosition", "left-->>" + left + "--top-->>" + top + "--dx-->>" + dx + "--dy-->>" + dy);
                int changeX = left - mAutoBackOriginPos.x;
                if (changeX > 0) {
                    //向右移动
                    Log.i("viewPosition", "向右移动-->>" + changeX);
                    if (mTouchDirectionListener != null)
                        mTouchDirectionListener.touchDirection(DIRECTION_RIGHT, changeX * 100 / mAutoBackOriginPos.x);
                } else if (changeX < 0) {
                    Log.i("viewPosition", "向左移动-->>" + -changeX);
                    if (mTouchDirectionListener != null)
                        mTouchDirectionListener.touchDirection(DIRECTION_LEFT, -changeX * 100 / mAutoBackOriginPos.x);
                } else if (changeX == 0) {
                    Log.i("viewPosition", "回到原点-->>" + changeX);
                    if (mTouchDirectionListener != null)
                        mTouchDirectionListener.touchDirection(DIRECTION_ORIGIN, changeX * 100 / mAutoBackOriginPos.x);
                }
            }

            /**
             * 手指释放时回调:
             *
             * 如果是mAutoBackView则调用settleCapturedViewAt回到初始的位置。
             * 大家可以看到紧随其后的代码是invalidate();
             * 因为其内部使用的是mScroller.startScroll，所以别忘了需要invalidate()以及结合computeScroll方法一起。
             */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
//                super.onViewReleased(releasedChild, xvel, yvel);
                Log.i("viewPosition", "onViewReleased手指释放-->>");
                if (mTouchReleasedListener != null) mTouchReleasedListener.touchReleased();
                if (releasedChild == mAutoBackView) {
                    mDragHelper.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
//        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
        Log.i("viewPosition", "mAutoBackOriginPos.x-->>" + mAutoBackOriginPos.x + "--mAutoBackOriginPos.y-->>" + mAutoBackOriginPos.y);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAutoBackView = getChildAt(0);
    }
}
