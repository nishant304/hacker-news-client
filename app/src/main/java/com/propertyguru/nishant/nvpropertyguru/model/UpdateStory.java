package com.propertyguru.nishant.nvpropertyguru.model;

/**
 * Created by nishant on 18.03.17.
 */

public class UpdateStory {

    private Story story;

    private int rank;

    private int id;

    public UpdateStory(Story story, int rank, int id){
        this.story = story;
        this.id = id;
        this.rank = rank;
    }

}
