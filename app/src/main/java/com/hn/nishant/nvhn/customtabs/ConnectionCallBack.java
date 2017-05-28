package com.hn.nishant.nvhn.customtabs;

import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;

/**
 * Created by nishant on 28.05.17.
 */
public interface ConnectionCallBack{

    void onServiceConnected(CustomTabsClient customTabsIntent);

    void onServiceDisconnected();
}

