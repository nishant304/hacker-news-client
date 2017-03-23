package com.hn.nishant.nvhn.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.dao.StoriesDao;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.model.Stories;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.AbstractBatchRequest;
import com.hn.nishant.nvhn.network.ResponseListener;
import com.hn.nishant.nvhn.network.StoryBatchRequest;

import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/**
 * Created by nishant on 15.03.17.
 */

public class StoryViewController extends Fragment implements OrderedRealmCollectionChangeListener<RealmResults<Story>>,
        DoneCallback<RealmResults<Stories>> {

    public static final String TAG = StoryViewController.class.getSimpleName();

    private ApiService apiService = App.getApiService();

    private RealmResults<Story> storiesList;

    private RealmResults<Stories> remainingStoriesIds;

    private DeferredObject<RealmResults<Story>, Void, Void> deferredStoryFetch = new DeferredObject();

    private DeferredObject<RealmResults<Stories>, Void, Void> deferredLeftStories = new DeferredObject();

    private OnDataLoadListener loadListener;

    public static StoryViewController getInstance(@NonNull FragmentManager fragmentManager) {
        StoryViewController storyViewController = (StoryViewController) fragmentManager.findFragmentByTag(TAG);
        if (storyViewController == null) {
            storyViewController = new StoryViewController();
            fragmentManager
                    .beginTransaction().add(storyViewController, TAG).commit();
        }
        return storyViewController;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnDataLoadListener){
            loadListener = (OnDataLoadListener) context;
        }else{
            throw new IllegalStateException("activty must implement ondataloadlistener");
        }
    }

    @Override
    public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
        storiesList.removeChangeListener(this);
        if(deferredStoryFetch.isPending()) {
            deferredStoryFetch.resolve(collection);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (storiesList == null) {
            storiesList = StoryDao.getStoriesSortedByRank();
        }
        storiesList.addChangeListener(this);
        remainingStoriesIds = StoriesDao.getStories();
        remainingStoriesIds.addChangeListener(new StoriesChangeListener());
        getLatestStories();
    }

    public RealmResults<Story> getStories() {
        if (storiesList == null) {
            storiesList = StoryDao.getStoriesSortedByRank();
        }
        return storiesList;
    }

    private class StroyListResponseListener implements ResponseListener<List<Long>>  {

        private DeferredObject<List<Long>,Void,Void> deferredObject;

        private StroyListResponseListener(DeferredObject<List<Long>,Void,Void> deferredObject){
            this.deferredObject = deferredObject;
        }

        @Override
        public void onSuccess(List<Long> list) {
            deferredObject.resolve(list);
        }

        @Override
        public void onError(Exception ex) {
            if(loadListener != null){
                loadListener.onLoadError(ex);
            }
        }
    }

    private void getNewStories(List<Long> list) {
        int i = 0;
        ArrayList<Long> res = new ArrayList<>();
        ArrayList<Integer> ranks = new ArrayList<>();
        for (; i < Math.min(15, list.size()); i++) {
            res.add(list.get(i));
            ranks.add(i);
        }
        new StoryBatchRequest(res, new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addnewData(response);
                if (loadListener != null) {
                    if(response.size() != 0) {
                        loadListener.onDataLoaded();
                    }else{
                        loadListener.onLoadError(new Exception("Something went wrong"));
                    }
                }
            }
        }, ranks).start();

        ArrayList<Long> rem = new ArrayList<>();
        ArrayList<Long> rankss = new ArrayList<>();
        for (; i < list.size(); i++) {
            rem.add(list.get(i));
            rankss.add((long) i);
        }
        if (deferredLeftStories == null || !deferredLeftStories.isPending()) {
            deferredLeftStories = new DeferredObject<>();
        }
        StoriesDao.addToDb(rem, rankss);
    }

    private void refreshList(List<Long> list) {
        ArrayList<Long> selectForUpdate = new ArrayList<>();
        final ArrayList<Integer> selectForDelete = new ArrayList<>();
        final ArrayList<Integer> ranksForSelected = new ArrayList<>();
        int i = 0;
        for (; i < Math.min(list.size(), storiesList.size()); i++) {
            if (list.get(i).intValue() == storiesList.get(i).getId()) {
                selectForUpdate.add(Long.valueOf(storiesList.get(i).getId()));
            } else {
                selectForDelete.add(storiesList.get(i).getId());
                selectForUpdate.add(list.get(i));
            }
            ranksForSelected.add(i);
        }

        new StoryBatchRequest(selectForUpdate, new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addAndDelete(response, ranksForSelected, selectForDelete);
                if (loadListener != null) {
                    if(response.size() != 0) {
                        loadListener.onDataLoaded();
                    }else{
                        loadListener.onLoadError(new Exception("Something went wrong"));
                    }
                }
            }
        }, ranksForSelected).start();

        ArrayList<Long> rem = new ArrayList<>();
        ArrayList<Long> rankss = new ArrayList<>();
        for (; i < list.size(); i++) {
            rem.add(list.get(i));
            rankss.add((long) i);
        }
        StoriesDao.addToDb(rem, rankss);
    }

    public void getLatestStories() {
        apiService.getStoryIds(storiesResponseListener);
    }

    public void onRefreshRequest(){
        DeferredObject<List<Long>,Void,Void> deferredObject = new DeferredObject<>();
        apiService.getStoryIds();
    }

    public void loadMore(final int noOfItems) {
        if (noOfItems <= 0) {
            loadListener.onDataLoaded();
            return;
        }
        deferredLeftStories.done(this);
    }

    public interface OnDataLoadListener {
        void onDataLoaded();
        void onLoadError(Exception ex);
    }

    private class StoriesChangeListener implements OrderedRealmCollectionChangeListener<RealmResults<Stories>> {
        @Override
        public void onChange(RealmResults<Stories> collection, OrderedCollectionChangeSet changeSet) {
            if (!deferredLeftStories.isResolved()) {
                deferredLeftStories.resolve(collection);
            }
        }
    }

    @Override
    public void onDone(RealmResults<Stories> collection) {
        List<Long> req = new ArrayList<Long>();
        List<Integer> ranks = new ArrayList<Integer>();
        for (int i = 0; i < Math.min(AbstractBatchRequest.getSuggestedReqCount(), collection.size()); i++) {
            req.add(collection.get(i).getId());
            ranks.add(collection.get(i).getRank());
        }
        new StoryBatchRequest(req, new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addnewData(response);
                if (deferredLeftStories == null || !deferredLeftStories.isPending()) {
                    deferredLeftStories = new DeferredObject<>();
                }
                StoriesDao.delete(response);
                loadListener.onDataLoaded();
            }
        }, ranks).start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (deferredLeftStories.isPending()) {
            deferredLeftStories.reject(null);
        }
        if (deferredStoryFetch.isPending()) {
            deferredStoryFetch.reject(null);
        }
        remainingStoriesIds.removeAllChangeListeners();
        storiesList.removeAllChangeListeners();
        loadListener = null;
    }

}
