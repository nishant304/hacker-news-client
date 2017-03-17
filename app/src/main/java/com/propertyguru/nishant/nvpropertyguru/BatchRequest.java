package com.propertyguru.nishant.nvpropertyguru;

import android.os.Looper;
import android.os.Process;

import com.propertyguru.nishant.nvpropertyguru.api.RetorfitApiService;
import com.propertyguru.nishant.nvpropertyguru.controller.StoryController;
import com.propertyguru.nishant.nvpropertyguru.model.Stories;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.network.FireBaseImpl;
import com.propertyguru.nishant.nvpropertyguru.network.ResponseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

/**
 * Created by nishant on 16.03.17.
 */

public class BatchRequest<T> {

    private int reqCount;

    private Object lock = new Object();

    private ArrayList<Story> response = new ArrayList<>();

    private StoryController.StoryFetchListener storyFetchListener;

    private List<Long> requests;

    public BatchRequest(List<Long> list, StoryController.StoryFetchListener storyFetchListener) {
        reqCount = list.size();
        requests = list;
        Collections.sort(requests);
        this.storyFetchListener = storyFetchListener;
        for (Long id : list) {
            FireBaseImpl.getInstance().getStory(id, resp);
        }
    }

    private ResponseListener<Story> resp = new ResponseListener<Story>() {
        @Override
        public void onSuccess(final Story s) {
            reqCount--;
            System.out.println("req coutn"+reqCount);
            response.add(s);
            if (reqCount == 0) {
                commit();
                storyFetchListener.onStoryFetched();
            }
        }

        @Override
        public void onError(Exception ex) {
            reqCount--;
            System.out.println("req coutn"+reqCount);
            if (reqCount == 0) {
                commit();
                storyFetchListener.onStoryFetched();
            }

        }
    };

    private void commit() {
        addToDb();
        Realm.getInstance(App.getConfig()).executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < response.size(); i++) {
                    try {
                        realm.copyToRealm(response.get(i));
                    } catch (Exception e) {

                    }
                    System.out.println("data changed from copy");
                }
            }
        });
    }

    public static BatchRequest<Story> getAllStoriesForIds(List<Long> list, StoryController.StoryFetchListener storyFetchListener) {
        return new BatchRequest<Story>(list, storyFetchListener);
    }

    public void addToDb() {

        Realm.getInstance(App.getConfig()).executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                HashMap<Long, Boolean> hm = new HashMap<Long, Boolean>();
                for (Story s : response) {
                    Integer id = s.getId();
                    Stories stories = realm.where(Stories.class).equalTo("id", id).findFirst();
                    if (stories == null) {
                        stories = realm.createObject(Stories.class, id);
                    }
                    stories.setFetched(true);
                }
            }
        });
    }

}
