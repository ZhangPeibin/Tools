package com.milk.tools.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.milk.tools.common.Toast;

/**
 * Created by wiki on 15/11/22.
 */
public class NetUtil {

    /**
     * check the net is available
     * @return
     */
    public static boolean isAvailable(Context context){
        ConnectivityManager lConnectivityManager  = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo lNetworkInfo = lConnectivityManager.getActiveNetworkInfo();

        return lNetworkInfo !=null && lNetworkInfo.isAvailable();
    }


    /**
     * check the net is connected or connecting
     * @return
     */
    public static boolean isConnected(Context context){
        ConnectivityManager lConnectivityManager  = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo lNetworkInfo = lConnectivityManager.getActiveNetworkInfo();

        return lNetworkInfo !=null && lNetworkInfo.isConnectedOrConnecting();
    }



    /*是否有网络*/
    public static boolean isNetEnable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /*是否有WiFi*/
    public static boolean isNetWifiEnable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }
        return false;
    }

    public static void showNetTypeToast(Context context,boolean wifiShow){
        if(isNetWifiEnable(context)){
            if(wifiShow){
                Toast.toast(context,"当前网络为wifi");
            }
        }else{
            Toast.toast(context,"当前网络为2G/3G/4G");
        }
    }

    /*手机移动网络*/
    public static boolean isNetMobileEnable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }
}
