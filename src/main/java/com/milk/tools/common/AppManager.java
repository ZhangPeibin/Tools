package com.milk.tools.common;

import android.content.Context;
import android.content.Intent;

/**
 * Created by wiki on 15/11/1.
 */
public class AppManager {

    public static void systemExit(){
        System.exit(0);
    }

    public static void killProcessExit(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    public static void homeExit(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
