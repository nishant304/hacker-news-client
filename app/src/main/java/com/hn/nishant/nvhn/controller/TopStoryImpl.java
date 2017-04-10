package com.hn.nishant.nvhn.controller;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.controller.interfaces.IStoryCateogry;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.ResponseListener;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by nishant on 07.04.17.
 */

public class TopStoryImpl implements IStoryCateogry {

    String category = "topCategory";

    @Override
    public RealmResults<Story> getLocalStories() {
        return StoryDao.getStoriesSortedByRank(category);
    }

    @Override
    public void getLatestStories(ResponseListener<List<Long>> responseListener) {
        App.getApiService().getStoryIds(responseListener,"topstories");
    }

    @Override
    public String getCategory() {
        return category;
    }
}
