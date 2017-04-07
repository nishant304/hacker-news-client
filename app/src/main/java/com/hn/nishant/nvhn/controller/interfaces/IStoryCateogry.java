package com.hn.nishant.nvhn.controller.interfaces;

import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.ResponseListener;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by nishant on 07.04.17.
 */

public interface IStoryCateogry {
    void getLatestStories(ResponseListener<List<Long>> responseListener);

    RealmResults<Story> getLocalStories();
}
