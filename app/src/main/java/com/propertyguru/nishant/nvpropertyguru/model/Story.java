package com.propertyguru.nishant.nvpropertyguru.model;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nishant on 15.03.17.
 */

import java.util.List;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Story extends RealmObject  {


    @SerializedName("by")
    @Expose
    private String by;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;


    @SerializedName("parent")
    @Expose
    private Integer parent;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("time")
    @Expose
    private Integer time;

    @SerializedName("type")
    @Expose
    private String type;

    @Exclude
    private Integer rank ;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

