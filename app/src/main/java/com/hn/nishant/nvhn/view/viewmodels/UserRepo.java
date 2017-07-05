package com.hn.nishant.nvhn.view.viewmodels;

import android.arch.lifecycle.LiveData;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.dao.UserDao;
import com.hn.nishant.nvhn.model.User;
import com.hn.nishant.nvhn.network.ResponseListener;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

/**
 * Created by nishant on 30.06.17.
 */

public class UserRepo {

    private ApiService apiService = App.getApiService();

    private UserDao userDao;

    public UserRepo(UserDao userDao){
        this.userDao = userDao;
    }

    public LiveData<User> getUserForId(final String userId){
        return new NetworkBoundResource<User>(){

            @Override
            Observable<User> getFromNetwork() {
                return apiService.getUserDetail(userId);
            }


            @Override
            void saveInDb(User user) {
                userDao.saveUser(user);
            }

            @Override
            LiveData<User> getFromDb() {
                return userDao.getUserData(userId);
            }

            @Override
            boolean shouldFetchFromNetwork(User user) {
                return user == null;
            }
        }.asLiveData();
    }

}
