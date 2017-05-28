package com.hn.nishant.nvhn.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.controller.StoryViewController;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.view.custom.HnWebView;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/**
 * Created by nishant on 08.04.17.
 */

public class BrowseActivity extends BaseActivity implements StoryViewController.OnDataLoadListener, OrderedRealmCollectionChangeListener<RealmResults<Story>> {

    StoryViewController storyViewController;

    ViewPager viewPager;

    private View mDecorView;

    private RealmResults<Story> realmResults;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDecorView = getWindow().getDecorView();

        //hideSystemUI();
        setContentView(R.layout.browse_activity);
        storyViewController = StoryViewController.getInstance(getFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        realmResults = storyViewController.getStories();
        realmResults.addChangeListener(this);
    }

    @Override
    public void onDataLoaded() {

    }

    @Override
    public void onLoadError(Exception ex) {

    }

    @Override
    public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new BrowsePagerAdapter(realmResults));
        viewPager.setCurrentItem(100000);
        realmResults.removeAllChangeListeners();
    }

    private class BrowsePagerAdapter extends PagerAdapter {

        RealmResults<Story> res;

        private WebViewCache webViewCache = new WebViewCache();

        private BrowsePagerAdapter(RealmResults<Story> realmResults){
            this.res = realmResults;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int pos = position % res.size();
            HnWebView hnWebView = webViewCache.get(pos);
            if(hnWebView == null) {
                hnWebView = new HnWebView(BrowseActivity.this, realmResults.get(pos).getUrl());
            }
            int prev = pos ==0 ? res.size()-1 : pos-1;
            if(webViewCache.get(prev) == null){
                webViewCache.put(prev, new HnWebView(BrowseActivity.this,realmResults.get(prev).getUrl()));
            }

            int next = pos == res.size()-1 ? 0 :pos+1;
            if(webViewCache.get(next) == null){
                webViewCache.put(next, new HnWebView(BrowseActivity.this,realmResults.get(next).getUrl()));
            }
            if(hnWebView.isFinished()){
                hnWebView.reload();
            }
            container.addView(hnWebView);
            return hnWebView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            HnWebView hnWebView = (HnWebView)object;
            hnWebView.stopLoading();
            hnWebView.loadUrl("about:blank");
            container.removeView((View)object);
        }
    }


    private class WebViewCache extends LruCache<Integer,HnWebView>{

        private WebViewCache(){
            super(15);
        }
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            hideSystemUI();
        }else{
            //hideSystemUI();
        }
    }

    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
