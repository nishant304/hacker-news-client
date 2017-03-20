package com.propertyguru.nishant.nvpropertyguru.view.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.propertyguru.nishant.nvpropertyguru.R;
import com.propertyguru.nishant.nvpropertyguru.controller.CommentsController;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.view.adapter.CommentsAdapter;

import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by nishant on 20.03.17.
 */

public class CommentsActivty extends BaseActivity implements CommentsController.CommentLoadListener {

    private CommentsController commentsController;

    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        int id = getIntent().getIntExtra("storyId",0);
        if(id == 0){
            return;
        }
        commentsController = CommentsController.getInstance(getFragmentManager(),id);
    }

    @Override
    public void onCommentsLoaded(RealmResults<Story> stories) {
        CommentsAdapter commentsAdapter = new CommentsAdapter(this,stories);
        recyclerView.setAdapter(commentsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivty.this));
    }
}
