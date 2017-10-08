package com.milk.tools.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.milk.tools.R;
import com.milk.tools.widget.PinnedHeadAdapter;

/**
 * Created by wiki on 16/3/1.
 */
public class PinnedHeadListView extends ListView implements AbsListView.OnScrollListener {


    /* 是否需要固定headView */
    private boolean mShouldPin = true;

    /* 固定到顶端的View */
    private View mPinnedHeadView = null;

    /* 所需要固定的headView */
    private PinnedHeadAdapter mPinnedHeadAdapter;

    /* 供外部调用*/
    private OnScrollListener mOnScrollListener = null;

    private int mPinnedHeadViewWidth = 0;
    private int mPinnedHeadViewHeight = 0;

    private int mHeaderOffset = 0;
    private int mHeightMode;
    private int mWidthMode;


    public PinnedHeadListView(Context context) {
        this(context, null);
        super.setOnScrollListener(this);
    }

    public PinnedHeadListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        super.setOnScrollListener(this);
    }

    public PinnedHeadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnScrollListener(this);
    }

    public void setAdapter(PinnedHeadAdapter pinnedHeadAdapter) {
        if (pinnedHeadAdapter != null) {
            this.mPinnedHeadAdapter = pinnedHeadAdapter;
            this.mPinnedHeadView = pinnedHeadAdapter.getHeadView();
            ensureHeadView();
//            if (this.mPinnedHeadView != null)
//                //默认是headView未隐藏
//                this.mPinnedHeadView.setVisibility(GONE);
        }

        super.setAdapter(pinnedHeadAdapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null)
            mOnScrollListener.onScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (!mShouldPin || mPinnedHeadAdapter == null || (mPinnedHeadView != null && mPinnedHeadAdapter.getSectionItemCount() == 0)) {
            if (mPinnedHeadView != null) {
                mPinnedHeadView.setVisibility(GONE);
            }
            return;
        }

        ensureHeadView();

        boolean isFistVisibleItemAsSectionView = mPinnedHeadAdapter.isSectionForPosition(firstVisibleItem);
        boolean isNextVisibleItemAsSectionView = mPinnedHeadAdapter.isSectionForPosition(firstVisibleItem + 1);

        //如果当前屏幕的第一个可见的item为sectionView
        if (isFistVisibleItemAsSectionView) {
            mPinnedHeadView.setVisibility(VISIBLE);
            mHeaderOffset = 0;
            ensureHeadView();
        } else if (isNextVisibleItemAsSectionView) {
            mPinnedHeadView.setVisibility(VISIBLE);
            View nextVisibleItem = getChildAt(0);//此中情况,第一个view不是sectionView
            int bottom = nextVisibleItem.getBottom();
            //代表第二view也就是sectionView开始覆盖header
            if (bottom <= mPinnedHeadViewHeight) {
                //第一个item向上滑动的距离
                mHeaderOffset = mPinnedHeadViewHeight - bottom;
                if (bottom <= 0) {
                    mHeaderOffset = 0;
                    ensureHeadView();
                }
            }
        } else {
            mPinnedHeadView.setVisibility(GONE);
        }
        mPinnedHeadAdapter.updateHeadView(mPinnedHeadView, firstVisibleItem);
        invalidate();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!mShouldPin || mPinnedHeadView == null || mPinnedHeadAdapter == null) {
            return;
        }

        int saveCount = canvas.save();

        canvas.translate(0, -mHeaderOffset);
        canvas.clipRect(0, 0, getWidth(), mPinnedHeadViewHeight);
        mPinnedHeadView.draw(canvas);

        canvas.restoreToCount(saveCount);
    }

    /**
     * 设置headView的位置
     */
    private void ensureHeadView() {
        if (mPinnedHeadView != null && mPinnedHeadView.isLayoutRequested()) {
            int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), mWidthMode);

            int heightSpec;
            ViewGroup.LayoutParams layoutParams = mPinnedHeadView.getLayoutParams();
            if (layoutParams != null && layoutParams.height > 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            } else {
                    heightSpec = MeasureSpec.makeMeasureSpec(40, MeasureSpec.EXACTLY);
            }
            mPinnedHeadView.measure(widthSpec, heightSpec);
            mPinnedHeadViewWidth = mPinnedHeadView.getMeasuredWidth();
            mPinnedHeadViewHeight = mPinnedHeadView.getMeasuredHeight();
            mPinnedHeadView.layout(0, 0, mPinnedHeadViewWidth, mPinnedHeadViewHeight);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec);

    }

}
