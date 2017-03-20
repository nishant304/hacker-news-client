package com.propertyguru.nishant.nvpropertyguru.util;

import io.realm.RealmObject;

/**
 * Created by nishant on 19.03.17.
 */


public class RealmInteger extends RealmObject{

    private Long value;

    public RealmInteger(Long value){
        this.value = value;
    }

    public RealmInteger(){

    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

}
