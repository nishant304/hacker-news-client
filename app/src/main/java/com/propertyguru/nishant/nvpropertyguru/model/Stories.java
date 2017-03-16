package com.propertyguru.nishant.nvpropertyguru.model;

import org.json.JSONArray;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nishant on 15.03.17.
 */

public class Stories extends RealmObject {

    @PrimaryKey
    private Integer id;

    private boolean isFetched ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isFetched() {
        return isFetched;
    }

    public void setFetched(boolean fetched) {
        isFetched = fetched;
    }

}
