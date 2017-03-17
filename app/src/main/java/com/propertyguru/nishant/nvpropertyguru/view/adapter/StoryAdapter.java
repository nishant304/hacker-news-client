package com.propertyguru.nishant.nvpropertyguru.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.propertyguru.nishant.nvpropertyguru.R;
import com.propertyguru.nishant.nvpropertyguru.model.Story;

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
        View view = inflater.inflate(R.layout.story_item_view,parent,false);
        return new StoryHolder(view);
    }

    @Override
    public void onBindViewHolder(StoryHolder holder, int position) {
        holder.text.setText(itemList.get(position).getTitle());
        //holder.text.setText(itemList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class StoryHolder extends RecyclerView.ViewHolder{

        TextView text ;
        TextView time ;

        StoryHolder(View view){
            super(view);
            text = (TextView) view.findViewById(R.id.storyName);
            time = (TextView) view.findViewById(R.id.storyTime);
        }

    }

    @Override
    public void onChange(RealmResults<Story> collection, OrderedCollectionChangeSet changeSet) {
        if(changeSet != null){
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

