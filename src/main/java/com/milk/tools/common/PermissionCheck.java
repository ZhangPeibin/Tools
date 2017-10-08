package com.milk.tools.common;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.milk.tools.utils.PackageUtil;
import com.milk.tools.utils.VersionUtils;

/**
 * Created by Administrator on 2016/3/12.
 */
public class PermissionCheck {


    public static final int REQUEST_ALL_PERMISSION_CODE = 0X11;

    public static boolean check(AppCompatActivity appCompatActivity, String permission){
        return ContextCompat.checkSelfPermission(appCompatActivity,permission) != PackageManager.PERMISSION_GRANTED &&
                VersionUtils.M();
    }

    public static void request(AppCompatActivity appCompatActivity, String permission, int permissionRequestCode){
        ActivityCompat.requestPermissions(appCompatActivity,
                new String[]{permission},
                permissionRequestCode);
    };

    public static void request(AppCompatActivity appCompatActivity, String[] permission, int permissionRequestCode){
        ActivityCompat.requestPermissions(appCompatActivity,
                permission,
                permissionRequestCode);
    };

    public static void requestAllPermission(AppCompatActivity appCompatActivity){
        String[] permissions = PackageUtil.getPermissions(appCompatActivity.getApplicationContext());
        if(permissions!=null && permissions.length!=0)
            request(appCompatActivity,permissions,REQUEST_ALL_PERMISSION_CODE);
    }


    public static boolean checkHasUsageStatsPermission(Context context){
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow("android:get_usage_stats",android.os.Process.myUid(),context.getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED){
            return true;
        }
        return false;
    }
}
