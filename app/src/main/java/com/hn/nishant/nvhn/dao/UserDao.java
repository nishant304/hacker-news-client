package com.hn.nishant.nvhn.dao;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.hn.nishant.nvhn.model.User;
import com.hn.nishant.nvhn.view.viewmodels.RealmLiveData;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by nishant on 30.06.17.
 */

public class UserDao {

    private Realm realm;

    public UserDao(@NonNull Realm realm) {
        this.realm = realm;
    }

    public RealmResults<User> getUserForId(String userId) {
        return realm.where(User.class).equalTo("id", userId).findAllAsync();
    }

    public void saveUser(final User user) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(user);
            }
        });
    }

    public LiveData<User> getUserData(String userId) {
        return new RealmLiveData<User>(getUserForId(userId)) {
            @Override
            public User transform(RealmResults<User> realmResults) {
                return realmResults.size() == 0 ? null : realmResults.get(0);
            }
        };
    }

}
