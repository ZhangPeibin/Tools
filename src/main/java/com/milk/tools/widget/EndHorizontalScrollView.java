package com.milk.tools.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

/**
 * Created by Administrator on 2017/3/7.
 */

public class EndHorizontalScrollView extends HorizontalScrollView {

    private OnScrollListener mOnScrollListener;

    private static final int PRE_DISTANCE = 100;

    private int mWidth;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public EndHorizontalScrollView (Context context) {
        this(context,null);
    }

    public EndHorizontalScrollView (Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EndHorizontalScrollView (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        mWidth = display.getWidth();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scrollX = getScrollX();
        float right = getChildAt(0).getMeasuredWidth();
        float maxScrollX = right - mWidth;
        if (Math.abs(scrollX) > maxScrollX -PRE_DISTANCE){
            if (mOnScrollListener!=null){
                this.mOnScrollListener.right();
            }
        }else if (Math.abs(scrollX) ==0){
            if (mOnScrollListener!=null){
                this.mOnScrollListener.left();
            }
        }
    }

    public interface OnScrollListener{
        void left ();
        void right ();
    }
}
