package com.hn.nishant.nvhn.view.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.controller.NewStoryImpl;
import com.hn.nishant.nvhn.controller.StoryViewController;
import com.hn.nishant.nvhn.view.adapter.StoryAdapter;
import com.hn.nishant.nvhn.view.ui.ChangeItemAnimator;
import com.hn.nishant.nvhn.view.ui.LinearLayoutManager;

public class StoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        StoryViewController.OnDataLoadListener {

    private LinearLayoutManager layoutManager;

    public RecyclerView recyclerView;

    private StoryViewController storyViewController;

    private int pos;

    private SwipeRefreshLayout swipeRefreshLayout;

    private StoryAdapter storyAdapter;

    private boolean isLoading = false;

    private Toolbar toolbar;

    private NewStoryImpl newStoryImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ahnc");
        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            pos = savedInstanceState.getInt("pos");
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        storyViewController = StoryViewController.getInstance(getFragmentManager());
        setUpRecyclerView();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        swipeRefreshLayout.setRefreshing(storyViewController.isLoading());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.story_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            onRefresh();
        }else{
            onNewStorySelected();
        }

        return true;
    }

    private  void onNewStorySelected(){
        if(newStoryImpl == null){
            newStoryImpl = new NewStoryImpl();
            storyViewController.setStoryCateogry(newStoryImpl);
            storyAdapter.setNewData(storyViewController.getStories());
        }
    }

    private void setUpRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        storyAdapter = new StoryAdapter(this, storyViewController.getStories());

        recyclerView.addOnScrollListener(new ScrollListener());
        recyclerView.setHasFixedSize(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(1, 20);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(storyAdapter);
        recyclerView.setItemAnimator(new ChangeItemAnimator());
        layoutManager.scrollToPosition(pos);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("pos", layoutManager.findFirstVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        if (storyViewController.isLoading()) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        storyViewController.getLatestStories();
    }

    @Override
    public void onDataLoaded() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadError(Exception ex) {
        swipeRefreshLayout.setRefreshing(false);
        makeToast(ex.getMessage());
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleItems = layoutManager.getChildCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            int totalItems = layoutManager.getItemCount();
            if (dy > 0 && visibleItems + firstVisibleItem + 5 >= totalItems) {
                storyViewController.loadMore();
            }
        }

    }

    @Override
    protected void onDestroy() {
        storyAdapter.onDestroy();
        super.onDestroy();
    }

    @VisibleForTesting
    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

}
