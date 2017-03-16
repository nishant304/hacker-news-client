package com.propertyguru.nishant.nvpropertyguru;

import com.propertyguru.nishant.nvpropertyguru.api.RetorfitApiService;
import com.propertyguru.nishant.nvpropertyguru.controller.StoryController;
import com.propertyguru.nishant.nvpropertyguru.model.Stories;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.network.FirebaseImpl;
import com.propertyguru.nishant.nvpropertyguru.network.ResponseListener;

import java.util.ArrayList;
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


    private ArrayList<Story> response = new ArrayList<>();

    private StoryController.StoryFetchListener storyFetchListener;

    public BatchRequest(List<Integer> list, StoryController.StoryFetchListener storyFetchListener){
        RetorfitApiService retorfitApiService = App.getRetrofit().create(RetorfitApiService.class);
        reqCount = list.size();
        this.storyFetchListener = storyFetchListener;
        for(Integer id:list) {
            FirebaseImpl.getFirebase().getStory(id,resp);
        }
    }

    private ResponseListener<Story> resp = new ResponseListener<Story>() {
        @Override
        public void onSuccess(final Story s) {
            addToDb(s.getId(), true);
            synchronized (lock) {
                reqCount--;
                if (reqCount == 0) {
                    commit();
                    storyFetchListener.onStoryFetched();
                }else{
                   response.add(s);
                }
            }
        }

        @Override
        public void onError(Exception ex) {
                synchronized (lock){
                    reqCount--;
                    if(reqCount == 0){
                        commit();
                        storyFetchListener.onStoryFetched();
                    }
                }
        }
    };

    private  void commit(){
        Realm realm = Realm.getInstance(App.getConfig());
        realm.beginTransaction();
        for(int i=0;i<response.size();i++){
            realm.copyToRealm(response.get(i));
            System.out.println("data changed from copy");
        }
        realm.commitTransaction();
    }

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
