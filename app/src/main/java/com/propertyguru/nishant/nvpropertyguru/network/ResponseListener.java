package com.propertyguru.nishant.nvpropertyguru.network;

/**
 * Created by nishant on 16.03.17.
 */

public interface ResponseListener<T> {

    void onSuccess(T t);

    void onError(Exception ex);
}
