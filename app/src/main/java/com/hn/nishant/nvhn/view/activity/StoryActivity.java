package com.hn.nishant.nvhn.view.activity;

import android.support.annotation.VisibleForTesting;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.controller.StoryViewController;
import com.hn.nishant.nvhn.view.adapter.StoryAdapter;
import com.hn.nishant.nvhn.view.ui.LinearLayoutManager;

public class StoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, StoryViewController.OnDataLoadListener {

    private LinearLayoutManager layoutManager;

    public RecyclerView recyclerView;

    private StoryViewController storyViewController;

    private int pos;

    private SwipeRefreshLayout swipeRefreshLayout;

    private StoryAdapter storyAdapter;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            pos = savedInstanceState.getInt("pos");
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        storyViewController = StoryViewController.getInstance(getFragmentManager());
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new ScrollListener());
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        storyAdapter = new StoryAdapter(this, storyViewController.getStories());
        recyclerView.setAdapter(storyAdapter);
        layoutManager.scrollToPosition(pos);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("pos", layoutManager.findFirstVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        isLoading = true;
        storyViewController.getLatestStories();
    }

    @Override
    public void onDataLoaded() {
        isLoading = false;
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadError(Exception ex) {
        isLoading = false;
        swipeRefreshLayout.setRefreshing(false);
        makeToast(ex.getMessage());
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleItems = layoutManager.getChildCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            int totalItems = layoutManager.getItemCount();
            if (dy > 0 && !isLoading && visibleItems + firstVisibleItem >= totalItems) {
                isLoading = true;
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
