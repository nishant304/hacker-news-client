package com.hn.nishant.nvhn.view.viewmodels;

import android.arch.lifecycle.LiveData;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.dao.UserDao;
import com.hn.nishant.nvhn.model.User;
import com.hn.nishant.nvhn.network.ResponseListener;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by nishant on 29.06.17.
 */

public abstract class UserLiveData extends LiveData<User> implements RealmChangeListener<RealmResults<User>> {

    private RealmResults<User> user;

    private String userId;

    private UserDao userDao;

    public UserLiveData(String userId, UserDao userDao){
            user = userDao.getUserForId(userId);
            user.addChangeListener(this);
        this.userId = userId;
        this.userDao = userDao;
    }

    @Override
    protected void onActive() {
        user.addChangeListener(this);
    }

    @Override
    protected void onInactive() {
        user.removeChangeListener(this);
    }

    @Override
    public void onChange(RealmResults<User> element) {
        this.user = element;
        if(user.size() ==0){
            getFromNetwork(userId);
        }else{
            setValue(user.get(0));
        }
    }

    public abstract void getFromNetwork(String userId);
}
