package com.propertyguru.nishant.nvpropertyguru.dao;

import com.propertyguru.nishant.nvpropertyguru.App;
import com.propertyguru.nishant.nvpropertyguru.model.Story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nishant on 18.03.17.
 */

public class StoryDao {

    public static RealmResults<Story> getStoriesSortedByRank() {
        return App.getRealm().where(Story.class).equalTo("type","story").findAllSortedAsync("rank");
    }

    public static void addAndDelete(final List<Story> response, final List<Integer> ranks,final List<Integer> delete){
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                delData(delete,realm);
                addData(response,realm);
            }
        });
    }

    private static void addData( List<Story> response,Realm realm){
        for (int i = 0; i < response.size(); i++) {
            try {
                Story story = response.get(i);
                realm.copyToRealmOrUpdate(story);
            } catch (Exception e) {

            }
            System.out.println("data changed from copy");
        }
    }

    public  static  void addnewData(final List<Story> response){
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                addData(response,realm);
            }
        });
    }

    public  static void delete(final List<Story> response){
        final ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            list.add(response.get(i).getId());
        }
        App.getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
            delData(list,realm);
            }
        });
    }

    private static void delData(List<Integer> list,Realm realm){
        for (int i = 0; i < list.size(); i++) {
            try {
                Story story = realm.where(Story.class).equalTo("id",list.get(i)).findFirst();
                story.deleteFromRealm();
            } catch (Exception e) {

            }
        }
    }

}
