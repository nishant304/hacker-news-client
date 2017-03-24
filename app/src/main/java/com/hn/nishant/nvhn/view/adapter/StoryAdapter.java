package com.hn.nishant.nvhn.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.view.activity.CommentsActivty;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/**
 * Created by nishant on 15.03.17.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryHolder> implements OrderedRealmCollectionChangeListener<RealmResults<Story>> {

    private LayoutInflater inflater;

    private RealmResults<Story> itemList;

    public StoryAdapter(Context context, RealmResults<Story> itemList) {
        inflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.itemList.addChangeListener(this);
    }

    @Override
    public StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StoryHolder(inflater.inflate(R.layout.story_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final StoryHolder holder, int position) {
        holder.text.setText(itemList.get(position).getTitle());
        holder.time.setText("by " + itemList.get(position).getBy());
        holder.comments.setText(itemList.get(position).getDescendants() + " comments");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CommentsActivty.class);
                intent.putExtra("storyId", itemList.get(holder.getAdapterPosition()).getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

   static   class StoryHolder extends RecyclerView.ViewHolder {

        TextView text;
        TextView time;
        TextView comments;
        TextView score;

        StoryHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.article_title);
            time = (TextView) view.findViewById(R.id.article_time);
            comments = (TextView) view.findViewById(R.id.article_comments);
        }
    }

    protected LayoutInflater getInflater() {
        return inflater;
    }

    protected Story getItemAtPosition(int pos) {
        return itemList.get(pos);
    }


    /***
     *
     * @param collection
     * @param changeSet
     * while it is awesome to notify the adapter about items changed it comes at cost of
     * draw and layout perfroamnce cost if there are too many changes specially on refresh,so
     * lets this keep this implementation for insertions only
     */
    @Override
    public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
        if (changeSet != null && changeSet.getInsertionRanges().length == 1
                && changeSet.getDeletionRanges().length == 0
                && changeSet.getChangeRanges().length == 0) {

            for (int i = 0; i < changeSet.getInsertionRanges().length; i++) {
                notifyItemRangeInserted(changeSet.getInsertionRanges()[i].startIndex,
                        changeSet.getInsertionRanges()[i].length);
            }
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        itemList.removeAllChangeListeners();
    }

}

