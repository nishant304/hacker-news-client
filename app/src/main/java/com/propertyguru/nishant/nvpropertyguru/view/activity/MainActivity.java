package com.propertyguru.nishant.nvpropertyguru.view.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.propertyguru.nishant.nvpropertyguru.App;
import com.propertyguru.nishant.nvpropertyguru.R;
import com.propertyguru.nishant.nvpropertyguru.controller.StoryController;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.view.adapter.StoryAdapter;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;

    private  StoryAdapter storyAdapter;

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        //swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new ScrollListener());
    }

    @Override
    public void onRefresh() {
        //StoryController.fetchLatest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StoryController.fetchOnCreate();
        RealmResults<Story> list = Realm.getInstance(App.getConfig()).where(Story.class).findAllAsync();
        list.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Story>>() {
            @Override
            public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
                if(changeSet != null) {
                    storyAdapter.notifyDataSetChanged();
                }
            }
        });
        storyAdapter = new StoryAdapter(this,list);
        recyclerView.setAdapter(storyAdapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private  class ScrollListener extends RecyclerView.OnScrollListener {

        private boolean loading;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleItems = layoutManager.getChildCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            int totalItems = layoutManager.getItemCount();

            if(dy>0 && !loading && 2*visibleItems + firstVisibleItem >= totalItems){
                loading = true;
                StoryController.fetchMore(new StoryController.StoryFetchListener() {
                    @Override
                    public void onStoryFetched() {
                        loading = false;
                    }
                });
            }
        }

    }
}
