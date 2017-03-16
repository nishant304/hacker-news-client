package com.propertyguru.nishant.nvpropertyguru.controller;

import com.propertyguru.nishant.nvpropertyguru.App;
import com.propertyguru.nishant.nvpropertyguru.BatchRequest;
import com.propertyguru.nishant.nvpropertyguru.api.RetorfitApiService;
import com.propertyguru.nishant.nvpropertyguru.model.Stories;
import com.propertyguru.nishant.nvpropertyguru.model.Story;

import java.util.ArrayList;
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

public class StoryController {

    static CountDownLatch latch ;

    public static void fetchOnCreate(){
        RealmResults<Stories> result = Realm.getInstance(App.getConfig())
                .where(Stories.class).equalTo("isFetched",true).findAllAsync();
        result.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Stories>>() {
            @Override
            public void onChange(RealmResults<Stories> collection, OrderedCollectionChangeSet changeSet) {
                if(collection.size() ==0) {
                    fetchFromNetwork();
                }
            }
        });
    }

    public static void fetchLatest(){
        fetchFromNetwork();
    }


    private static void fetchFromNetwork(){
        final RetorfitApiService retorfitApiService = App.getRetrofit().create(RetorfitApiService.class);
        Call<List<Integer>> call =  retorfitApiService.getStoryIds();
        call.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                ArrayList<Integer> list = (ArrayList<Integer>) response.body();
                int  i =0;
                for( ;i<Math.min(15,list.size());i++){
                    getStoryForId(list.get(i),new CountDownLatch(1));
                }

                for( ;i<list.size();i++){
                    addToDb(list.get(i),false);
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {

            }
        });
    }

    public  static  void fetchMore(final StoryFetchListener storyFetchListener){

        final RealmResults<Stories> result = Realm.getInstance(App.getConfig())
                .where(Stories.class).equalTo("isFetched",false).findAllAsync();
        result.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Stories>>() {
            @Override
            public void onChange(RealmResults<Stories> collection, OrderedCollectionChangeSet changeSet) {
                result.removeAllChangeListeners();
                List<Integer> req = new ArrayList<Integer>();
                for(int i =0;i< Math.min(15,collection.size());i++){
                    req.add(collection.get(i).getId());
                }
                BatchRequest.getAllStoriesForIds(req,storyFetchListener);
            }
        });
    }

    public  static void addToDb(final int id,final boolean isFetched){
        Realm.getInstance(App.getConfig()).executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Stories stories = realm.where(Stories.class).equalTo("id",id).findFirst();
                if(stories == null) {
                    stories = realm.createObject(Stories.class,id);
                }
                stories.setFetched(isFetched);
            }
        });
    }

    private static void getStoryForId(final int id, final CountDownLatch latch){
        RetorfitApiService retorfitApiService = App.getRetrofit().create(RetorfitApiService.class);
        Call<Story> storyCall = retorfitApiService.getStory(id);
        storyCall.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, final Response<Story> response) {
                addToDb(id,true);

                Realm.getInstance(App.getConfig()).executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            realm.copyToRealm((Story) response.body());
                            latch.countDown();
                        }catch (Exception e){
                            latch.countDown();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                addToDb(id,false);
                latch.countDown();
            }
        });
    }

    public interface StoryFetchListener{
        void onStoryFetched();
    }

}
