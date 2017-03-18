package com.propertyguru.nishant.nvpropertyguru.model;

import com.propertyguru.nishant.nvpropertyguru.App;

import org.json.JSONArray;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nishant on 15.03.17.
 */

public class Stories extends RealmObject {

    @PrimaryKey
    private Long id;

    private boolean isFetched ;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    private int rank;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isFetched() {
        return isFetched;
    }

    public void setFetched(boolean fetched) {
        isFetched = fetched;
    }

    public static void addToDb(final List<Long> list,final List<Long> ranks,  final boolean isFetched) {
        Realm.getInstance(App.getConfig()).executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i=0;i<list.size();i++) {
                    long id = list.get(i);
                    Stories stories = realm.where(Stories.class).equalTo("id", id).findFirst();
                    if (stories == null) {
                        stories = realm.createObject(Stories.class, id);
                        stories.setRank(ranks.get(i).intValue());
                        stories.setFetched(false);
                    }
                }
            }
        });
    }

}
