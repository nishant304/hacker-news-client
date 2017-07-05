package com.hn.nishant.nvhn.view.viewmodels;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by nishant on 04.07.17.
 */

public abstract class RealmLiveData<T extends RealmModel> extends LiveData implements RealmChangeListener<RealmResults<T>> {

    private RealmResults<T> realmResults;

    public RealmLiveData(@NonNull RealmResults<T> realmResults){
        this.realmResults = realmResults;
    }

    @Override
    protected void onActive() {
        this.realmResults.addChangeListener(this);
    }

    @Override
    protected void onInactive() {
        this.realmResults.removeChangeListener(this);
    }

    @Override
    public void onChange(RealmResults<T> element) {
            this.realmResults = element;
            setValue(transform(element));
    }

    public abstract T transform(RealmResults<T> realmResults);

}
