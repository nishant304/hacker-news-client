package com.hn.nishant.nvhn.view.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.view.adapter.StoryAdapter;

/**
 * Created by nishant on 28.03.17.
 */

public class ChangeItemAnimator extends DefaultItemAnimator {

    @Override
    public boolean animateChange(final RecyclerView.ViewHolder oldHolder, final RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        StoryAdapter.StoryHolder storyHolder = (StoryAdapter.StoryHolder)newHolder;
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(storyHolder.itemView, View.ROTATION_X, 0, 90);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(storyHolder.itemView, View.ROTATION_X, 90, 0);
        objectAnimator1.setCurrentPlayTime(1000);
        objectAnimator1.setInterpolator(new AccelerateInterpolator());
        objectAnimator2.setCurrentPlayTime(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchChangeFinished(newHolder,false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.playSequentially(objectAnimator1,objectAnimator2);
        animatorSet.start();
        return true;
    }
}
