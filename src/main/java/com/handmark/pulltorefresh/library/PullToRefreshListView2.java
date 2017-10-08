/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.milk.tools.R;

import java.lang.reflect.Method;

public class PullToRefreshListView2 extends PullToRefreshAdapterViewBase<ListView> implements PullToRefreshBase.OnLastItemVisibleListener {
    public static final String TAG = "PullToRefreshListView22";

    private LoadingLayout mHeaderLoadingView;
    private LoadingLayout mFooterLoadingView;

    private FrameLayout mLvFooterLoadingFrame;

    private boolean mListViewExtrasEnabled;

    private View mFootView;

    private ProgressBar footViewProgress;

    private TextView footViewTextView;

    private RefreshViewLoadMore refreshViewLoadMore;

    private Context mContext;

    private boolean isLoading = false;

    public PullToRefreshListView2(Context context) {
        super(context);
        this.mContext = context;
    }

    public PullToRefreshListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public PullToRefreshListView2(Context context, Mode mode) {
        super(context, mode);
        this.mContext = context;
    }

    public PullToRefreshListView2(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        this.mContext = context;
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected void onRefreshing(final boolean doScroll) {
        if(isLoading) {
            return;
        }
        isLoading = true;

        /**
         * If we're not showing the Refreshing view, or the list is empty, the
         * the header/footer views won't show so we use the normal method.
         */
          ListAdapter adapter = mRefreshableView.getAdapter();
        if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
            super.onRefreshing(doScroll);
            return;
        }

        super.onRefreshing(false);

        final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
        final int selection, scrollToY;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                origLoadingView = getFooterLayout();
                listViewLoadingView = mFooterLoadingView;
                oppositeListViewLoadingView = mHeaderLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToY = getScrollY() - getFooterSize();
                break;
            case PULL_FROM_START:
            default:
                origLoadingView = getHeaderLayout();
                listViewLoadingView = mHeaderLoadingView;
                oppositeListViewLoadingView = mFooterLoadingView;
                selection = 0;
                scrollToY = getScrollY() + getHeaderSize();
                break;
        }

        // Hide our original Loading View
        origLoadingView.reset();
        origLoadingView.hideAllViews();

        // Make sure the opposite end is hidden too
        oppositeListViewLoadingView.setVisibility(GONE);

        // Show the ListView Loading View and set it to refresh.
        listViewLoadingView.setVisibility(VISIBLE);
        listViewLoadingView.refreshing();

        if (doScroll) {
            // We need to disable the automatic visibility changes for now
            disableLoadingLayoutVisibilityChanges();

            // We scroll slightly so that the ListView's header/footer is at the
            // same Y position as our normal header/footer
            setHeaderScroll(scrollToY);

            // Make sure the ListView is scrolled to show the loading
            // header/footer
            mRefreshableView.setSelection(selection);

            // Smooth scroll as normal
            smoothScrollTo(0);
        }

        //刷新的时候把footview的文字改变过来
        footViewProgress.setVisibility(VISIBLE);
        footViewTextView.setText("正在加载...");
    }

    @Override
    protected void onReset() {
        /**
         * If the extras are not enabled, just call up to super and return.
         */
        if (!mListViewExtrasEnabled) {
            super.onReset();
            return;
        }

        final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
        final int scrollToHeight, selection;
        final boolean scrollLvToEdge;

        switch (getCurrentMode()) {
            case MANUAL_REFRESH_ONLY:
            case PULL_FROM_END:
                originalLoadingLayout = getFooterLayout();
                listViewLoadingLayout = mFooterLoadingView;
                selection = mRefreshableView.getCount() - 1;
                scrollToHeight = getFooterSize();
                scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
                break;
            case PULL_FROM_START:
            default:
                originalLoadingLayout = getHeaderLayout();
                listViewLoadingLayout = mHeaderLoadingView;
                scrollToHeight = -getHeaderSize();
                selection = 0;
                scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
                break;
        }

        // If the ListView header loading layout is showing, then we need to
        // flip so that the original one is showing instead
        if (listViewLoadingLayout.getVisibility() == VISIBLE ) {

            // Set our Original View to Visible
            originalLoadingLayout.showInvisibleViews();

            // Hide the ListView Header/Footer
            listViewLoadingLayout.setVisibility(GONE);

            /**
             * Scroll so the View is at the same Y as the ListView
             * header/footer, but only scroll if: we've pulled to refresh, it's
             * positioned correctly
             */
            if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
                mRefreshableView.setSelection(selection);
                setHeaderScroll(scrollToHeight);
            }
        }

        // Finally, call up to super
        super.onReset();
    }

    @Override
    protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
        LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

        if (mListViewExtrasEnabled) {
            final Mode mode = getMode();

            if (includeStart && mode.showHeaderLoadingLayout()) {
                proxy.addLayout(mHeaderLoadingView);
            }
            if (includeEnd && mode.showFooterLoadingLayout()) {
                proxy.addLayout(mFooterLoadingView);
            }
        }

        return proxy;
    }

    protected ListView createListView(Context context, AttributeSet attrs) {
        footViewProgress = (ProgressBar) mFootView.findViewById(R.id.pull_to_load_footer_progressbar);
        footViewTextView = (TextView) mFootView.findViewById(R.id.pull_to_load_footer_hint_textview);

        final ListView lv;
        if (VERSION.SDK_INT <= VERSION_CODES.GINGERBREAD) {
            lv = new InternalListViewSDK9(context, attrs);
        } else {
            lv = new InternalListView(context, attrs);
        }
        return lv;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        mFootView = inflate(context, R.layout.pull_to_refresh_load_footer, null);
        ListView listView = createListView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            listView.setOverScrollMode(OVER_SCROLL_NEVER);
        }
        listView.setId(android.R.id.list);
        return listView;
    }

    @Override
    protected void handleStyledAttributes(TypedArray a) {
        super.handleStyledAttributes(a);

        mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

        if (mListViewExtrasEnabled) {
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

            // Create Loading Views ready for use later
            FrameLayout frame = new FrameLayout(getContext());
            mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
            mHeaderLoadingView.setVisibility(GONE);
            frame.addView(mHeaderLoadingView, lp);
            mRefreshableView.addHeaderView(frame, null, false);

            mLvFooterLoadingFrame = new FrameLayout(getContext());
            mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
            mFooterLoadingView.setVisibility(GONE);
            mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

            /**
             * If the value for Scrolling While Refreshing hasn't been
             * explicitly set via XML, enable Scrolling While Refreshing.
             */
            if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
                setScrollingWhileRefreshingEnabled(true);
            }
        }
    }


    @TargetApi(9)
    final class InternalListViewSDK9 extends InternalListView {

        public InternalListViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshListView2.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

            return returnValue;
        }
    }

    @Override
    public void onLastItemVisible() {
        if (refreshViewLoadMore != null) {
            if(!isLoading) {
                footViewProgress.setVisibility(VISIBLE);
                footViewTextView.setText("正在加载...");
                refreshViewLoadMore.loadmore();
                isLoading = true;
            }
        }
    }

    public void loadMoreFailed() {
        mFootView.setPadding(0, dip2px(mContext, 15), 0, dip2px(mContext, 15));
        footViewProgress.setVisibility(GONE);
        footViewTextView.setText("点击重新加载");
        mFootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mFootView.setOnClickListener(null);
                footViewProgress.setVisibility(VISIBLE);
                footViewTextView.setText("正在加载...");
                if (refreshViewLoadMore != null) {
                    refreshViewLoadMore.loadmore();
                }
            }
        });

        isLoading = false;
    }

    public void dataLoadComplet() {
        dataLoadComplet("数据加载完毕");
    }

    public void dataLoadComplet(String s) {
        if (!isDirectChildHeaderOrFooter(getRefreshableView(), mFootView)) {
            getRefreshableView().addFooterView(mFootView, null, false);
        }
        mFootView.setPadding(0, dip2px(mContext, 15), 0, dip2px(mContext, 15));
        footViewProgress.setVisibility(GONE);
        footViewTextView.setText(s);

        isLoading = false;
    }

    public void showFootView() {
        if (!isDirectChildHeaderOrFooter(getRefreshableView(), mFootView)) {
            mFootView.setPadding(0, dip2px(mContext, 15), 0, dip2px(mContext, 15));
            getRefreshableView().addFooterView(mFootView, null, false);
            footViewProgress.setVisibility(VISIBLE);
            footViewTextView.setText("正在加载...");
        }

        isLoading = false;
    }

    private boolean isDirectChildHeaderOrFooter(ListView list, View view) {
        try {
            Class<? extends ListView> clazz = list.getClass();
            Method method = getMethod(clazz, "isDirectChildHeaderOrFooter", View.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(list, view);
        } catch (Exception e) {
            int count = getRefreshableView().getFooterViewsCount();
            if (count <= 1) {
                return false;
            }
            return true;
        }
    }

    private Method getMethod(Class clazz, String methodName, final Class... classes) throws Exception {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName, classes);
                }
            }
        }
        return method;
    }

    public void removeRefreshFootView() {
        if (getRefreshableView().getFooterViewsCount() > 0) {
            getRefreshableView().removeFooterView(mFootView);
        }
    }

    public interface RefreshViewLoadMore {
        void loadmore ();
    }

    public void setRefreshViewLoadMore(RefreshViewLoadMore listViewLoadMore) {
        this.refreshViewLoadMore = listViewLoadMore;
    }

    protected class InternalListView extends ListView implements EmptyViewMethodAccessor {

        private boolean mAddedLvFooter = false;

        public InternalListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setOnLastItemVisibleListener(PullToRefreshListView2.this);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                super.dispatchDraw(canvas);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                return super.dispatchTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
                addFooterView(mLvFooterLoadingFrame, null, false);
                mAddedLvFooter = true;
            }
            super.setAdapter(adapter);
        }

        @Override
        public void setEmptyView(View emptyView) {
            PullToRefreshListView2.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }
    }

    public void onRefreshComplete(long delayMillis) {
        Handler handler = getHandler();
        if (handler == null) {
            handler = new Handler(android.os.Looper.getMainLooper());
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PullToRefreshListView2.super.onRefreshComplete();
            }
        }, delayMillis);

        isLoading = false;
    }

    public void setRefreshing(long delayMillis) {
        Handler handler = getHandler();
        if (handler == null) {
            handler = new Handler(android.os.Looper.getMainLooper());
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
            }
        }, delayMillis);
    }

}
