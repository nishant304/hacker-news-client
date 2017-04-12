package com.hn.nishant.nvhn.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.controller.CommentsViewController;
import com.hn.nishant.nvhn.view.adapter.CommentsAdapter;

/**
 * Created by nishant on 20.03.17.
 */

public class CommentsActivty extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int id = getIntent().getIntExtra("storyId", 0);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        CommentsViewController commentsViewController = CommentsViewController.getInstance(getFragmentManager(), id);
        CommentsAdapter commentsAdapter = new CommentsAdapter(this, commentsViewController.getAllComments(id));
        recyclerView.setAdapter(commentsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivty.this));
    }

}