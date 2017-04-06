package com.hn.nishant.nvhn.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hn.nishant.nvhn.util.RealmInteger;

public class Story extends RealmObject {


    @SerializedName("by")
    @Expose
    private String by = "";

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;


    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getParent() {
        return parent;
    }

    @SerializedName("parent")
    @Expose
    private Long parent;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("time")
    @Expose
    private Integer time;

    @SerializedName("type")
    @Expose
    private String type;

    private Integer descendants = 0;

    @Exclude
    private Integer rank;

    public RealmList<RealmInteger> getKid() {
        return kid;
    }

    public void setKid(RealmList<RealmInteger> kid) {
        this.kid = kid;
    }

    @Exclude
    private RealmList<RealmInteger> kid;

    public Integer getDescendants() {
        return descendants;
    }

    public void setDescendants(Integer descendants) {
        this.descendants = descendants;
    }

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

