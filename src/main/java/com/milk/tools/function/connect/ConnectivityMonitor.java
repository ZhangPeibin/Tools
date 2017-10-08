
package com.milk.tools.function.connect;
/**
 * Created by Administrator on 2016/12/9.
 */
public interface ConnectivityMonitor {

    /**
     * An interface for listening to network connectivity events picked up by the monitor.
     */
    interface ConnectivityListener {
        /**
         * Called when the connectivity state changes.
         *
         * @param isConnected True if we're currently connected to a network, false otherwise.
         */
        void onConnectivityChanged(boolean isConnected);
    }

    void unRegister();
}
