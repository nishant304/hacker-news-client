package com.hn.nishant.nvhn.network;

import android.support.annotation.NonNull;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;

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

    private  int reqToComplete;

    private int i =0;

    static long prevReqTime = 0;

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
        this.reqToComplete = reqCount;
        if(prevReqTime != 0 && System.currentTimeMillis() - prevReqTime <1000){
            suggestedReqCount += 5;
            suggestedReqCount = Math.min(20,suggestedReqCount);
        }
        prevReqTime = System.currentTimeMillis();
    }

    public void start() {
        for (   ; i < Math.min(reqCount,reqCount); i++) {
            placeSingleReq(apiService, resp, i);
        }
    }

    private ResponseListener<T> resp = new ResponseListener<T>() {
        @Override
        public void onSuccess(T t) {
            reqToComplete--;
            if(i< reqCount ){
                placeSingleReq(apiService, resp, i++);
            }
            onSingleItemFetched(t);
            responses.add(t);
            System.out.println("req completed "+reqToComplete);
            if (reqToComplete == 0) {
                jobCompleteListener.onJobComplete(responses);
                adjustSuggestedReqCount();
            }
        }

        @Override
        public void onError(Exception ex) {
            reqToComplete--;
            if(i< reqCount ){
                placeSingleReq(apiService, resp, i++);
            }
            if (reqToComplete == 0) {
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
        if (duration < 300) {
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

    public int getReqCount() {
        return reqCount;
    }

    public void setReqCount(int reqCount) {
        this.reqCount = reqCount;
    }

    public int getReqToComplete() {
        return reqToComplete;
    }

    public void setReqToComplete(int reqToComplete) {
        this.reqToComplete = reqToComplete;
    }


}


