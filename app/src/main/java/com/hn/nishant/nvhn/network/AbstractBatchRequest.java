package com.hn.nishant.nvhn.network;

import android.support.annotation.NonNull;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.model.Story;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nishant on 18.03.17.
 */

public abstract class AbstractBatchRequest<T> {

    private JobCompleteListener jobCompleteListener;

    private ApiService apiService = App.getApiService();

    private List<T> responses = new ArrayList<>();

    private int reqCount;

    private static int suggestedReqCount = 12;

    private HashMap<Integer,Boolean> reqTracker = new HashMap<>();

    private long startTime;

    private  int reqCompleted ;

    private int i =0;

    public AbstractBatchRequest(@NonNull JobCompleteListener jobCompleteListener, int reqCount) {
        if (reqCount < 0) {
            throw new IllegalArgumentException("req count should not be less than zero");
        }

        if (reqCount == 0) {
            jobCompleteListener.onJobComplete(responses);
        }

        this.startTime = System.currentTimeMillis();
        this.jobCompleteListener = jobCompleteListener;
        this.reqCount = reqCount;
        this.reqCompleted = reqCount;
    }

    public void start() {
        for (   ; i < reqCount; i++) {
            placeSingleReq(apiService, resp, i);
        }
    }

    private ResponseListener<T> resp = new ResponseListener<T>() {
        @Override
        public void onSuccess(T t) {
            reqCompleted--;
            if(i< reqCount ){
                placeSingleReq(apiService, resp, i++);
            }
            onSingleItemFetched(t);
            responses.add(t);
            if (reqCompleted == 0) {
                jobCompleteListener.onJobComplete(responses);
                adjustSuggestedReqCount();
            }
        }

        @Override
        public void onError(Exception ex) {
            reqCompleted--;
            if(i< reqCount ){
                placeSingleReq(apiService, resp, i++);
            }
            if (reqCompleted == 0) {
                jobCompleteListener.onJobComplete(responses);
                adjustSuggestedReqCount();
            }
        }
    };

    protected void onSingleItemFetched(T t) {

    }

    protected abstract void placeSingleReq(ApiService apiService, ResponseListener<T> responseListener, int position);

    public interface JobCompleteListener<T> {
        void onJobComplete(List<T> response);
    }

    private void adjustSuggestedReqCount() {
        long duration = System.currentTimeMillis() - startTime;
        if (duration < 1000) {
            suggestedReqCount++;
        } else {
            suggestedReqCount--;
        }
        suggestedReqCount = Math.min(20, suggestedReqCount);
        suggestedReqCount = Math.max(5, suggestedReqCount);
    }

    public static int getSuggestedReqCount() {
        return suggestedReqCount;
    }
}


