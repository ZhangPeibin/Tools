package com.milk.tools.function.connect;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.milk.tools.common.Preconditions;

/**
 * Created by Administrator on 2016/12/9.
 */

public class DefaultConnectivityMonitor implements ConnectivityMonitor {

    private boolean mIsRegister = false;

    private boolean mIsConnected = false;

    private Context mContext;

    private ConnectivityMonitor.ConnectivityListener mConnectivityListener;

    private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean wasConnected = mIsConnected;
            mIsConnected = isConnected(context);
            if (wasConnected != mIsConnected){
                mConnectivityListener.onConnectivityChanged(mIsConnected);
            }
        }
    };

    public DefaultConnectivityMonitor(Context context,ConnectivityMonitor.ConnectivityListener connectivityListener) {
        mContext = Preconditions.checkNotNull(context, "you can not listener network state with a null context");
        mConnectivityListener = Preconditions.checkNotNull(connectivityListener,"Use a null connectivityListener is meaningless");
        register();
    }

    public void register() {
        if (mIsRegister) {
            return;
        }
        mIsConnected = isConnected(mContext);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(connectivityReceiver, intentFilter);
        mIsRegister = true;
    }

    public void unRegister() {
        if (!mIsRegister) {
            return;
        }

        mContext.unregisterReceiver(connectivityReceiver);
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
