package com.propertyguru.nishant.nvpropertyguru.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.propertyguru.nishant.nvpropertyguru.api.ApiService;
import com.propertyguru.nishant.nvpropertyguru.dao.StoriesDao;
import com.propertyguru.nishant.nvpropertyguru.dao.StoryDao;
import com.propertyguru.nishant.nvpropertyguru.model.Stories;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.network.AbstractBatchRequest;
import com.propertyguru.nishant.nvpropertyguru.network.BatchRequest;
import com.propertyguru.nishant.nvpropertyguru.network.FireBaseImpl;
import com.propertyguru.nishant.nvpropertyguru.network.ResponseListener;

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
            DoneCallback<RealmResults<Stories>>{

    public static final String TAG = StoryViewController.class.getSimpleName();

    private ApiService apiService = FireBaseImpl.getInstance();

    private RealmResults<Story> sotries;

    private RealmResults<Stories> collection;

    private DeferredObject<RealmResults<Story>,Void,Void> deferredStoryFetch = new DeferredObject();

    private DeferredObject<RealmResults<Stories>,Void,Void> deferredLeftStories = new DeferredObject();

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
        loadListener = (OnDataLoadListener) context;
    }

    @Override
    public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
        sotries.removeChangeListener(this);
        deferredStoryFetch.resolve(collection);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(sotries == null) {
            sotries = StoryDao.getStoriesSortedByRank();
        }
        sotries.addChangeListener(this);
        collection = StoriesDao.getStories();
        collection.addChangeListener(new StoriesChangeListener());
        getLatestStories();
    }

    public RealmResults<Story> getStories(){
        if(sotries == null){
            sotries = StoryDao.getStoriesSortedByRank();
        }
        return sotries;
    }

    private ResponseListener<List<Long>> storiesResponseListener = new ResponseListener<List<Long>>() {
        @Override
        public void onSuccess(final List<Long> list) {
            deferredStoryFetch.done(new DoneCallback<RealmResults<Story>>() {
                @Override
                public void onDone(RealmResults<Story> result) {
                    if(result.size() == 0){
                        firstFetch(list);
                    }else{
                        refreshList(list);
                    }
                }
            });
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
                if(loadListener != null) {
                    loadListener.onDataLoaded();
                }
            }
        }, ranks).start();

        ArrayList<Long> rem = new ArrayList<>();
        ArrayList<Long> rankss = new ArrayList<>();
        for (; i < list.size(); i++) {
            rem.add(list.get(i));
            rankss.add((long) i);
        }
        if(deferredLeftStories == null || !deferredLeftStories.isPending()) {
            deferredLeftStories = new DeferredObject<>();
        }
        StoriesDao.addToDb(rem, rankss);
    }

    private void refreshList(List<Long> list){
        ArrayList<Long> selectForUpdate = new ArrayList<>();
        final ArrayList<Integer> selectForDelete = new ArrayList<>();
        final ArrayList<Integer> ranksForSelected = new ArrayList<>();
        int i =0;
        for ( ;i< Math.min(list.size(),sotries.size());i++){
            if(list.get(i).intValue() == sotries.get(i).getId()){
                selectForUpdate.add(Long.valueOf(sotries.get(i).getId()));
            }else{
                selectForDelete.add(sotries.get(i).getId());
                selectForUpdate.add(list.get(i));
            }
            ranksForSelected.add(i);
        }

        new BatchRequest(selectForUpdate, new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addAndDelete(response,ranksForSelected,selectForDelete);
                if(loadListener != null) {
                    loadListener.onDataLoaded();
                }
            }
        },ranksForSelected).start();

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

    public void loadMore(final int noOfItems) {
        if(noOfItems <= 0){
            loadListener.onDataLoaded();
            return;
        }
        deferredLeftStories.done(this);
    }

    public interface OnDataLoadListener {
        void onDataLoaded();
    }

    private class StoriesChangeListener implements OrderedRealmCollectionChangeListener<RealmResults<Stories>>{
        @Override
        public void onChange(RealmResults<Stories> collection, OrderedCollectionChangeSet changeSet) {
            if(!deferredLeftStories.isResolved()) {
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
        new BatchRequest(req, new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addnewData(response);
                if(deferredLeftStories == null || !deferredLeftStories.isPending()) {
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
        if(deferredLeftStories.isPending()){
            deferredLeftStories.reject(null);
        }
        if(deferredStoryFetch.isPending()){
            deferredStoryFetch.reject(null);
        }
        collection.removeAllChangeListeners();
        sotries.removeAllChangeListeners();
        loadListener = null;
    }

}
