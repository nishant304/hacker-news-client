package com.hn.nishant.nvhn.api;

import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.ResponseListener;

import java.util.List;

/**
 * Created by nishant on 16.03.17.
 */

public interface ApiService {

    void getStoryIds(ResponseListener<List<Long>> responseListener);

    void getStory(long id, ResponseListener<Story> responseListener);

    void getUpdates(ResponseListener<List<Long>> responseListener);

}
