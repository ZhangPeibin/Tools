package com.milk.tools.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;


import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 2017/2/7.
 */

public class Util {


    public static String getDeviceID(Context paramContext) {
        if (paramContext == null) {
            return null;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return null;
            }
            String deviceId = telephonyManager.getDeviceId();
            if (deviceId == null) {
                return null;
            }
            deviceId = deviceId.trim();
            if ( deviceId.length() == 15 )
                return deviceId;
        } catch (SecurityException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
        return null;
    }



    public static Object getPrivateValue(Object o, String fieldName) {
        Class clz = o.getClass();
        try {
            Field field = null;
            field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(o);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(100);
        for (int i = 0; i < runningService.size(); i++) {
            String className = runningService.get(i).service.getClassName();
            if (className.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    public static String encodeData(ConcurrentLinkedQueue<String> queue) {
        if (queue == null) return "";
        StringBuilder stringBuilder = new StringBuilder();
        String json;
        while ((json = queue.poll()) != null) {
            stringBuilder.append(json).append("-*$*-");
        }
        return stringBuilder.toString();
    }

    public static List<String> decodeData(String queue) {
        if (queue == null) return Collections.EMPTY_LIST;
        String[] s = queue.split("\\-\\*\\$\\*\\-");
        List<String> strings = new ArrayList<>();
        Collections.addAll(strings, s);
        return strings;
    }


    public static void sleep(long times){
        try {
            Thread.sleep(times );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(){
        sleep(1000);
    }


    /**
     * 短暂的让cpu唤醒
     * @param context
     */
    public static void requestWakeLockAndRelease(Context context){
        if (((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            PowerManager.WakeLock newWakeLock = ((PowerManager)context.getSystemService("power")).newWakeLock(268435462, "bright");
            newWakeLock.acquire();
            newWakeLock.release();
            ((KeyguardManager) context.getSystemService("keyguard")).newKeyguardLock("unLock").disableKeyguard();
        }

    }
}
