package com.propertyguru.nishant.nvpropertyguru;

import android.app.Application;

import com.google.android.gms.measurement.AppMeasurementReceiver;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.propertyguru.nishant.nvpropertyguru.api.ApiService;
import com.propertyguru.nishant.nvpropertyguru.network.FireBaseImpl;

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

    private static  RealmConfiguration config;

    private static ApiService apiService ;

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

    public  static RealmConfiguration getConfig(){
        return config;
    }

    public static ApiService getApiService() {
        return apiService;
    }

    public static Realm getRealm() {
        return Realm.getInstance(getConfig());
    }

}
