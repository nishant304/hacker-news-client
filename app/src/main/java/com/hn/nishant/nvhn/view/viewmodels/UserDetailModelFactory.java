package com.hn.nishant.nvhn.view.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

/**
 * Created by nishant on 04.07.17.
 */

public class UserDetailModelFactory implements ViewModelProvider.Factory {

    private String userId;

    public UserDetailModelFactory(String userId){
        this.userId = userId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T)new UserDetailViewModel(userId);
    }

}
