package com.propertyguru.nishant.nvpropertyguru.api;

import com.propertyguru.nishant.nvpropertyguru.model.Story;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by nishant on 15.03.17.
 */

public interface RetorfitApiService  {

    @GET("v0/topstories.json")
    public Call<List<Integer>> getStoryIds();

    @GET("v0/item/{id}.json")
    public Call<Story> getStory(@Path("id") int id);

}
