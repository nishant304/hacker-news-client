package com.propertyguru.nishant.nvpropertyguru.api;

import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.network.ResponseListener;

import java.util.List;

/**
 * Created by nishant on 16.03.17.
 */

public interface ApiService {

    public void getStoryIds(ResponseListener<List<Long>> responseListener);

    public void getStory(long id, ResponseListener<Story> responseListener);

}
