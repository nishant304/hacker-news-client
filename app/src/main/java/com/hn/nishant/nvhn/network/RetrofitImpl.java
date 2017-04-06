package com.hn.nishant.nvhn.network;

import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.api.RetorfitApiService;
import com.hn.nishant.nvhn.model.Story;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nishant on 17.03.17.
 */

public class RetrofitImpl implements ApiService {

    private RetorfitApiService retorfitApiService;

    private RetrofitImpl retrofitImpl = new RetrofitImpl();

    private RetrofitImpl() {
        retorfitApiService = App.getRetrofit().create(RetorfitApiService.class);
    }

    @Override
    public void getStory(long id, final ResponseListener<Story> responseListener) {
        Call<Story> storyCall = retorfitApiService.getStory(id);
        storyCall.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {
                responseListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                responseListener.onError(new Exception(t.getMessage()));
            }
        });
    }

    @Override
    public void getStoryIds(final ResponseListener<List<Long>> responseListener) {
        Call<List<Long>> call = retorfitApiService.getStoryIds();
        call.enqueue(new Callback<List<Long>>() {
            @Override
            public void onResponse(Call<List<Long>> call, Response<List<Long>> response) {
                responseListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Long>> call, Throwable t) {
                responseListener.onError(new Exception(t.getMessage()));
            }
        });
    }

    @Override
    public void getUpdates(ResponseListener<List<Long>> responseListener) {

    }

    public RetrofitImpl getRetrofitImpl() {
        return retrofitImpl;
    }

}
