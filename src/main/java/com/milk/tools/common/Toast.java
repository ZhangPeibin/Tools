package com.milk.tools.common;

import android.content.Context;

/**
 * Created by wiki on 15/11/22.
 */
public class Toast {

    /**
     * toast a short message for user
     * @param lToastString
     */
    public static final void toast(Context context,String lToastString){
        android.widget.Toast.makeText(context,
                lToastString, android.widget.Toast.LENGTH_LONG).show();
    }


    /**
     * toast a message for user with resId
     * @param resId
     */
    public static final void toast(Context context,int resId){
        android.widget.Toast.makeText(context,
                resId, android.widget.Toast.LENGTH_SHORT).show();
    }



}
