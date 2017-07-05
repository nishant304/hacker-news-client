package com.hn.nishant.nvhn.view.viewmodels;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.dao.UserDao;
import com.hn.nishant.nvhn.model.User;

/**
 * Created by nishant on 28.06.17.
 */

public class UserDetailViewModel extends ViewModel {

    private UserRepo userRepo = new UserRepo(new UserDao(App.getRealm()));

    private LiveData<User> userLiveData;

    public UserDetailViewModel(@NonNull  String userId){
        userLiveData = userRepo.getUserForId(userId);
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    @Override
    protected void onCleared() {

    }

}
