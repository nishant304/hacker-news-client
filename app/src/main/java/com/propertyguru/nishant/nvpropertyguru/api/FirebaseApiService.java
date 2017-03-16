package com.propertyguru.nishant.nvpropertyguru.api;

import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.network.ResponseListener;

import java.util.List;

/**
 * Created by nishant on 16.03.17.
 */

public interface FirebaseApiService extends ApiService {

    public void getStoryIds(ResponseListener<List<Integer>> responseListener);

    public void getStory(int id, ResponseListener<Story> responseListener);

}
