package com.propertyguru.nishant.nvpropertyguru.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.propertyguru.nishant.nvpropertyguru.App;
import com.propertyguru.nishant.nvpropertyguru.dao.StoryDao;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.network.AbstractBatchRequest;
import com.propertyguru.nishant.nvpropertyguru.network.BatchRequest;
import com.propertyguru.nishant.nvpropertyguru.util.RealmInteger;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by nishant on 20.03.17.
 */

public class CommentsController extends Fragment {

    private static final String TAG = CommentsController.class.getSimpleName();

    private CommentLoadListener commentLoadListener;

    public static CommentsController getInstance(FragmentManager fragmentManager,int id){
        CommentsController commentsController = (CommentsController) fragmentManager.findFragmentByTag(TAG);
        if(commentsController == null){
            commentsController = new CommentsController();
            Bundle bundle = new Bundle();
            bundle.putInt("id",id);
            commentsController.setArguments(bundle);
            fragmentManager
                    .beginTransaction()
                    .add(commentsController,TAG)
                    .commit();
        }
        return  commentsController;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof CommentLoadListener){
            commentLoadListener = (CommentLoadListener)context;
        }else{
            throw new IllegalStateException("activity must implement commentloadlistener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getRealm().where(Story.class)
                .equalTo("type","comment").equalTo("parent",Long.valueOf(getArguments().getInt("id"))).findAllAsync().addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Story>>() {
            @Override
            public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
                collection.removeChangeListener(this);
                commentLoadListener.onCommentsLoaded(collection);
                if(collection.size() == 0){
                    fetchComments();
                }
            }
        });
    }

    private void fetchComments(){
        Story story =  App.getRealm().where(Story.class).equalTo("id",getArguments().getInt("id")).findFirst();
        RealmList<RealmInteger> commentsId = story.getKid();
        List<Long> list = new ArrayList<>();
        List<Integer> ranks = new ArrayList<>();
        for(RealmInteger realmInteger : commentsId){
            list.add(realmInteger.getValue());
            ranks.add(realmInteger.getValue().intValue());
        }
        new BatchRequest(list, new AbstractBatchRequest.JobCompleteListener<Story>() {
            @Override
            public void onJobComplete(List<Story> response) {
                StoryDao.addnewData(response);
            }
        },ranks).start();
    }

    public interface CommentLoadListener{
        void onCommentsLoaded(RealmResults<Story> stories);
    }
}
