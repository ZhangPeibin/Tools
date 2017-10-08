package com.milk.tools.utils;

import android.os.Build;

/**
 * Created by wiki on 15/10/31.
 */
public class VersionUtils {

    /**
     * @return SDK VERSION > LOLLIPOP
     */
    public static boolean M(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * @return SDK VERSION > LOLLIPOP
     */
    public static boolean Lollipop(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     *
     * @return SDK VERSION > KITKAT
     */
    public static boolean Kitkat(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;
    }


    /**
     *
     * @return SDK VERSION > JELLY_BEAN_MR2
     */
    public static boolean JellyBeanMr2(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2;
    }


    /**
     *
     * @return SDK VERSION > ICE_CREAM_SANDWICH
     */
    public static boolean IceCreamSandwich(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }


    /**
     *
     * @return SDK VERSION > HONEYCOMB
     */
    public static boolean HONEYCOMB(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB;
    }
}

