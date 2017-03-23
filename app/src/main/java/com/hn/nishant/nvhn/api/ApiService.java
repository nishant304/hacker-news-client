package com.hn.nishant.nvhn.api;

import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.ResponseListener;

import java.util.List;

/**
 * Created by nishant on 16.03.17.
 */

public interface ApiService {

    public void getStoryIds(ResponseListener<List<Long>> responseListener);

    public void getStory(long id, ResponseListener<Story> responseListener);

}
