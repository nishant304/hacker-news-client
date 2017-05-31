package com.hn.nishant.nvhn.controller;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.controller.interfaces.IStoryCateogry;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.ResponseListener;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by nishant on 29.05.17.
 */

public class AskStoryImpl implements IStoryCateogry {

    private String category = "askCategory";

    @Override
    public RealmResults<Story> getLocalStories() {
        return StoryDao.getStoriesSortedByRank(category);
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void getLatestStories(ResponseListener<List<Long>> responseListener) {
        App.getApiService().getStoryIds(responseListener,"askstories");
    }
}
