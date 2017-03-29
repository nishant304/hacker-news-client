package com.hn.nishant.nvhn.view.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.view.adapter.StoryAdapter;

import java.util.List;

/**
 * Created by nishant on 28.03.17.
 */

public class ChangeItemAnimator extends DefaultItemAnimator {

    private static final int DEFAULT_ANIM_DURATION = 500;

    private AccelerateDecelerateInterpolator interPolator = new AccelerateDecelerateInterpolator();

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @Override
    public ItemHolderInfo obtainHolderInfo() {
        return new CommentsInfo();
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        CommentsInfo commentsInfo = (CommentsInfo) super.recordPostLayoutInformation(state, viewHolder);
        TextView comment =  ((StoryAdapter.StoryHolder) viewHolder).comments;
        if(comment != null) {
            commentsInfo.commentText =  comment.getText().toString();
        }
        return commentsInfo;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        CommentsInfo commentsInfo = (CommentsInfo) super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
        TextView comment =  ((StoryAdapter.StoryHolder) viewHolder).comments;
        if(comment != null) {
            commentsInfo.commentText =  comment.getText().toString();
        }
        return commentsInfo;
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull final RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        if (oldHolder != newHolder) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }

        final StoryAdapter.StoryHolder storyHolder = (StoryAdapter.StoryHolder) newHolder;
        ObjectAnimator normalToReverseAnim = ObjectAnimator.ofFloat(storyHolder.comments, View.ROTATION_X, 0, 90);
        normalToReverseAnim.setInterpolator(interPolator);
        normalToReverseAnim.setDuration(DEFAULT_ANIM_DURATION);

        final String newText = ((CommentsInfo) postInfo).commentText;
        String oldText = ((CommentsInfo) preInfo).commentText;
        ((StoryAdapter.StoryHolder) newHolder).comments.setText(oldText);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(normalToReverseAnim);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ((StoryAdapter.StoryHolder) newHolder).comments.setText(newText);
                playReverseToNormalAnim(storyHolder.comments,newHolder);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
        return true;
    }

    private void playReverseToNormalAnim(TextView comments, final RecyclerView.ViewHolder viewHolder){
        ObjectAnimator reverseToDefault = ObjectAnimator.ofFloat(comments, View.ROTATION_X, 90, 0);
        reverseToDefault.setInterpolator(interPolator);
        reverseToDefault.setDuration(DEFAULT_ANIM_DURATION);

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(reverseToDefault);
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchChangeFinished(viewHolder,false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSet.start();
    }

    private static class CommentsInfo extends ItemHolderInfo {
        String commentText = "";
    }

}
