package com.hn.nishant.nvhn.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.dao.StoryToFetchDao;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.model.StoryToFetch;
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

public class StoryViewController extends Fragment implements OrderedRealmCollectionChangeListener<RealmResults<Story>> {

    public static final String TAG = StoryViewController.class.getSimpleName();

    private ApiService apiService = App.getApiService();

    private RealmResults<Story> storiesList;

    private RealmResults<StoryToFetch> remainingStoryToFetchIds;

    private DeferredObject<RealmResults<Story>, Void, Void> deferredExistingStory = new DeferredObject();

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
        if (context instanceof OnDataLoadListener) {
            loadListener = (OnDataLoadListener) context;
        } else {
            throw new IllegalStateException("activty must implement ondataloadlistener");
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
        remainingStoryToFetchIds = StoryToFetchDao.getStoriesToFetch();
        getLatestStories();
    }

    @Override
    public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
        storiesList.removeChangeListener(this);
        deferredExistingStory.resolve(collection);
    }

    public RealmResults<Story> getStories() {
        if (storiesList == null) {
            storiesList = StoryDao.getStoriesSortedByRank();
        }
        return storiesList;
    }

    private class LiveStroyListResponseListener implements ResponseListener<List<Long>> {

        private DeferredObject<List<Long>, Void, Void> deferredLiveStoryList;

        private LiveStroyListResponseListener(DeferredObject<List<Long>, Void, Void> deferredLiveStoryList) {
            this.deferredLiveStoryList = deferredLiveStoryList;
        }

        @Override
        public void onSuccess(List<Long> list) {
            deferredLiveStoryList.resolve(list);
            StoryToFetchDao.deleteFromRemainderList();
            StoryToFetchDao.add(list);
        }

        @Override
        public void onError(Exception ex) {
            if (loadListener != null) {
                loadListener.onLoadError(new Exception("Something went wrong"));
            }
        }
    }

    private void refreshList(List<Long> list) {
        ArrayList<Long> selectForUpdate = new ArrayList<>();
        final ArrayList<Integer> selectForDelete = new ArrayList<>();
        final ArrayList<Integer> ranks = new ArrayList<>();
        int i = 0;
        for (; i < Math.min(Math.min(AbstractBatchRequest.getSuggestedReqCount(), list.size()), storiesList.size()); i++) {
            if (list.get(i).intValue() == storiesList.get(i).getId()) {
                selectForUpdate.add(Long.valueOf(storiesList.get(i).getId()));
            } else {
                selectForDelete.add(storiesList.get(i).getId());
                selectForUpdate.add(list.get(i));
            }
            ranks.add(i);
        }
        for (; i < storiesList.size(); i++) {
            selectForDelete.add(storiesList.get(i).getId());
        }

        new StoryBatchRequest(selectForUpdate,ranks, new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addAndDelete(response, selectForDelete);
                StoryToFetchDao.deleteFromRemainderList(response);
                if (loadListener != null) {
                    loadListener.onDataLoaded();
                }
            }
        }).start();
    }

    public void getLatestStories() {
        deferredExistingStory.done(new DoneCallback<RealmResults<Story>>() {
            @Override
            public void onDone(RealmResults<Story> storyOnDevice) {
                loadOrRefresh(storyOnDevice.size() !=0 );
            }
        });
    }

    private void loadOrRefresh(final boolean shouldReresh) {
        DeferredObject<List<Long>, Void, Void> deferredLiveStoryIds = new DeferredObject<>();
        apiService.getStoryIds(new LiveStroyListResponseListener(deferredLiveStoryIds));
        deferredLiveStoryIds.done(new DoneCallback<List<Long>>() {
            @Override
            public void onDone(List<Long> liveStoryItemIds) {
                if(shouldReresh) {
                    refreshList(liveStoryItemIds);
                }else{
                    loadMore();
                }
            }
        });
    }

    public void loadMore() {
        remainingStoryToFetchIds = StoryToFetchDao.getStoriesToFetch();
        remainingStoryToFetchIds.addChangeListener(new LoadFromRemainingStory());
    }

    public interface OnDataLoadListener {
        void onDataLoaded();

        void onLoadError(Exception ex);
    }

    private class LoadFromRemainingStory implements OrderedRealmCollectionChangeListener<RealmResults<StoryToFetch>> {
        @Override
        public void onChange(RealmResults<StoryToFetch> remianingStory, OrderedCollectionChangeSet changeSet) {
            remianingStory.removeChangeListener(this);
            List<Long> req = new ArrayList<Long>();
            List<Integer> ranks = new ArrayList<>();
            for (int i = 0; i < Math.min(AbstractBatchRequest.getSuggestedReqCount(), remianingStory.size()); i++) {
                req.add(remianingStory.get(i).getId());
                ranks.add(remianingStory.get(i).getRank());
            }
            new StoryBatchRequest(req,ranks, new AbstractBatchRequest.JobCompleteListener<Story>() {
                @Override
                public void onJobComplete(List<Story> response) {
                    StoryDao.addnewData(response);
                    StoryToFetchDao.deleteFromRemainderList(response);
                    if (loadListener != null) {
                        loadListener.onDataLoaded();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (deferredExistingStory.isPending()) {
            deferredExistingStory.reject(null);
        }
        remainingStoryToFetchIds.removeAllChangeListeners();
        storiesList.removeAllChangeListeners();
        loadListener = null;
    }

}
