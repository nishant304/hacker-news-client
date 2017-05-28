package com.hn.nishant.nvhn.customtabs;

import android.content.ComponentName;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;

import java.lang.ref.WeakReference;

/**
 * Created by nishant on 28.05.17.
 */

public class ServiceConnection extends CustomTabsServiceConnection {

    private WeakReference<ConnectionCallBack> weakReference;

    public ServiceConnection(ConnectionCallBack connectionCallBack){
        weakReference = new WeakReference<ConnectionCallBack>(connectionCallBack);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        ConnectionCallBack connectionCallBack = weakReference.get();
        if(connectionCallBack != null){
            connectionCallBack.onServiceDisconnected();
        }
    }

    @Override
    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        ConnectionCallBack connectionCallBack = weakReference.get();
        if(connectionCallBack != null){
            connectionCallBack.onServiceConnected(client);
        }
    }

}
