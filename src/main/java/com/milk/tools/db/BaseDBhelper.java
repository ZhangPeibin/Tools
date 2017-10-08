package com.milk.tools.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.milk.tools.utils.MD5;


/**
 * Created by Administrator on 2017/2/13.
 */

public class BaseDBhelper extends SQLiteOpenHelper {


    public static BaseDBhelper getInstance(Context context, String uin){
        return  new BaseDBhelper(new DatabaseContext(context),uin);
    }

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "_EnMicroMsg.db";

    public BaseDBhelper (Context context, String uin){
        super(context, MD5.getMd5(uin)+DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
