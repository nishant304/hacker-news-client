package com.propertyguru.nishant.nvpropertyguru.dao;

import com.propertyguru.nishant.nvpropertyguru.App;
import com.propertyguru.nishant.nvpropertyguru.model.Stories;
import com.propertyguru.nishant.nvpropertyguru.model.Story;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nishant on 18.03.17.
 */

public class StoriesDao {

    public static RealmResults<Stories> getStories(){
        return App.getRealm()
                .where(Stories.class).findAllSortedAsync("rank");
    }

    public static void delete(final List<Story> response) {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Story s : response) {
                    Integer id = s.getId();
                    Stories stories = realm.where(Stories.class).equalTo("id", id).findFirst();
                    if (stories != null) {
                        stories.deleteFromRealm();
                    }
                }
            }
        });
    }

    public static void delete() {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Stories.class);
            }
        });
    }

    public static void addToDb(final List<Long> list,final List<Long> ranks) {
        Realm.getInstance(App.getConfig()).executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Stories.class);
                for (int i=0;i<list.size();i++) {
                    long id = list.get(i);
                    Stories stories = realm.createObject(Stories.class, id);
                    stories.setRank(ranks.get(i).intValue());
                }
            }
        });
    }

}
