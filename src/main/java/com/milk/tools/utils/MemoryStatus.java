package com.milk.tools.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MemoryStatus {
    private static final String TAG = "MemoryStatus";
    static final int ERROR = -1;

    public static String getDownloadDir (Context app, String name) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if ( sdCardExist ) {//如果SD卡存在，则获取跟目录
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString() + "/" + name + "/cache/download";
        }
        return app.getExternalCacheDir() + name + "/cache/download";
    }

    public static String getMntPath (String obj) {
        if ( obj == null || obj.trim().length() == 0 ) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
//			if(obj.endsWith(PreferenceSetting.MEDIAFILE_SUBDIR)){
//				obj = obj.substring(0,obj.lastIndexOf(PreferenceSetting.MEDIAFILE_SUBDIR));
//			}
            return obj;
        }

    }

    static public boolean externalMemoryAvailable () {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    static public String getAvailableInternalMemorySizeText () {
        return formatSize(getAvailableInternalMemorySize());
    }

    static public long getAvailableInternalMemorySize () {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    static public String getTotalInternalMemorySizeText () {
        return formatSize(getTotalInternalMemorySize());
    }

    static public long getTotalInternalMemorySize () {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }


    static public String getAvailableExternalMemorySizeText () {
        return formatSize(getAvailableExternalMemorySize());
    }

    static public long getAvailableExternalMemorySize () {
        try {
            if ( externalMemoryAvailable() ) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                return availableBlocks * blockSize;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    static public String getAvailableExternalMemorySizeText (String mntPath) {
        return formatSize(getAvailableExternalMemorySize(mntPath));
    }

    static public long getAvailableExternalMemorySize (String mntPath) {
        try {
            StatFs stat = new StatFs(getMntPath(mntPath));
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return ERROR;
        }
    }

    static public String getAlreadyUsedExternalMemorySizeText () {
        return formatSize(getAlreadyUsedExternalMemorySize());
    }

    static public long getAlreadyUsedExternalMemorySize () {
        return getTotalExternalMemorySize() - getAvailableExternalMemorySize();
    }

    static public String getAlreadyUsedExternalMemorySizeText (String mtnPath) {
        return formatSize(getAlreadyUsedExternalMemorySize(mtnPath));
    }

    static public long getAlreadyUsedExternalMemorySize (String mtnPath) {
        return getTotalExternalMemorySize(mtnPath) - getAvailableExternalMemorySize(mtnPath);
    }

    static public String getTotalExternalMemorySizeText () {
        return formatSize(getTotalExternalMemorySize());
    }

    static public long getTotalExternalMemorySize () {
        if ( externalMemoryAvailable() ) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    static public String getTotalExternalMemorySizeText (String mntPath) {
        return formatSize(getTotalExternalMemorySize(mntPath));
    }

    static public long getTotalExternalMemorySize (String mntPath) {
        try {
            StatFs stat = new StatFs(getMntPath(mntPath));
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return ERROR;
        }
    }

    static public String formatSize (long size) {
        String suffix = "B";
        boolean decimal = false;
        if ( size >= 1024 ) {
            suffix = "KB";
            size /= 1024;
            if ( size >= 1024 ) {
                suffix = "MB";
                size /= 1024;
                if ( size >= 1024 ) {
                    suffix = "GB";
                    decimal = true;
//					size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        String result = "0";
        if ( decimal ) {
            int commaOffset = resultBuffer.length() - 3;
            resultBuffer.insert(commaOffset, '.');
            result = resultBuffer.substring(0, commaOffset + 2);
        } else {
            result = resultBuffer.toString();
        }
        result += suffix;
        return result;
    }


    public static void a (Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(100);
        for ( ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfos ){
            String name = runningServiceInfo.process;
            if ( name!=null ){
                if ( name.startsWith("com.ss.android.article") ||
                        name.startsWith("com.sina")){
                    activityManager.killBackgroundProcesses(runningServiceInfo.service.getPackageName());
                }
            }
        }
    }


    public static String getTotalRam(Context context){//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(firstLine != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "GB";//返回1GB/2GB/3GB/4GB
    }
}
