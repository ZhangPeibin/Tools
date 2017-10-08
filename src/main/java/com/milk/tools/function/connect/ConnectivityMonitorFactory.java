package com.milk.tools.function.connect;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 *  network monitor
 * Created by Administrator on 2016/12/9.
 */
public class ConnectivityMonitorFactory {
    public ConnectivityMonitor build(Context context,ConnectivityMonitor.ConnectivityListener connectivityListener){
        final int res = context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE");
        final boolean hasPermission = res == PackageManager.PERMISSION_GRANTED;
        if (hasPermission){
            return new DefaultConnectivityMonitor(context,connectivityListener);
        }else{
            //to do nothing
            return new NoConnectivityMonitor();
        }
    }
}
