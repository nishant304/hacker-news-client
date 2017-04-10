package com.hn.nishant.nvhn.dao;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.events.UpdateEvent;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.ResponseListener;
import com.hn.nishant.nvhn.util.RealmInteger;
import com.hn.nishant.nvhn.util.StoryObjPool;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by nishant on 18.03.17.
 */

public class StoryDao {

    public static final int DEFALUT_ITEM_ID = 1000;

    public static void addDummy(Realm realm) {
        Story story = new Story();
        story.setRank(DEFALUT_ITEM_ID);
        story.setId(DEFALUT_ITEM_ID);
        story.setBy("me");
        story.setType("story");
        story.setTitle("please");
        realm.copyToRealmOrUpdate(story);
    }

    public static void deleteDummy() {
        Story story = App.getRealm().where(Story.class).equalTo("id", DEFALUT_ITEM_ID).findFirst();
        if (story != null) {
            story.deleteFromRealm();
        }
    }

    public static RealmResults<Story> getStoriesSortedByRank(String cateGoryFieldKey) {
        return App.getRealm().where(Story.class).equalTo("type", "story").equalTo(cateGoryFieldKey,true).findAllSortedAsync("rank");
    }

    public static RealmResults<Story> getAllComments(int id) {
        return App.getRealm().where(Story.class)
                .equalTo("type", "comment")
                .equalTo("parent", Long.valueOf(id))
                .findAllAsync();
    }

    public static Story getStoryForId(int id, Realm realm) {
        return realm.where(Story.class).equalTo("id", id).findFirst();
    }

    public static void addData(final List<Story> list){
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
            }
        });
    }

    public static void addAndDelete(final List<Story> response, final List<Integer> delete, Realm.Transaction.OnSuccess onSuccess) {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                delData(response, realm);
                realm.copyToRealmOrUpdate(response);
                addDummy(realm);
            }
        }, onSuccess);
    }

    /***
     * Sometimes realm is unable to delete references means we have to use update function
     * to avoid primary key constraint failed excpetion
     * @param response
     * @param onSuccess
     */
    public static void addnewData(final List<Story> response, final boolean shouldAdd, Realm.Transaction.OnSuccess onSuccess) {
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                deleteDummy();
                realm.copyToRealmOrUpdate(response);
                if (shouldAdd) {
                    addDummy(realm);
                }
            }
        }, onSuccess);
    }

    private static void delData(List<Story> list, Realm realm) {
        RealmResults<Story> all = realm.where(Story.class).equalTo("type", "story").findAll();
        for (int i = all.size() - 1; i >= 0; i--) {
            try {
                Story story = all.get(i);
                boolean exist = false;
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getId().equals(story.getId())) {
                        exist = true;
                    }
                }
                if (!exist) {
                    all.deleteFromRealm(i);
                }
            } catch (Exception e) {

            }
        }
    }

    public static ResponseListener<List<Long>> updateListener = new ResponseListener<List<Long>>() {
        @Override
        public void onSuccess(final List<Long> list) {
            App.getRealm().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ArrayList<Long> idToUpdate = new ArrayList<>();
                    ArrayList<Integer> ranks = new ArrayList<>();
                    for(Long id :list){
                        Story story  = realm.where(Story.class).equalTo("id",id.intValue()).findFirst();
                        if(story != null){
                            idToUpdate.add(id);
                            ranks.add(story.getRank());
                        }
                    }
                    UpdateEvent updateEvent = new UpdateEvent();
                    updateEvent.setRanks(ranks);
                    updateEvent.setUpdatedItems(idToUpdate);
                    EventBus.getDefault().post(updateEvent);
                }
            });
        }

        @Override
        public void onError(Exception ex) {

        }
    };

}
