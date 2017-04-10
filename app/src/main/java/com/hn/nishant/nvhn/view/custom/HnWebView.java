package com.hn.nishant.nvhn.view.custom;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * Created by nishant on 08.04.17.
 */

public class HnWebView extends WebView {

    public HnWebView(Context context, String url){
        super(context);
        setWebViewClient(new WebViewClient());
        getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 );
        getSettings().setAppCachePath( context.getApplicationContext().getCacheDir().getAbsolutePath() );
        getSettings().setAllowFileAccess( true );
        getSettings().setAppCacheEnabled( true );
        getSettings().setJavaScriptEnabled( true );
        getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

        if ( !isNetworkAvailable() ) {
            getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }
        loadUrl(url);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
