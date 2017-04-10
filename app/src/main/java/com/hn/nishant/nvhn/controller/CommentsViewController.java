package com.hn.nishant.nvhn.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.AbstractBatchRequest;
import com.hn.nishant.nvhn.network.StoryBatchRequest;
import com.hn.nishant.nvhn.util.RealmInteger;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by nishant on 20.03.17.
 */

public class CommentsViewController extends Fragment {

    private static final String TAG = CommentsViewController.class.getSimpleName();

    public static CommentsViewController getInstance(FragmentManager fragmentManager, int id) {
        CommentsViewController commentsViewController = (CommentsViewController) fragmentManager.findFragmentByTag(TAG);
        if (commentsViewController == null) {
            commentsViewController = new CommentsViewController();
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            commentsViewController.setArguments(bundle);
            fragmentManager
                    .beginTransaction()
                    .add(commentsViewController, TAG)
                    .commit();
        }
        return commentsViewController;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNewComments();
    }

    public RealmResults<Story> getAllComments(int id) {
        return StoryDao.getAllComments(id);
    }

    private void getNewComments() {
        Story story = StoryDao.getStoryForId(getArguments().getInt("id"), App.getRealm());
        RealmList<RealmInteger> commentsId = story.getKid();
        List<Long> list = new ArrayList<>();
        List<Integer> ranks = new ArrayList<>();
        for (RealmInteger realmInteger : commentsId) {
            list.add(realmInteger.getValue());
            ranks.add(realmInteger.getValue().intValue());
        }
        new StoryBatchRequest(list,ranks,"newCategory", new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addnewData(response,false, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                    }
                });
            }
        }).start();
    }

}
