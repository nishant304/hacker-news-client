package com.hn.nishant.nvhn.view.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.databinding.FragmentUserDetailViewBinding;
import com.hn.nishant.nvhn.model.User;
import com.hn.nishant.nvhn.view.viewmodels.UserDetailModelFactory;
import com.hn.nishant.nvhn.view.viewmodels.UserDetailViewModel;

/**
 * Created by nishant on 27.06.17.
 */

public class UserDetailFragment extends LifecycleFragment {

    private UserDetailViewModel userDetailViewModel;

    private FragmentUserDetailViewBinding fragmentUserDetailViewBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         fragmentUserDetailViewBinding  =FragmentUserDetailViewBinding.inflate(inflater,container,false);
        return fragmentUserDetailViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        userDetailViewModel = ViewModelProviders.of(this, new UserDetailModelFactory(
                getArguments().getString("userId"))).get(UserDetailViewModel.class);
        userDetailViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                fragmentUserDetailViewBinding.setUser(user);
            }
        });
    }

}
