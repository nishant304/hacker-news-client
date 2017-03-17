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
import com.propertyguru.nishant.nvpropertyguru.BatchRequest;
import com.propertyguru.nishant.nvpropertyguru.api.ApiService;
import com.propertyguru.nishant.nvpropertyguru.api.RetorfitApiService;
import com.propertyguru.nishant.nvpropertyguru.dao.SotryDao;
import com.propertyguru.nishant.nvpropertyguru.model.Stories;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.model.UpdateStory;
import com.propertyguru.nishant.nvpropertyguru.network.FireBaseImpl;
import com.propertyguru.nishant.nvpropertyguru.network.ResponseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nishant on 15.03.17.
 */

public class StoryController extends Fragment {

    public static final String TAG = StoryController.class.getSimpleName();

    private ApiService apiService = FireBaseImpl.getInstance();

    public LinearLayoutManager layoutManager ;

    private RealmResults<Story> sotries;

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
        layoutManager = new LinearLayoutManager(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        fetchOnCreate();
    }

    public RealmResults<Story> getStories() {
        if (sotries == null) {
            sotries = Realm.getInstance(App.getConfig()).where(Story.class).findAllAsync();
        }
        return sotries;
    }

    public void fetchOnCreate() {
        RealmResults<Stories> result = Realm.getInstance(App.getConfig())
                .where(Stories.class).equalTo("isFetched", true).findAllAsync();
        result.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Stories>>() {
            @Override
            public void onChange(RealmResults<Stories> collection, OrderedCollectionChangeSet changeSet) {
                if (collection.size() == 0) {
                    fetchFromNetwork();
                }
            }
        });
    }

    public void fetchLatest() {
        fetchFromNetwork();
    }

    private ResponseListener<List<Long>> storiesResponseListener = new ResponseListener<List<Long>>() {
        @Override
        public void onSuccess(List<Long> list) {

            if(sotries != null &&sotries.size() !=0){
                List<UpdateStory> updateStoryList = new ArrayList<>();
                   for(int i=0;i<Math.min(list.size(),sotries.size());i++){
                        if(sotries.get(i).getId() != list.get(i)){
                            updateStoryList.add(new UpdateStory(sotries.get(i),i,list.get(i)));
                        }
                   }
                SotryDao.updateNewList(updateStoryList);

            }else {
                int i = 0;
                ArrayList<Long> res = new ArrayList<>();
                for (; i < Math.min(15, list.size()); i++) {
                    res.add(list.get(i));
                }

                BatchRequest.getAllStoriesForIds(res, new StoryFetchListener() {
                    @Override
                    public void onStoryFetched() {

                    }
                });

                ArrayList<Long> rem = new ArrayList<>();
                for (; i < list.size(); i++) {
                    rem.add(list.get(i));
                }
                addToDb(rem, false);
            }

        }

        @Override
        public void onError(Exception ex) {

        }
    };

    private void fetchFromNetwork() {
        apiService.getStoryIds(storiesResponseListener);
    }

    public static void fetchMore(final StoryFetchListener storyFetchListener) {

        final RealmResults<Stories> result = Realm.getInstance(App.getConfig())
                .where(Stories.class).equalTo("isFetched", false).findAllAsync();
        result.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Stories>>() {
            @Override
            public void onChange(RealmResults<Stories> collection, OrderedCollectionChangeSet changeSet) {
                result.removeAllChangeListeners();
                if(collection.size() == 0){
                    storyFetchListener.onStoryFetched();
                    return;
                }
                List<Long> req = new ArrayList<Long>();
                for (int i = 0; i < Math.min(8, collection.size()); i++) {
                    req.add(collection.get(i).getId());
                }
                BatchRequest.getAllStoriesForIds(req, storyFetchListener);
            }
        });
    }

    public RecyclerView.OnScrollListener scrollListener = new  RecyclerView.OnScrollListener() {

        private boolean loading;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleItems = layoutManager.getChildCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            int totalItems = layoutManager.getItemCount();

            if(dy>0 && !loading && visibleItems + firstVisibleItem >= totalItems){
                loading = true;
                fetchMore(new StoryController.StoryFetchListener() {
                    @Override
                    public void onStoryFetched() {
                        loading = false;
                    }
                });
            }
        }
    };

    public static void addToDb(final List<Long> list, final boolean isFetched) {
        Realm.getInstance(App.getConfig()).executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Long id : list) {
                    Stories stories = realm.where(Stories.class).equalTo("id", id).findFirst();
                    if (stories == null) {
                        stories = realm.createObject(Stories.class, id);
                        stories.setFetched(false);
                    }
                }
            }
        });
    }

    public interface StoryFetchListener {
        void onStoryFetched();
    }

}
