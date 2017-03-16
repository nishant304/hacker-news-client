package com.propertyguru.nishant.nvpropertyguru;

import com.propertyguru.nishant.nvpropertyguru.api.RetorfitApiService;
import com.propertyguru.nishant.nvpropertyguru.controller.StoryController;
import com.propertyguru.nishant.nvpropertyguru.model.Stories;
import com.propertyguru.nishant.nvpropertyguru.model.Story;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nishant on 16.03.17.
 */

public class BatchRequest<T> {

    int reqCount ;
    private Object lock = new Object();

    private StoryController.StoryFetchListener storyFetchListener;

    public BatchRequest(List<Integer> list, StoryController.StoryFetchListener storyFetchListener){
        RetorfitApiService retorfitApiService = App.getRetrofit().create(RetorfitApiService.class);
        reqCount = list.size();
        this.storyFetchListener = storyFetchListener;
        for(Integer id:list) {
            Call<Story> storyCall = retorfitApiService.getStory(id);
            storyCall.enqueue(story);
        }
    }

    private Callback<Story> story = new Callback<Story>() {
        @Override
        public void onResponse(Call<Story> call, Response<Story> response) {
            final Story s = (Story) response.body();
               addToDb(s.getId(),true);
               synchronized (lock){
                   reqCount--;
                   if(reqCount == 0){
                        storyFetchListener.onStoryFetched();
                   }

                   Realm.getInstance(App.getConfig()).executeTransaction(new Realm.Transaction() {
                       @Override
                       public void execute(Realm realm) {
                           try {
                               realm.copyToRealm(s);
                           }catch (Exception e){
                           }
                       }
                   });



               }
        }

        @Override
        public void onFailure(Call<Story> call, Throwable t) {
            synchronized (lock){
                reqCount--;
                if(reqCount == 0){
                    storyFetchListener.onStoryFetched();
                }
            }
        }
    };

    public  static  BatchRequest<Story> getAllStoriesForIds(List<Integer> list, StoryController.StoryFetchListener storyFetchListener){
        return new BatchRequest<Story>(list, storyFetchListener );
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

}
