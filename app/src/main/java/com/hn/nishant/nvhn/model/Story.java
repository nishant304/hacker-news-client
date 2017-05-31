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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getParent() {
        return parent;
    }

    @SerializedName("parent")
    @Expose
    private Long parent;


    public Long getParent1() {
        return parent1;
    }

    public void setParent1(Long parent1) {
        this.parent1 = parent1;
    }

    @Expose
    private Long parent1;


    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("time")
    @Expose
    private Long time;

    @SerializedName("type")
    @Expose
    private String type;

    private Integer descendants = 0;

    @Exclude
    private Integer rank;

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    private Long score;

    private Boolean topCategory;

    public Boolean getTopCategory() {
        return topCategory;
    }

    public void setTopCategory(Boolean topCategory) {
        this.topCategory = topCategory;
    }

    public Boolean getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(Boolean newCategory) {
        this.newCategory = newCategory;
    }

    private Boolean newCategory;

    public Boolean getBestCategory() {
        return bestCategory;
    }

    public void setBestCategory(Boolean bestCategory) {
        this.bestCategory = bestCategory;
    }

    private Boolean bestCategory;

    public Boolean getShowCategory() {
        return showCategory;
    }

    public void setShowCategory(Boolean showCategory) {
        this.showCategory = showCategory;
    }

    private Boolean showCategory;

    public Boolean getAskCategory() {
        return askCategory;
    }

    public void setAskCategory(Boolean askCategory) {
        this.askCategory = askCategory;
    }

    private Boolean askCategory;

    public Boolean getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(Boolean jobCategory) {
        this.jobCategory = jobCategory;
    }

    private Boolean jobCategory;



    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
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

