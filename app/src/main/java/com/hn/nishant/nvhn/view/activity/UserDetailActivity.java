package com.hn.nishant.nvhn.view.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.databinding.FragmentUserDetailViewBinding;
import com.hn.nishant.nvhn.model.User;
import com.hn.nishant.nvhn.view.viewmodels.UserDetailModelFactory;
import com.hn.nishant.nvhn.view.viewmodels.UserDetailViewModel;

/**
 * Created by nishant on 06.07.17.
 */

public class UserDetailActivity extends LifecycleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentUserDetailViewBinding fragmentUserDetailViewBinding =
                DataBindingUtil.setContentView(this, R.layout.fragment_user_detail_view);
        UserDetailViewModel userDetailViewModel = ViewModelProviders.of(this, new UserDetailModelFactory(
                getIntent().getStringExtra("userId"))).get(UserDetailViewModel.class);
        userDetailViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                fragmentUserDetailViewBinding.setUser(user);
            }
        });
    }

}
