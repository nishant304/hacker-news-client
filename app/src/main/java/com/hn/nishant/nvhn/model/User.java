package com.hn.nishant.nvhn.model;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nishant on 26.06.17.
 */

public class User extends RealmObject {

    private String about;

    @PrimaryKey
    private String id;

    private Integer karma;

    private Long created;

    public String getAbout() {
        return about;
    }

    public String getId() {
        return id;
    }

    public Integer getKarma() {
        return karma;
    }


    public Long getCreated() {
        return created;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

}
