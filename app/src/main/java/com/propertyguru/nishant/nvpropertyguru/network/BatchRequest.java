package com.propertyguru.nishant.nvpropertyguru.network;

import com.propertyguru.nishant.nvpropertyguru.api.ApiService;
import com.propertyguru.nishant.nvpropertyguru.model.Story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nishant on 16.03.17.
 */

public class BatchRequest extends AbstractBatchRequest<Story> {

    private List<Long> requests;

    private List<Integer> ranks;

    private  HashMap<Integer,Integer> hm = new HashMap<>();

    public BatchRequest(List<Long> list, JobCompleteListener jobCompleteListener,List<Integer> ranks) {
        super(jobCompleteListener,list.size());
        this.requests = list;
        this.ranks = ranks;
    }

    @Override
    protected void onSingleItemFetched(Story story) {
        story.setRank(hm.get(story.getId()));
    }

    @Override
    protected void placeSingleReq(ApiService apiService, ResponseListener<Story> responseListener, int position) {
        hm.put(requests.get(position).intValue(),ranks.get(position));
        apiService.getStory(requests.get(position),responseListener);
    }

}
