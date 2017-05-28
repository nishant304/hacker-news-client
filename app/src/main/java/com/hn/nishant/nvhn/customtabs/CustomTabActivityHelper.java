package com.hn.nishant.nvhn.customtabs;

import android.app.Activity;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;

import java.lang.ref.WeakReference;


/**
 * Created by nishant on 28.05.17.
 */

public class CustomTabActivityHelper implements ConnectionCallBack {

    private CustomTabsClient mClient;

    private ServiceConnection mServiceConnection;

    private CustomTabFallBack customTabFallBack = new CTFallBack();

    private WeakReference<Activity> weakRefActivity;

    public CustomTabActivityHelper(Activity activity){
        weakRefActivity = new WeakReference<Activity>(activity);
        mServiceConnection = new ServiceConnection(this);
    }

    public void openCustomTab(long position, Uri uri
                , CustomTabsIntent customTabsIntent){
        Activity activity = weakRefActivity.get();
        if(activity == null){
            return;
        }
        String packageName = CustomTabsHelper.getPackageNameToUse(activity);

        if(packageName == null){
            customTabFallBack.openUri(activity,position);
        }else{
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity,uri);
        }
    }

    public void unbindService(){
        Activity activity = weakRefActivity.get();
        if(mServiceConnection != null) {
            activity.unbindService(mServiceConnection);
        }
    }

    public void bindService(){
        Activity activity = weakRefActivity.get();
        CustomTabsClient.bindCustomTabsService(activity,
                CustomTabsHelper.getPackageNameToUse(activity),mServiceConnection);
    }

    @Override
    public void onServiceDisconnected() {
        mClient = null;
    }

    @Override
    public void onServiceConnected(CustomTabsClient customTabsIntent) {
        mClient = customTabsIntent;
        mClient.warmup(0L);
    }

    public interface CustomTabFallBack{

        void openUri(Activity activity, long  position);

    }

}
