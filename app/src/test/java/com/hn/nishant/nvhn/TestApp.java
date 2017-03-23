package com.hn.nishant.nvhn;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

/**
 * Created by nishant on 22.03.17.
 */

public class TestApp extends App implements TestLifecycleApplication{

    @Override
    public void onCreate() {

    }

    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void afterTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {

    }
}
