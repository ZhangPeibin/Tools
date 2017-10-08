package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/5/3.
 */

public class HvSrollListView extends ListView {

    /**
     * 手势
     */
    private GestureDetector mGesture;
    /**
     * 列头
     */
    public LinearLayout mListHead;
    /**
     * 偏移坐标
     */
    private int mOffset = 0;
    /**
     * 屏幕宽度
     */
    private int screenWidth;


    /**
     * 构造函数
     */
    public HvSrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGesture = new GestureDetector(context, mOnGesture);
    }

    /**
     * 分发触摸事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return mGesture.onTouchEvent(ev);
    }

    /**
     * 手势
     */
    private GestureDetector.OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
//            int scrollWidth = getWidth();//屏幕宽度
//            int fWidth = mListHead.getChildAt(0).getMeasuredWidth();
//            for (int i = 0, j = getChildCount(); i < j; i++) {
//                View child = ((ViewGroup) getChildAt(i)).getChildAt(1);
//                if(Math.abs(velocityX) > Math.abs(velocityY)) {
//                    if(velocityX < 0) {
//                        child.scrollTo(500, 0);
//                        mListHead.scrollTo(500, 0);
//                    }else {
//                        child.scrollTo(fWidth, 0);
//                        mListHead.scrollTo(fWidth, 0);
//                    }
//                }
//            }
            return false;
        }

        /** 滚动 */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            synchronized (HvSrollListView.this) {//synchronized (HVListView.this)锁定了一个对象当有多个线程调用该代码块，只能有当一个线程执行完了，下一个线程才能进来执行
                int moveX = (int) distanceX;//滑动的偏移量
                int curX = mListHead.getScrollX();//头部标题的偏移量
                int scrollWidth = getWidth();//屏幕宽度
                int dx = moveX;//偏移量
                //控制越界问题
                if (curX + moveX < 0) {
                    dx = 0;
                }
                if (curX + moveX + getScreenWidth() > scrollWidth) {
                    dx = scrollWidth - getScreenWidth() - curX;
                 }
                mOffset += dx;

                if(mOffset < 0) {
                    mOffset = 0;
                }

                //根据手势滚动Item视图
                    for (int i = 0, j = getChildCount(); i < j; i++) {
                        View child = ((ViewGroup) getChildAt(i)).getChildAt(1);
                        if (child.getScrollX() != mOffset && Math.abs(distanceX) > Math.abs(distanceY)) {
                            child.scrollTo(mOffset, 0);
                            mListHead.scrollTo(mOffset, 0);

                        }
                    }
                }
            requestLayout();
            return true;
        }
    };


    /**
     * 获取屏幕可见范围内最大屏幕--固定了第一列
     *
     * @return
     */
    public int getScreenWidth() {
        if (screenWidth == 0) {
            screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
            if (getChildAt(0) != null) {
                screenWidth -= ((ViewGroup) getChildAt(0)).getChildAt(0)
                        .getMeasuredWidth();
            } else if (mListHead != null) {
                //减去固定第一列
                screenWidth -= mListHead.getChildAt(0).getMeasuredWidth();
            }
        }
        return screenWidth;
    }

    /**
     * 获取列头偏移量
     */
    public int getHeadScrollX() {
        return mListHead.getScrollX();
    }
}
