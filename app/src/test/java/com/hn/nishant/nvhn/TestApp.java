package com.hn.nishant.nvhn;

import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.network.FireBaseImpl;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

import io.realm.Realm;

/**
 * Created by nishant on 22.03.17.
 */

public class TestApp extends App implements TestLifecycleApplication{

    private static ApiService apiService ;

    private static Realm realm;

    @Override
    public void onCreate() {
        PowerMockito.mockStatic(Realm.class);
        realm = PowerMockito.mock(Realm.class);
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

    public static Realm getRealm(){
        return realm;
    }
}
