package com.hn.nishant.nvhn.network;

import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.util.RealmInteger;

import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by nishant on 11.04.17.
 */

public class CommentsBatchRequest extends AbstractBatchRequest<Story> {

    private List<Long> reqList;

    private HashMap<Integer,Integer> idToRankMapping = new HashMap<>();

    private HashMap<Integer,Integer> idToDepthMapping = new HashMap<>();

    private long parentId;

    public CommentsBatchRequest(List<Long> request,JobCompleteListener<Story> jobCompleteListener,long parentId) {
        super(jobCompleteListener,request.size());
        this.reqList = request;
        this.parentId = parentId;
        for(int i =0; i< reqList.size(); i++){
            idToRankMapping.put(reqList.get(i).intValue(),i);
            idToDepthMapping.put(reqList.get(i).intValue(),0);
        }
    }

    @Override
    protected void onSingleItemFetched(Story story) {
        int rank = idToRankMapping.get(story.getId());
        int depth = idToDepthMapping.get(story.getId());
        story.setRank(rank);
        story.setDepth(depth);
        story.setParent1(parentId);
        RealmList<RealmInteger> kids = story.getKid();
        if(kids == null){
            return;
        }

        for (RealmInteger realmInteger : kids) {
            idToRankMapping.put(realmInteger.getValue().intValue(), Integer.valueOf(rank));
            idToDepthMapping.put(realmInteger.getValue().intValue(),Integer.valueOf(depth+1));
            reqList.add(realmInteger.getValue());
        }
        setReqCount(getReqCount() + kids.size());
        setReqToComplete(getReqToComplete() + kids.size());
    }

    @Override
    protected void placeSingleReq(ApiService apiService, ResponseListener<Story> responseListener, int position) {
        apiService.getStory(reqList.get(position),responseListener);
    }

}
