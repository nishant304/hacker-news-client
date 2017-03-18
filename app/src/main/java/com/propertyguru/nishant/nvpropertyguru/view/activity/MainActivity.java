package com.propertyguru.nishant.nvpropertyguru.view.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.propertyguru.nishant.nvpropertyguru.R;
import com.propertyguru.nishant.nvpropertyguru.controller.StoryController;
import com.propertyguru.nishant.nvpropertyguru.view.adapter.StoryAdapter;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,StoryController.OnDataLoadListener {

    private RecyclerView recyclerView;

    private  StoryAdapter storyAdapter;

    private LinearLayoutManager layoutManager;

    private StoryController storyController;

    int pos ;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null){
            pos = savedInstanceState.getInt("pos");
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        storyController = StoryController.getInstance(getFragmentManager());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(storyController.scrollListener);
        storyAdapter = new StoryAdapter(this,storyController.getStories());
        recyclerView.setAdapter(storyAdapter);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        recyclerView.setLayoutManager(storyController.layoutManager);
        storyController.layoutManager.scrollToPosition(pos);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("pos",storyController.layoutManager.findFirstVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        storyController.refreshList();
    }

    @Override
    public void onDataLoaded() {
        swipeRefreshLayout.setRefreshing(false);
    }
}
