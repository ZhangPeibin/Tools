package com.milk.tools.widget.shourt;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.milk.tools.R;
import com.milk.tools.function.glide.GlideFetcher;
import com.milk.tools.utils.StringUtil;
import com.milk.tools.utils.WindowUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/6/24.
 */
public class ShortCutView extends ViewGroup {

    //上下文对象,用来创建View等元素
    private Context mContext;

    //快捷方式icon的大小
    private int mShortCutIconSize = 96;

    //快捷方式文本的字体大小
    private final static int SHORT_CUT_ICON_SIZE = 14;

    //快捷方式文本的字体颜色
    private int mShortCutTextColor = -1;

    //快捷方式文本合Icon之间的间隔
    private int mMarginBetweenIconAndText = 12;

    //快捷方式的上下间隔
    private int mMarginForTopAndBottom = 12;

    //每行所允许的单元格数量
    private final static int ITEM_COUNT = 4;

    //每个单元格的宽度
    private int mSingleItemWidth = -1;

    //定义图片控件集合
    private List<ImageView> mShortCutIconViews = new ArrayList<>();

    //定义文本控件集合
    private List<TextView> mShortCutTextViews = new ArrayList<>();

    //快捷方式数据源
    private List<HomeQuickItem> mHomeQuickItems = null;

    //item的点击事件
    private OnClickListener mOnClickListener;

    private MyClick mMyClick = null;

    private CustomShortCutParam mTextLayoutParam;
    private CustomShortCutParam mLayoutParam;


    public ShortCutView (Context context) {
        super(context);

        mContext = context;
        init();
    }

    /**
     * 初始化方法
     *
     * @param context        上下文对象,负责View等创建
     * @param homeQuickItems 数据源,快捷方式数据源
     * @param onclick        点击事件
     */
    public ShortCutView (Context context, List<HomeQuickItem> homeQuickItems, OnClickListener onclick) {
        super(context);
        mOnClickListener = onclick;
        mContext = context;
        init();
        setShortCutSource(homeQuickItems, true);
    }

    public ShortCutView (Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        init();
    }

    public ShortCutView (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * 初始化参数方法
     */
    private void init() {
        //默认为48dp
        mShortCutIconSize = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);

        //字体颜色默认为#686868
        mShortCutTextColor = getResources().getColor(R.color.home_quick_button_color);

        //文本与Icon间隔默认为8dp
        mMarginBetweenIconAndText = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin)/2;

        //默认为16dp
        mMarginForTopAndBottom = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin) * 2/3;

        //屏幕宽度
        int screenWidth = WindowUtils.getScreenWidth(mContext);

        //当个快捷方式的宽度
        mSingleItemWidth = screenWidth / ITEM_COUNT;

        setBackgroundColor(getResources().getColor(R.color.color_app_bg));

        mTextLayoutParam = new CustomShortCutParam(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTextLayoutParam.topMargin = mMarginBetweenIconAndText;

        mLayoutParam = new CustomShortCutParam(mShortCutIconSize, mShortCutIconSize);
        mLayoutParam.topMargin = mMarginForTopAndBottom;
    }


    /**
     * 当我们向View绑定数据源的时候,检查是否需要创建子View
     * 如果已经存在子View或者说数据源数目发生了变化,那么就会重新创建View,
     * 并更新数据到对应的View控件上
     * 如果不存在子View,则直接创建子View,并绑定数据到界面上
     *
     * @param items         数据源
     * @param needDrawAgain 是否需要重绘
     */
    public void setShortCutSource(List<HomeQuickItem> items, boolean needDrawAgain) {
        if (items == null || items.size() == 0) {
            return;
        }
        if (mMyClick == null) mMyClick = new MyClick();
        boolean isEqualSize = !(mHomeQuickItems == null) && (mHomeQuickItems.size() == items.size());
        if (isEqualSize) {//只要新的数据与已经显示的数据size相同,我们就只需要刷新
            for (int i = 0; i < mShortCutIconViews.size(); i++) {
                updateUI(items.get(i), mShortCutIconViews.get(i), mShortCutTextViews.get(i));
            }
            return;
        }

        if (mHomeQuickItems == null) mHomeQuickItems = items;
        else{mHomeQuickItems.clear(); mHomeQuickItems.addAll(items);}
        removeAllViews();
        mShortCutIconViews.clear();
        mShortCutTextViews.clear();
        final int size = items.size();
        HomeQuickItem homeQuickItem = null;
        for (int i = 0; i < size; i++) {
            homeQuickItem = items.get(i);
            if (homeQuickItem != null) {
                final ImageView imageView = createImageView(mContext);
                final TextView textView = createContentView(mContext);
                textView.setTag(textView.getId(), imageView);
                textView.setOnClickListener(mMyClick);
                addView(imageView);
                addView(textView);
                updateUI(homeQuickItem, imageView, textView);
                mShortCutIconViews.add(imageView);
                mShortCutTextViews.add(textView);
            }
        }

        if (needDrawAgain) {
            invalidate();
            requestLayout();
        }
    }

    private class MyClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag(0);
            if (tag != null && tag instanceof ImageView) {
                ImageView imageView = (ImageView) tag;
                imageView.performClick();
            }
        }
    }

    /**
     * 更新UI界面
     *
     * @param homeQuickItem 数据，需要绑定到UI界面上面的数据
     * @param i             icon控件
     * @param t             文本控件
     */
    private void updateUI(HomeQuickItem homeQuickItem, final ImageView i, final TextView t) {
        Object o = i.getTag(i.getId()+1);
        if (o!=null) o = null;
        i.setTag(i.getId()*2,homeQuickItem);
        String tString = t.getText().toString();//当前文本控件的文本
        final boolean isTStringEmpty = StringUtil.isEmpty(tString);
        final boolean isTStringEqualNewString = tString.equals(homeQuickItem.getName());
        //只有当前文本控件的文本为空或者与新的文本不同的时候需要更新该文本控件
        final boolean shouldDrawText = isTStringEmpty || !isTStringEqualNewString;
        if (shouldDrawText)
            t.setText(homeQuickItem.getName());

        final String imageUrl = homeQuickItem.getAndroid();
        String oldUrl = (String) t.getTag();
        //只有当icon控件未成功加载图片或者url发生的改变后才会对icon控件进行更新
        if ( imageUrl.equals(oldUrl)) {
            return;
        }
        GlideFetcher.get(mContext).fetchNoCache(imageUrl, i,-1, new RequestListener() {
            @Override
            public boolean onException (Exception e, Object model, Target target, boolean isFirstResource) {
                t.setTag(null);
                return false;
            }

            @Override
            public boolean onResourceReady (Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        });
        t.setTag(imageUrl);
//        GlideFetcher.get(mContext).fetchBitmapTarget(imageUrl, new GlideFetcher.FetchBitmap() {
//            @Override
//            public void fetch(Bitmap bitmap) {
//                if(bitmap!=null){
//                    StateListDrawable stateListDrawable = BitmapUtil.createPressedListDrawable(bitmap);
//                    i.setBackgroundDrawable(stateListDrawable);
//                    //图片加载完成后,将url设置到对应的文本控件
//                    //为下一次的图片是否加载做必要性检查要素
//                    t.setTag(imageUrl);
//                }
//            }
//        });
    }

    /**
     * 创建左侧图片控件,距离上侧16dp,大小为348dp
     *
     * @param context 用来生产控件的上下文对象
     * @return ImageView, 左侧图片控件
     */
    private ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(mLayoutParam);
        if (mOnClickListener != null)
            imageView.setOnClickListener(mOnClickListener);
        return imageView;
    }

    /**
     * 创建文本控件
     *
     * @param context 用来生产控件的上下文对象
     * @return TextView 文本控件
     */
    public TextView createContentView(Context context) {
        TextView textView = new TextView(context);
//        mTextLayoutParam = new CustomShortCutParam(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        mTextLayoutParam.topMargin = mMarginBetweenIconAndText;
        textView.setLayoutParams(mTextLayoutParam);
        textView.setMaxLines(1);
        textView.setTextColor(mShortCutTextColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SHORT_CUT_ICON_SIZE);
        return textView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }

        //所有的行数
        final int lines = (((childCount / 2) % ITEM_COUNT) == 0) ? (childCount / 2) / ITEM_COUNT : (childCount / 2) / ITEM_COUNT + 1;

        //高度,每一行必然有上间距,但只有最后一行才有下间距,并且含有文本icon的间距
        int height = mMarginForTopAndBottom * (lines + 1) + mMarginBetweenIconAndText * lines;

        //测量所有的图片控件
        for (int i = 0; i < mShortCutIconViews.size(); i++) {
            View view = mShortCutIconViews.get(i);
            if (view != null) {
                measureChildWithMargins(view, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
        }

        //更新所有高度
        height += mShortCutIconViews.get(0).getMeasuredHeight() * lines;

        //测量所有的文本控件
        for (int i = 0; i < mShortCutTextViews.size(); i++) {
            View view = mShortCutTextViews.get(i);
            if (view != null) {
                measureChildWithMargins(view, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
        }

        //计算出父View的实际高度
        height += mShortCutTextViews.get(0).getMeasuredHeight() * lines;

        //设置父View的高度与宽度
        setMeasuredDimension(resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }

        //单元格内,元素必须居中,那么算出居中后该元素离单元格左右的距离
        final int iconLeftRightWidth = (mSingleItemWidth - getChildAt(0).getMeasuredWidth()) / 2;

        final int iconHeight = getChildAt(0).getMeasuredHeight();

        //除最后一个item外,其他的item的高度
        final int itemHeight = mMarginForTopAndBottom + //icon距上段距离
                iconHeight +//icon的高度
                mMarginBetweenIconAndText +//icon到文本的距离
                getChildAt(1).getMeasuredHeight();//文本的高度


        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            CustomShortCutParam customShortCutParam = (CustomShortCutParam) view.getLayoutParams();

            int result = i % 2;//判断是icon控件还是text控件,0为icon控件,1为text控件

            int currentLine = i / (ITEM_COUNT * 2);//当前的行数
            int index = i % (ITEM_COUNT * 2);//每行的view的索引,范围为0-7
            if (currentLine != 0) {//第一行
                index = i % (ITEM_COUNT * 2 * currentLine);
            }
            int n = 2 * (index / 2) + 1;//用来计算水平的距离的系数
            int currentLineAllViewCount = index / 2 + 1;
            if (result == 0) {
                //对图片控件进行布局
                view.layout(n * iconLeftRightWidth + ((currentLineAllViewCount - 1) * view.getMeasuredWidth()),
                        customShortCutParam.topMargin + currentLine * itemHeight,
                        n * iconLeftRightWidth + currentLineAllViewCount * view.getMeasuredWidth(),
                        customShortCutParam.topMargin + view.getMeasuredHeight() + currentLine * itemHeight);
            } else {
                //对文本控件进行布局
                view.layout(getTextLeftRightWidth(view) + (currentLineAllViewCount - 1) * mSingleItemWidth,
                        //文字距离icon的间隔+行高+icon的高度+icon顶部的距离
                        customShortCutParam.topMargin + currentLine * itemHeight + iconHeight + mMarginForTopAndBottom,
                        getTextLeftRightWidth(view) + (currentLineAllViewCount - 1) * mSingleItemWidth + view.getMeasuredWidth(),
                        customShortCutParam.topMargin + currentLine * itemHeight + iconHeight + mMarginForTopAndBottom + view.getMeasuredHeight());
            }
        }
    }


    /**
     * 得到当前View在单元格中与左右相隔的距离
     *
     * @param view 需要测量的View
     * @return View在单元格中与左右相隔的距离
     */
    private int getTextLeftRightWidth(View view) {
        if (view == null) {
            return (mSingleItemWidth - getChildAt(1).getMeasuredWidth()) / 2;
        }
        return (mSingleItemWidth - view.getMeasuredWidth()) / 2;
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new CustomShortCutParam(CustomShortCutParam.WRAP_CONTENT, CustomShortCutParam.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CustomShortCutParam(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new CustomShortCutParam(p);
    }

    //实现自己的LayoutParams,并覆盖所有于之相关的方法
    //那么我们在进行测量以及布局元素是可操作Margin属性
    public class CustomShortCutParam extends MarginLayoutParams {

        public CustomShortCutParam(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public CustomShortCutParam(int width, int height) {
            super(width, height);
        }

        public CustomShortCutParam(MarginLayoutParams source) {
            super(source);
        }

        public CustomShortCutParam(LayoutParams source) {
            super(source);
        }
    }
}
