package com.hn.nishant.nvhn.network;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.model.Story;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nishant on 05.04.17.
 */

public class IntensiveStoryRequest {

    private Deque<Integer> requestQueue = new LinkedList<>();

    private ApiService apiService = App.getApiService();

    private List<Story> storyList = new ArrayList<>();

    public void onNewRequest(List<Integer> ids){

        storyList = new ArrayList<>();

        if(requestQueue.size() == 0) {
            for (Integer id : ids) {
                requestQueue.addFirst(id);
            }
            start();
        }else{
            for (Integer id : ids) {
                requestQueue.addFirst(id);
            }
        }
    }

    private void start(){
        for(int i=0; i< Math.min(7,requestQueue.size());i++){
            apiService.getStory(requestQueue.poll(),resp);
        }
    }

    private ResponseListener<Story> resp = new ResponseListener<Story>() {
        @Override
        public void onSuccess(Story story) {
            storyList.add(story);
            onResponse();
        }

        @Override
        public void onError(Exception ex) {
            onResponse();
        }
    };

    private void onResponse(){
        if(!requestQueue.isEmpty()){
            apiService.getStory(requestQueue.poll(),resp);
        }

        if(requestQueue.isEmpty()){
            StoryDao.addData(new ArrayList<Story>(storyList));
        }
    }

}
