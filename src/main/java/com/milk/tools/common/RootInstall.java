package com.milk.tools.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.milk.tools.function.su.Su;
import com.milk.tools.utils.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.acl.LastOwnerException;
import java.util.logging.Handler;

/**
 * Created by Administrator on 2017/3/15.
 */

public class RootInstall {


    /**
     * 安装软件,并且启动软件
     * @param apkPath  app的本地目录
     * @param lunchInfo package/package.class"
     */
    public static void installWithStart(String apkPath,String lunchInfo){
        boolean hasInstall = install(apkPath);
        if (hasInstall){
            launch(lunchInfo);
        }
    }

    /**
     * 安装软件,并且启动软件
     * @param apkPath  app的本地目录
     * @param lunchInfo package/package.class"
     */
    public static void installAndLaunchAppBeforeReboot(String apkPath,final String lunchInfo){
        boolean hasInstall = install(apkPath);
        if (hasInstall){
            Logger.d("start to lunchInfo[%s]",lunchInfo);
            launch(lunchInfo);
            reboot();
        }
    }

    /**
     * 安装软件,并且启动软件
     * @param apkPath  app的本地目录
     * @param lunchInfo package/package.class"
     */
    public static void installAndLaunchAppBeforeReboot(String apkPath,String lunchInfo,AfterLaunchHook afterLaunchHook){
        boolean hasInstall = install(apkPath);
        if (hasInstall){
            boolean launch = launch(lunchInfo);
            if(afterLaunchHook!=null) afterLaunchHook.afterLaunch(launch);
            reboot();
        }
    }

    public  interface AfterLaunchHook{
        void afterLaunch(boolean lunchSuccess);
    }


    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     * @param apkPath
     *          要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public static boolean install(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Logger.e(msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Logger.e(e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Logger.e(e.getMessage(), e);
            }
        }
        return result;
    }

    public static boolean reboot(){
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        Process process = null;
        try {
            // 申请su权限
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "reboot";
            String auto = "";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.close();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        } catch (Exception e) {
            Logger.e(e.getMessage(), e);
            return false;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.e(e.getMessage(), e);
            }
            if (process != null) {
                Logger.e("release reboot success" + process.exitValue());
                process.destroy();
            }
        }
        return true;
    }



    public static boolean launch(String activityName){
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        Process process = null;
        try {
            // 申请su权限
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "am start -n " +"\""+activityName +"\""+"  -a android.intent.action.MAIN -c android.intent.category.LAUNCHER\n";
            String auto = "";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            dataOutputStream.close();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        } catch (Exception e) {
            Logger.e(e.getMessage(), e);
            return false;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Logger.e(e.getMessage(), e);
            }
            if (process != null) {
                Logger.e("release launch success" + process.exitValue());
                process.destroy();
            }
        }
        return true;
    }


    public static boolean checkApkExist (Context context, String packageName) {
        if ( TextUtils.isEmpty(packageName) ) return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
