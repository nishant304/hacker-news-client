package com.hn.nishant.nvhn.network;

import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.model.Story;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nishant on 16.03.17.
 */

public class StoryBatchRequest extends AbstractBatchRequest<Story> {

    private List<Long> requests;

    private List<Integer> ranks;

    private HashMap<Integer, Integer> idToRankMapping = new HashMap<>();

    public StoryBatchRequest(List<Long> list, List<Integer> ranks, JobCompleteListener jobCompleteListener) {
        super(jobCompleteListener, list.size());
        this.requests = list;
        this.ranks = ranks;
    }

    @Override
    protected void onSingleItemFetched(Story story) {
        story.setRank(idToRankMapping.get(story.getId()));
    }

    @Override
    protected void placeSingleReq(ApiService apiService, ResponseListener<Story> responseListener, int position) {
        idToRankMapping.put(requests.get(position).intValue(), ranks.get(position));
        apiService.getStory(requests.get(position), responseListener);
    }

}
