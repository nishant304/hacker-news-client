package com.propertyguru.nishant.nvpropertyguru.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;

/**
 * Created by nishant on 20.03.17.
 *
 * workaround for relam limitation for storing array of ints
 */


public final class AppUtil {


    public static int getCountForConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected() ){
            return 0;
        }

        // give the user best experience on wifi
        if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            return 10;
        }

        //save battery by fetching extra data on fast network
        if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
            if(networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA ||
                    networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA){
                return 15;
            }
        }

        //fetch less data on slow network
        return 5;
    }


}
