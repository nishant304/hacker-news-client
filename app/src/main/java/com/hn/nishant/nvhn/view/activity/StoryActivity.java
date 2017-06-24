package com.hn.nishant.nvhn.view.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.controller.AskStoryImpl;
import com.hn.nishant.nvhn.controller.BestStoryImpl;
import com.hn.nishant.nvhn.controller.JobStoryImpl;
import com.hn.nishant.nvhn.controller.NewStoryImpl;
import com.hn.nishant.nvhn.controller.ShowStoryImpl;
import com.hn.nishant.nvhn.controller.StoryViewController;
import com.hn.nishant.nvhn.controller.TopStoryImpl;
import com.hn.nishant.nvhn.controller.interfaces.IStoryCateogry;
import com.hn.nishant.nvhn.customtabs.CustomTabActivityHelper;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.view.adapter.StoryAdapter;
import com.hn.nishant.nvhn.view.ui.ChangeItemAnimator;
import com.hn.nishant.nvhn.view.ui.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class StoryActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        StoryViewController.OnDataLoadListener, AdapterView.OnItemClickListener {

    private LinearLayoutManager layoutManager;

    public RecyclerView recyclerView;

    private StoryViewController storyViewController;

    private int pos;

    private SwipeRefreshLayout swipeRefreshLayout;

    private StoryAdapter storyAdapter;

    private boolean isLoading = false;

    private Toolbar toolbar;

    private NewStoryImpl newStoryImpl;

    private DrawerLayout drawerLayout;

    private ListView listView;

    private CustomTabActivityHelper customTabActivityHelper;

    private String [] mPlanetTitles = new String[]{"abc","bca","best","show","ask","job"};

    private IStoryCateogry[] storyCateogry = new IStoryCateogry[]{new NewStoryImpl(),
            new TopStoryImpl(),new BestStoryImpl(), new ShowStoryImpl(), new AskStoryImpl()
            , new JobStoryImpl()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.left_drawer);
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mPlanetTitles));
        listView.setOnItemClickListener(this);
        toolbar.setTitle("ahnc");
        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            pos = savedInstanceState.getInt("pos");
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        storyViewController = StoryViewController.getInstance(getFragmentManager());
        storyViewController.getLatestStories();
        setUpRecyclerView();
        customTabActivityHelper = new CustomTabActivityHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindService();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindService();
        EventBus.getDefault().unregister(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStorySelected(Story story){
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setStartAnimations(this,R.anim.slide_in_right,R.anim.slide_out_left);
        intentBuilder.setExitAnimations(this,android.R.anim.slide_in_left,android.R.anim.slide_out_right);

        Intent intent = new Intent(this,CommentsActivty.class);
        intent.putExtra("storyId", story.getId());

        //intentBuilder.setToolbarColor(R.color.black_overlay);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        intentBuilder.setActionButton(BitmapFactory.decodeResource(getResources(),R.drawable.comments)
                ,"comments",pendingIntent);
        customTabActivityHelper.openCustomTab(1, Uri.parse(story.getUrl()), intentBuilder.build());
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        storyViewController.setStoryCateogry(storyCateogry[position]);
        storyAdapter.setNewData(storyViewController.getStories());
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
        //helper.attachToRecyclerView(recyclerView);
        layoutManager.scrollToPosition(pos);
    }

    private Drawable bg = new ColorDrawable(Color.RED);

    private ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT
    |ItemTouchHelper.RIGHT){
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            makeToast("swiped");
            int pos = viewHolder.getAdapterPosition();
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

    });

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
            if (dy > 0 && visibleItems + firstVisibleItem >= totalItems) {
                storyViewController.loadMore();
            }

        }
    }

    @Override
    protected void onDestroy() {
        storyAdapter.onDestroy();
        super.onDestroy();
    }

}
