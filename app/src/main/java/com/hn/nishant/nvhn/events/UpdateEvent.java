package com.hn.nishant.nvhn.events;

import java.util.List;

/**
 * Created by nishant on 06.04.17.
 */

public class UpdateEvent {

    private List<Long> updatedItems;

    public List<Integer> getRanks() {
        return ranks;
    }

    public void setRanks(List<Integer> ranks) {
        this.ranks = ranks;
    }

    private List<Integer> ranks;

    public List<Long> getUpdatedItems() {
        return updatedItems;
    }

    public void setUpdatedItems(List<Long> updatedItems) {
        this.updatedItems = updatedItems;
    }

}
