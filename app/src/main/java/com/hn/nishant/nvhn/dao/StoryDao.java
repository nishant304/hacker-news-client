package com.hn.nishant.nvhn.dao;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.model.Story;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by nishant on 18.03.17.
 */

public class StoryDao {

    public static final int DEFALUT_ITEM_ID = 1000;

    public static void addDummy(){
        App.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Story story = new Story();
                story.setRank(DEFALUT_ITEM_ID);
                story.setId(DEFALUT_ITEM_ID);
                story.setBy("me");
                story.setType("story");
                story.setTitle("please");
                realm.copyToRealmOrUpdate(story);
                System.out.println("");
            }
        });
    }

    public static void deleteDummy(){
        Story story = App.getRealm().where(Story.class).equalTo("id",DEFALUT_ITEM_ID).findFirst();
        if(story != null){
            story.deleteFromRealm();
        }
    }

    public static RealmResults<Story> getStoriesSortedByRank() {
        return App.getRealm().where(Story.class).equalTo("type", "story").findAllSortedAsync("rank");
    }

    public static RealmResults<Story> getAllComments(int id) {
        return App.getRealm().where(Story.class)
                .equalTo("type", "comment")
                .equalTo("parent", Long.valueOf(id))
                .findAllAsync();
    }

    public static Story getStoryForId(int id,Realm realm) {
        return realm.where(Story.class).equalTo("id", id).findFirst();
    }

    public static void addAndDelete(final List<Story> response, final List<Integer> delete, Realm.Transaction.OnSuccess onSuccess) {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                delData(delete, realm);
                addData(response, realm);
            }
        },onSuccess);
    }

    private static void addData(List<Story> response, Realm realm) {
        for (int i = 0; i < response.size(); i++) {
            try {
                Story story = response.get(i);
                realm.copyToRealmOrUpdate(story);
            } catch (Exception e) {

            }
        }
    }

    public static void addnewData(final List<Story> response, Realm.Transaction.OnSuccess onSuccess) {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                deleteDummy();
                addData(response, realm);
            }
        },onSuccess);
    }

    public static void delete(final List<Story> response) {
        final ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            list.add(response.get(i).getId());
        }
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                delData(list, realm);
            }
        });
    }

    private static void delData(List<Integer> list, Realm realm) {
        for (int i = 0; i < list.size(); i++) {
            try {
                Story story = realm.where(Story.class).equalTo("id", list.get(i)).findFirst();
                story.deleteFromRealm();
            } catch (Exception e) {

            }
        }
    }

}
