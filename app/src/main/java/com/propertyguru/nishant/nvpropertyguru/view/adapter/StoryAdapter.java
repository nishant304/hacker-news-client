package com.propertyguru.nishant.nvpropertyguru.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.propertyguru.nishant.nvpropertyguru.R;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.view.activity.CommentsActivty;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by nishant on 15.03.17.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryHolder> implements OrderedRealmCollectionChangeListener<RealmResults<Story>> {

    private LayoutInflater inflater;

    private RealmResults<Story> itemList;

    public StoryAdapter(Context context, RealmResults<Story> itemList){
        inflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.itemList.addChangeListener(this);
    }

    @Override
    public StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StoryHolder(inflater.inflate(R.layout.story_item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(StoryHolder holder, final int position) {
        holder.text.setText(itemList.get(position).getTitle());
        holder.time.setText("by " +itemList.get(position).getBy());
        holder.comments.setText(itemList.get(position).getDescendants()+" comments");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CommentsActivty.class);
                intent.putExtra("storyId",itemList.get(position).getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class StoryHolder extends RecyclerView.ViewHolder{

        TextView text ;
        TextView time ;
        TextView comments ;
        TextView score ;

        StoryHolder(View view){
            super(view);
            text = (TextView) view.findViewById(R.id.article_title);
            time = (TextView) view.findViewById(R.id.article_time);
            comments = (TextView) view.findViewById(R.id.article_comments);
            score = (TextView) view.findViewById(R.id.article_score);
        }
    }

    protected LayoutInflater getInflater() {
        return inflater;
    }

    protected  Story  getItemAtPosition(int pos){
        return itemList.get(pos);
    }

    @Override
    public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
        if(changeSet != null && changeSet.getInsertionRanges().length>0){
            notifyItemRangeInserted(changeSet.getInsertionRanges()[0].startIndex,
                    changeSet.getInsertionRanges()[0].length);
        }else{
            notifyDataSetChanged();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        itemList.removeAllChangeListeners();
    }

}

