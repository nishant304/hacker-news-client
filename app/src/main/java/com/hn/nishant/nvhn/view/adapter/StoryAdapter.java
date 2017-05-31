package com.hn.nishant.nvhn.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hn.nishant.nvhn.R;
import com.hn.nishant.nvhn.dao.StoryDao;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.network.FireBaseImpl;
import com.hn.nishant.nvhn.view.activity.BrowseActivity;
import com.hn.nishant.nvhn.view.activity.CommentsActivty;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/**
 * Created by nishant on 15.03.17.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryHolder>
        implements OrderedRealmCollectionChangeListener<RealmResults<Story>> {

    private LayoutInflater inflater;

    protected RealmResults<Story> itemList;

    private ArrayMap<Integer, Integer> changeTracker = new ArrayMap<>();
    private ArrayMap<Integer, Integer> posTracker = new ArrayMap<>();

    public StoryAdapter(Context context, RealmResults<Story> itemList) {
        inflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.itemList.addChangeListener(this);
    }

    public void setNewData(RealmResults<Story> itemList){
        this.itemList = itemList;
        this.itemList.addChangeListener(this);
    }

    @Override
    public StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {
            return new ProgressBarHolder(inflater.inflate(R.layout.progress_layout, parent, false));
        }
        return new StoryHolder(inflater.inflate(R.layout.story_item_view, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getId() == StoryDao.DEFALUT_ITEM_ID ? 2 : 1;
    }

    @Override
    public void onBindViewHolder(StoryHolder holder, int position, List<Object> payloads) {
        if (payloads.size() == 0) {
            onBindViewHolder(holder, position);
        } else {
            holder.comments.setText((Integer) payloads.get(0) + "");
        }
    }

    @Override
    public void onBindViewHolder(final StoryHolder holder, int position) {
        if (holder instanceof ProgressBarHolder) {
            return;
        }
        Story story = itemList.get(position);
        holder.text.setText(itemList.get(position).getTitle());
        holder.time.setText(FireBaseImpl.getTimeDiff(story.getTime()) +" ago by " +
                itemList.get(position).getBy() +" with "+itemList.get(position).getScore()+" points");
        holder.comments.setText(itemList.get(position).getDescendants()+ "");
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class StoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text;
        TextView time;
        public TextView comments;

        StoryHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.article_title);
            time = (TextView) view.findViewById(R.id.article_time);
            comments = (TextView) view.findViewById(R.id.article_comments);
            if (comments != null) {
                comments.setOnClickListener(this);
            }
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.storyItemView){
                EventBus.getDefault().post(getItemAtPosition(getAdapterPosition()));
                return;
            }
            Intent intent = new Intent(v.getContext(),CommentsActivty.class);
            intent.putExtra("storyId", getItemAtPosition(getAdapterPosition()).getId());
            v.getContext().startActivity(intent);
        }
    }

    class ProgressBarHolder extends StoryHolder {

        ProgressBar progressBar;

        ProgressBarHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
        }
    }

    protected LayoutInflater getInflater() {
        return inflater;
    }

    protected Story getItemAtPosition(int pos) {
        return itemList.get(pos);
    }

    @Override
    public void onChange(final RealmResults<Story> collection, final OrderedCollectionChangeSet changeSet) {
        if (changeSet != null) {

            for (int i = changeSet.getDeletionRanges().length - 1; i >= 0; i--) {
                notifyItemRangeRemoved(changeSet.getDeletionRanges()[i].startIndex,
                        changeSet.getDeletionRanges()[i].length);
            }

            for (int i = 0; i < changeSet.getInsertionRanges().length; i++) {
                notifyItemRangeInserted(changeSet.getInsertionRanges()[i].startIndex,
                        changeSet.getInsertionRanges()[i].length);
            }

            for (int i = 0; i < changeSet.getChangeRanges().length; i++) {
                for (int j = changeSet.getChangeRanges()[i].startIndex; j < changeSet.getChangeRanges()[i].startIndex +
                        changeSet.getChangeRanges()[i].length && j <posTracker.size(); j++) {
                    System.out.println("index " +j + " pos tracker" +posTracker.size());
                    if (posTracker.get(j).intValue() == collection.get(j).getId()) {
                        if (isChanged(collection.get(j).getId(), collection.get(j).getDescendants())) {
                            notifyItemChanged(j, collection.get(j).getDescendants());
                        }
                    } else {
                        notifyItemChanged(j);
                    }
                }
            }

        } else {
            notifyDataSetChanged();
        }
        updateChangeTracker();
    }

    public void onDestroy() {
        itemList.removeChangeListener(this);
    }

    private void updateChangeTracker() {
        changeTracker.clear();
        posTracker.clear();
        for (int i = 0; i < itemList.size(); i++) {
            changeTracker.put(itemList.get(i).getId(), itemList.get(i).getDescendants());
            posTracker.put(i, itemList.get(i).getId());
        }
    }

    private boolean isChanged(int id, int newVal) {
        if (changeTracker.get(id) == null) {
            return true;
        }
        return changeTracker.get(id).intValue() != newVal;
    }

}

