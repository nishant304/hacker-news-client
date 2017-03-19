package com.propertyguru.nishant.nvpropertyguru.util;

import io.realm.RealmObject;

/**
 * Created by nishant on 19.03.17.
 */

public class RealmInteger extends RealmObject{

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
