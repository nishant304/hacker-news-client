package com.hn.nishant.nvhn;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.network.FireBaseImpl;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nishant on 15.03.17.
 */

public class App extends Application {

    private static final String BASE_URL = "https://hacker-news.firebaseio.com";

    private static Retrofit retrofit;

    private static RealmConfiguration config;

    private static ApiService apiService;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        Realm.init(this);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded()
                .build();

        apiService = FireBaseImpl.getInstance();
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static RealmConfiguration getConfig() {
        if(config == null){
            config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded()
                    .build();
        }
        return config;
    }

    public static ApiService getApiService() {
        return apiService;
    }

    public static Realm getRealm() {
        return Realm.getInstance(getConfig());
    }

}
