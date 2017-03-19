package com.propertyguru.nishant.nvpropertyguru.network;

import android.support.annotation.NonNull;

import com.propertyguru.nishant.nvpropertyguru.App;
import com.propertyguru.nishant.nvpropertyguru.api.ApiService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 18.03.17.
 */

public abstract class AbstractBatchRequest<T> {

    private JobCompleteListener jobCompleteListener;

    private ApiService apiService;

    private List<T> responses = new ArrayList<>();

    private int reqCount;

    public AbstractBatchRequest(@NonNull  JobCompleteListener jobCompleteListener, int reqCount){
        if(reqCount < 0){
            throw new IllegalArgumentException("req count should not be less than zero");
        }

        if(reqCount  == 0){
            jobCompleteListener.onJobComplete(responses);
        }

        this.jobCompleteListener = jobCompleteListener;
        this.apiService = App.getApiService();
        this.reqCount = reqCount;
    }

    public void start(){
        for(int i=0;i<reqCount;i++) {
            placeSingleReq(apiService, resp,i);
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
            }
        }

        @Override
        public void onError(Exception ex) {
            reqCount--;
            if (reqCount == 0) {
                jobCompleteListener.onJobComplete(responses);
            }
        }
    };

    protected void onSingleItemFetched(T t){

    }

    protected  abstract void placeSingleReq(ApiService apiService, ResponseListener<T> responseListener,int position);

    public  interface JobCompleteListener<T>{
        void onJobComplete(List<T> response);
    }
}


