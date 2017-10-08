package com.milk.tools.widget;

import android.view.View;
import android.widget.BaseAdapter;

/**
 * Created by wiki on 16/3/1.
 */
public abstract class PinnedHeadAdapter extends BaseAdapter{


    /**
     * 子类需要实现该方法以提供headView
     * @return
     */
    public abstract View getHeadView();


    /**
     * 子类实现该方法以修改headView
     * @param headView
     */
    public abstract void updateHeadView(View headView,int position);


    /**
     * 获取指定的position的type
     * @param position
     * @return
     */
    public abstract boolean isSectionForPosition(int position);


    /**
     * 获取item的count
     * @return
     */
    public abstract int getItemCount();

    /**
     * 获取section的count
     * @return
     */
    public abstract int getSectionItemCount();

}
