package com.hn.nishant.nvhn.view.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by nishant on 04.07.17.
 */

public abstract class NetworkBoundResource<T> {

    private LiveData<T> liveData;

    public NetworkBoundResource() {

        liveData = getFromDb();
        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                if (shouldFetchFromNetwork(t)) {
                    getFromNetwork().subscribe(new Action1<T>() {
                        @Override
                        public void call(T t) {
                            saveInDb(t);
                        }
                    });
                }
            }
        });
    }

    abstract LiveData<T> getFromDb();

    abstract boolean shouldFetchFromNetwork(T t);

    abstract Observable<T> getFromNetwork();

    abstract void saveInDb(T t);

    LiveData<T> asLiveData() {
        return liveData;
    }
}
