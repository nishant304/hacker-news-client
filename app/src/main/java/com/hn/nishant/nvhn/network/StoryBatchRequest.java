package com.hn.nishant.nvhn.network;

import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.model.Story;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nishant on 16.03.17.
 */

public class StoryBatchRequest extends AbstractBatchRequest<Story> {

    private List<Long> requests;

    private List<Integer> ranks;

    private String methodName;

    private HashMap<Integer, Integer> idToRankMapping = new HashMap<>();

    public StoryBatchRequest(List<Long> list, List<Integer> ranks,String category, JobCompleteListener jobCompleteListener) {
        super(jobCompleteListener, list.size());
        this.requests = list;
        this.ranks = ranks;
        StringBuilder stringBuilder = new StringBuilder(category);
        this.methodName = "set"+ stringBuilder.replace(0,1,Character.toUpperCase(category.charAt(0))+"").toString();
    }

    @Override
    protected void onSingleItemFetched(Story story) {
        story.setRank(idToRankMapping.get(story.getId()));
        try {
            Method method = Story.class.getDeclaredMethod(methodName,Boolean.class);
            method.invoke(story, true);
        }catch (Exception e){

        }
    }

    @Override
    protected void placeSingleReq(ApiService apiService, ResponseListener<Story> responseListener, int position) {
        idToRankMapping.put(requests.get(position).intValue(), ranks.get(position));
        apiService.getStory(requests.get(position), responseListener);
    }

}
