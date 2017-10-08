package com.milk.tools.utils;

/**
 * Created by peibin on 17-5-27.
 */

public class Gson {

    public static com.google.gson.Gson gson = null;

    public static com.google.gson.Gson getGson(){
        if ( gson == null ){
            synchronized (Gson.class){
                if ( gson == null ){
                    gson = new com.google.gson.Gson();
                }
            }
        }

        return gson;
    }
}
