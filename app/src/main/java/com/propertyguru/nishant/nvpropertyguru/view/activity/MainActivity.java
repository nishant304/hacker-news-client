package com.propertyguru.nishant.nvpropertyguru.view.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.propertyguru.nishant.nvpropertyguru.R;
import com.propertyguru.nishant.nvpropertyguru.controller.StoryViewController;
import com.propertyguru.nishant.nvpropertyguru.view.adapter.StoryAdapter;
import com.propertyguru.nishant.nvpropertyguru.view.ui.LinearLayoutManager;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,StoryViewController.OnDataLoadListener {

    private LinearLayoutManager layoutManager;

    private StoryViewController storyViewController;

    private int pos ;

    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null){
            pos = savedInstanceState.getInt("pos");
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        storyViewController = StoryViewController.getInstance(getFragmentManager());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new ScrollListener());
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new StoryAdapter(this, storyViewController.getStories()));
        layoutManager.scrollToPosition(pos);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("pos",layoutManager.findFirstVisibleItemPosition());
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

    private class ScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleItems = layoutManager.getChildCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            int totalItems = layoutManager.getItemCount();
            if (dy > 0 && !isLoading && visibleItems + firstVisibleItem +3>= totalItems) {
                isLoading = true;
                storyViewController.loadMore(2*visibleItems);
            }
        }
    }

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

}
