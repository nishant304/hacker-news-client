package com.propertyguru.nishant.nvpropertyguru.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.propertyguru.nishant.nvpropertyguru.App;
import com.propertyguru.nishant.nvpropertyguru.api.ApiService;
import com.propertyguru.nishant.nvpropertyguru.dao.StoriesDao;
import com.propertyguru.nishant.nvpropertyguru.dao.StoryDao;
import com.propertyguru.nishant.nvpropertyguru.model.Stories;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.network.AbstractBatchRequest;
import com.propertyguru.nishant.nvpropertyguru.network.BatchRequest;
import com.propertyguru.nishant.nvpropertyguru.network.FireBaseImpl;
import com.propertyguru.nishant.nvpropertyguru.network.ResponseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nishant on 15.03.17.
 */

public class StoryController extends Fragment {

    public static final String TAG = StoryController.class.getSimpleName();

    private ApiService apiService = FireBaseImpl.getInstance();

    public LinearLayoutManager layoutManager;

    private RealmResults<Story> sotries;

    private OnDataLoadListener loadListener;

    public static StoryController getInstance(@NonNull FragmentManager fragmentManager) {
        StoryController storyController = (StoryController) fragmentManager.findFragmentByTag(TAG);
        if (storyController == null) {
            storyController = new StoryController();
            fragmentManager.beginTransaction().add(storyController, TAG).commit();
        }
        return storyController;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        layoutManager = new RobustLayoutManaget(context);
        loadListener = (OnDataLoadListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        fetchOnCreate();
    }

    public RealmResults<Story> getStories() {
        if (sotries == null) {
            sotries = StoryDao.getStoriesSortedByRank();
        }
        return sotries;
    }

    public void fetchOnCreate() {
        getStories().addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Story>>() {
            @Override
            public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
                getStories().removeChangeListener(this);
                if (collection.size() == 0) {
                    fetchFromNetwork();
                }
            }
        });
    }

    public void refreshList() {
        fetchFromNetwork();
    }

    private ResponseListener<List<Long>> storiesResponseListener = new ResponseListener<List<Long>>() {
        @Override
        public void onSuccess(List<Long> list) {
                firstFetch(list);
        }

        @Override
        public void onError(Exception ex) {

        }
    };

    private void firstFetch(List<Long> list) {
        int i = 0;
        ArrayList<Long> res = new ArrayList<>();
        ArrayList<Integer> ranks = new ArrayList<>();
        for (; i < Math.min(15, list.size()); i++) {
            res.add(list.get(i));
            ranks.add(i);
        }
        new BatchRequest(res, new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addnewData(response);
                loadListener.onDataLoaded();
            }
        }, ranks).start();

        ArrayList<Long> rem = new ArrayList<>();
        ArrayList<Long> rankss = new ArrayList<>();
        for (; i < list.size(); i++) {
            rem.add(list.get(i));
            rankss.add((long) i);
        }
        Stories.addToDb(rem, rankss, false);
    }

    private void fetchFromNetwork() {
        apiService.getStoryIds(storiesResponseListener);
    }

    public void loadMore(final AbstractBatchRequest.JobCompleteListener<Story> jobCompleteListener) {
        final RealmResults<Stories> result = StoriesDao.getStories();
        result.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Stories>>() {
            @Override
            public void onChange(RealmResults<Stories> collection, OrderedCollectionChangeSet changeSet) {
                result.removeAllChangeListeners();
                if (collection.size() == 0) {
                    jobCompleteListener.onJobComplete(new ArrayList<Story>());
                    return;
                }
                List<Long> req = new ArrayList<Long>();
                List<Integer> ranks = new ArrayList<Integer>();
                for (int i = 0; i < Math.min(8, collection.size()); i++) {
                    req.add(collection.get(i).getId());
                    ranks.add(collection.get(i).getRank());
                }
                new BatchRequest(req, jobCompleteListener, ranks).start();
            }
        });
    }

    public RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        private boolean loading;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleItems = layoutManager.getChildCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            int totalItems = layoutManager.getItemCount();

            if (dy > 0 && !loading && visibleItems + firstVisibleItem >= totalItems) {
                loading = true;
                loadMore(new AbstractBatchRequest.JobCompleteListener<Story>() {
                    @Override
                    public void onJobComplete(List<Story> response) {
                        StoryDao.addnewData(response);
                        StoriesDao.delete(response);
                        loading = false;
                    }
                });
            }
        }
    };

    public interface OnDataLoadListener {
        void onDataLoaded();
    }

    private class RobustLayoutManaget extends LinearLayoutManager{

        RobustLayoutManaget(Context context){
            super(context);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

}
