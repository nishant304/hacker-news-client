package com.propertyguru.nishant.nvpropertyguru.view.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.propertyguru.nishant.nvpropertyguru.R;

/**
 * Created by nishant on 16.03.17.
 */

public class StoryFragment extends Fragment {

    private static final String TAG = StoryFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story_view,container,false);
        return view;
    }
}
