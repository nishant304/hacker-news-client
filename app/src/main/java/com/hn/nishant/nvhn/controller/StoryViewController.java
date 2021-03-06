package com.hn.nishant.nvhn.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.controller.interfaces.IStoryCateogry;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.events.UpdateEvent;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.AbstractBatchRequest;
import com.hn.nishant.nvhn.network.IntensiveStoryRequest;
import com.hn.nishant.nvhn.network.ResponseListener;
import com.hn.nishant.nvhn.network.StoryBatchRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DeferredObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nishant on 15.03.17.
 */

public class StoryViewController extends Fragment implements OrderedRealmCollectionChangeListener<RealmResults<Story>> {

    public static final String TAG = StoryViewController.class.getSimpleName();

    private IStoryCateogry storyCateogry = new TopStoryImpl();

    private ApiService apiService = App.getApiService();

    private RealmResults<Story> storiesList;

    private DeferredObject<RealmResults<Story>, Void, Void> deferredExistingStory = new DeferredObject();

    private OnDataLoadListener loadListener;

    private List<Long> liveStoryItemIds = new ArrayList<>();

    private int i = 0;

    private boolean isLoading = false;

    private IntensiveStoryRequest intensiveStoryRequest = new IntensiveStoryRequest();

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
    }

    public void setStoryCateogry(IStoryCateogry storyCateogry) {
        this.storyCateogry = storyCateogry;
        getLatestStories();
    }

    @Override
    public void onStart() {
        super.onStart();
        apiService.listenForUpdates();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
        storiesList.removeChangeListener(this);
        if (deferredExistingStory.isPending()) {
            deferredExistingStory.resolve(collection);
        }
    }

    public RealmResults<Story> getStories() {
        storiesList = storyCateogry.getLocalStories();
        storiesList.addChangeListener(this);
        return storiesList;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void getLatestStories() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        deferredExistingStory.done(new DoneCallback<RealmResults<Story>>() {
            @Override
            public void onDone(RealmResults<Story> storyOnDevice) {
                refresh();
            }
        });
    }

    private void refresh() {
        storyCateogry.getLatestStories(new ResponseListener<List<Long>>() {
            @Override
            public void onSuccess(List<Long> liveStoryItemIds) {
                StoryViewController.this.liveStoryItemIds = liveStoryItemIds;
                i = 0;
                refreshList(liveStoryItemIds);
            }

            @Override
            public void onError(Exception ex) {
                if (loadListener != null) {
                    loadListener.onLoadError(new Exception("Something went wrong"));
                }
                isLoading = false;
            }
        });
    }

    /***
     * load new latest list of items
     * keep items we already have on device from the new list
     * and delete items we have but not in the list
     * @param liveStoryItemIds
     */
    private void refreshList(List<Long> liveStoryItemIds) {
        ArrayList<Long> itemToBeFetchedList = new ArrayList<>();
        ArrayList<Integer> itemsToDelList = new ArrayList<>();
        ArrayList<Integer> ranks = new ArrayList<>();

        for (; i < Math.min(AbstractBatchRequest.getSuggestedReqCount(), liveStoryItemIds.size()); i++) {
            itemToBeFetchedList.add(liveStoryItemIds.get(i));
            ranks.add(i);
        }

        new StoryBatchRequest(itemToBeFetchedList, ranks, storyCateogry.getCategory(),
                new JobResponseListener(true, itemsToDelList, loadListener)).start();
    }

    public void loadMore() {
        if (isLoading) {
            return;
        }
        isLoading = true;

        final List<Long> req = new ArrayList<>();
        final List<Integer> ranks = new ArrayList<>();
        for (; i < liveStoryItemIds.size(); i++) {
            if (req.size() < AbstractBatchRequest.getSuggestedReqCount()) {
                req.add(liveStoryItemIds.get(i));
                ranks.add(i);
            } else {
                break;
            }
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new StoryBatchRequest(req, ranks,storyCateogry.getCategory(),
                        new JobResponseListener(false, null, loadListener)).start();
            }
        });
    }

    public interface OnDataLoadListener {
        void onDataLoaded();

        void onLoadError(Exception ex);
    }

    private class JobResponseListener implements AbstractBatchRequest.JobCompleteListener<Story> {

        private boolean isRefresh;
        private List<Integer> selectForDelete;
        private WeakReference<OnDataLoadListener> onDataLoadListenerWeakReference;

        JobResponseListener(boolean isRefresh, List<Integer> selectForDelete, OnDataLoadListener onDataLoadListener) {
            this.isRefresh = isRefresh;
            this.selectForDelete = selectForDelete;
            this.onDataLoadListenerWeakReference = new WeakReference<OnDataLoadListener>(onDataLoadListener);
        }

        @Override
        public void onJobComplete(final List<Story> response) {
            if (isRefresh) {
                StoryDao.addAndDelete(response, selectForDelete, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        OnDataLoadListener loadListener = onDataLoadListenerWeakReference.get();
                        if (loadListener != null) {
                            loadListener.onDataLoaded();
                        }
                        isLoading = false;
                    }
                });
            } else {
                StoryDao.addnewData(response, i != liveStoryItemIds.size(), new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        OnDataLoadListener loadListener = onDataLoadListenerWeakReference.get();
                        if (loadListener != null) {
                            loadListener.onDataLoaded();
                        }
                        isLoading = false;
                    }
                });
            }
        }
    }

    @Subscribe
    public void onItemUpdate(UpdateEvent updateEvent){
        new StoryBatchRequest(updateEvent.getUpdatedItems(), updateEvent.getRanks(),storyCateogry.getCategory(),
                new JobResponseListener(false, new ArrayList<Integer>(), loadListener)).start();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (deferredExistingStory.isPending()) {
            deferredExistingStory.reject(null);
        }
        storiesList.removeChangeListener(this);
        loadListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        apiService.stopListeningForUpdate();
    }

}
