package com.hn.nishant.nvhn.network;

import android.support.annotation.NonNull;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 18.03.17.
 */

public abstract class AbstractBatchRequest<T> {

    private JobCompleteListener jobCompleteListener;

    private ApiService apiService = App.getApiService();

    private List<T> responses = new ArrayList<>();

    private int reqCount;

    private static int suggestedReqCount = 10;

    private long startTime;

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
    }

    public void start() {
        for (int i = 0; i < reqCount; i++) {
            placeSingleReq(apiService, resp, i);
        }
    }

    private ResponseListener<T> resp = new ResponseListener<T>() {
        @Override
        public void onSuccess(T t) {
            reqCount--;
            onSingleItemFetched(t);
            responses.add(t);
            if (reqCount == 0) {
                jobCompleteListener.onJobComplete(responses);
                adjustSuggestedReqCount();
            }
        }

        @Override
        public void onError(Exception ex) {
            reqCount--;
            if (reqCount == 0) {
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
        if (duration < 500) {
            suggestedReqCount++;
        } else {
            suggestedReqCount--;
        }
        suggestedReqCount = Math.min(5, suggestedReqCount);
        suggestedReqCount = Math.max(15, suggestedReqCount);
    }

    public static int getSuggestedReqCount() {
        return suggestedReqCount;
    }
}


