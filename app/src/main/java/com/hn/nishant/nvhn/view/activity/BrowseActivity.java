package com.hn.nishant.nvhn.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

    private RealmResults<Story> realmResults;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        viewPager.setAdapter(new BrowsePagerAdapter(realmResults));
        viewPager.setCurrentItem(100000);
        realmResults.removeAllChangeListeners();
    }

    private class BrowsePagerAdapter extends PagerAdapter {

        RealmResults<Story> res;

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
            HnWebView hnWebView = new HnWebView(BrowseActivity.this,realmResults.get(pos).getUrl());
            container.addView(hnWebView);
            return hnWebView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

}
