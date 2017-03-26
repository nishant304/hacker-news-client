package com.hn.nishant.nvhn.dao;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.model.StoryToFetch;
import com.hn.nishant.nvhn.model.Story;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nishant on 18.03.17.
 */

public class StoryToFetchDao {

    public static RealmResults<StoryToFetch> getStoriesToFetch() {
        return App.getRealm()
                .where(StoryToFetch.class).findAllSortedAsync("rank");
    }

    public static void deleteFromRemainderList(final List<Story> response) {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Story s : response) {
                    Integer id = s.getId();
                    StoryToFetch storyToFetch = realm.where(StoryToFetch.class).equalTo("id", id).findFirst();
                    if (storyToFetch != null) {
                        storyToFetch.deleteFromRealm();
                    }
                }
            }
        });
    }

    public static void deleteFromRemainderList() {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(StoryToFetch.class);
            }
        });
    }

    public static void add(final List<Long> list, Realm.Transaction.OnSuccess onSuccess) {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(StoryToFetch.class);
                for (int i = 0; i < list.size(); i++) {
                    long id = list.get(i);
                    StoryToFetch storyToFetch = realm.createObject(StoryToFetch.class, id);
                    storyToFetch.setRank(i);
                }
            }
        },onSuccess);
    }

}
