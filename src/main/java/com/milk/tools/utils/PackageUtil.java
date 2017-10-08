package com.milk.tools.utils;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by wiki on 16/2/28.
 */
public class PackageUtil {
    private static final String DEFAULT = "";


    public static String getPackageName(Context context){
         return context.getPackageName();
    }

    public static int getPackageCode(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String[] getPermissions(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            return permissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasInstalledPackage(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();
        //获取系统中安装的应用包的信息
        List<PackageInfo> listPackageInfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < listPackageInfo.size(); i++) {
            if(listPackageInfo.get(i).packageName.equalsIgnoreCase(packageName)){
                return true;
            }
        }
        return false;

    }

    public static String getAppVersionName(Context context) {
        String versionName = "1";
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null)
                versionName = packageInfo.versionName;
            return versionName;
        } catch (Exception e) {
            return versionName;
        }
    }

    public static int getAppVersionCode(Context context) {
        int versionName = 0;
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null)
                versionName = packageInfo.versionCode;
            return versionName;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getMetaData(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo != null) {
            String msg = appInfo.metaData.getString("company");
            return msg;
        }
        return DEFAULT;
    }

    public static boolean checkCurrentPackageOK(Context context) {
        String d = getCurrentPackageName(context);
        if ("null".equals(d) || d == null) {
            return true;
        }
        List arrayList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 65536)) {
            arrayList.add(resolveInfo.activityInfo.packageName);
        }
        return arrayList.size() > 0 ? arrayList.contains(d) : false;
    }

    /**
     * 得到当前应用的包名
     * @param context
     * @return
     */
    public static String getCurrentPackageName(Context context) {
        String packageName = null;
        String str2 = "null";
        if ( Build.VERSION.SDK_INT >= 21) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
            long currentTimeMillis = System.currentTimeMillis();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(0, currentTimeMillis - 1000000000, currentTimeMillis);
            if (queryUsageStats != null) {
                SortedMap treeMap = new TreeMap();
                for (UsageStats usageStats : queryUsageStats) {
                    treeMap.put(Long.valueOf(usageStats.getLastTimeUsed()), usageStats);
                }
                if (!treeMap.isEmpty()) {
                    packageName = ((UsageStats) treeMap.get(treeMap.lastKey())).getPackageName();
                }
            }else{
                packageName = str2;
            }
        } else {
            packageName = ((ActivityManager.RunningTaskInfo) ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1).get(0)).topActivity.getPackageName();
        }
        return packageName;
    }

    /**
     * 打开package指定的界面
     * @param context
     * @param packageName
     * @return
     */
    public static boolean openUIFromPackage(Context context,String packageName){
        try {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 判断当前的应用是否为微信应用
     * @param context
     * @return
     */
    public static boolean isWeChatAtCurrentPackage(Context context){
        return "com.tencent.mm".equals(getCurrentPackageName(context));
    }

    public static String getAppVersion(Context context, String packageName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(packageName, 0).versionName;
    }

    public static List<AppInfo> getPackages(Context context) {
        List<AppInfo> appInfos = new ArrayList<>();
        // 获取已经安装的所有应用, PackageInfo　系统类，包含应用信息
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                // AppInfo 自定义类，包含应用信息
                AppInfo appInfo = new AppInfo();
                appInfo.appName =
                        packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();//获取应用名称
                appInfo.packageName = packageInfo.packageName; //获取应用包名，可用于卸载和启动应用
                appInfo.versionName = packageInfo.versionName;//获取应用版本名
                appInfo.versionCode = packageInfo.versionCode+"";//获取应用版本号
                appInfos.add(appInfo);
            } else { // 系统应用

            }
        }
        return appInfos;
    }

    public static class AppInfo {
        public String appName;
        public String packageName;
        public String versionName;
        public String versionCode;
    }

}
