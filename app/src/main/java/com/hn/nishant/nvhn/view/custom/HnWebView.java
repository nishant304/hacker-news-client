package com.hn.nishant.nvhn.view.custom;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hn.nishant.nvhn.view.activity.BrowseActivity;

/**
 * Created by nishant on 08.04.17.
 */

public class HnWebView extends WebView {

    private BrowseActivity browseActivity;

    public boolean isFinished() {
        return isFinished;
    }

    private boolean isFinished;

    private final String name = "App";

    public HnWebView(Context context, String url){
        super(context);
        browseActivity = (BrowseActivity)context;
        setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height");
                super.onPageFinished(view,url);
                isFinished = true;
            }
        });
        getSettings().setAllowFileAccess( true );
        getSettings().setJavaScriptEnabled( true );
        getSettings().setCacheMode( WebSettings.LOAD_NO_CACHE );
        addJavascriptInterface(this,name);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
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

    @JavascriptInterface
    public void resize(final float height) {
        browseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }


}
