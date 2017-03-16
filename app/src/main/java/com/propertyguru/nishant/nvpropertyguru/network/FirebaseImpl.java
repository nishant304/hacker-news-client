package com.propertyguru.nishant.nvpropertyguru.network;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.propertyguru.nishant.nvpropertyguru.api.FirebaseApiService;
import com.propertyguru.nishant.nvpropertyguru.api.RetorfitApiService;
import com.propertyguru.nishant.nvpropertyguru.model.Story;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Path;

/**
 * Created by nishant on 16.03.17.
 */

public class FirebaseImpl implements FirebaseApiService {

    private static final String BASE_URL = "https://hacker-news.firebaseio.com";

    @Override
    public void getStoryIds(final ResponseListener<List<Integer>> listener) {

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference(BASE_URL);
        firebaseDatabase.child("v0").child("topstories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Integer> list = (ArrayList<Integer>) dataSnapshot.getValue();
                listener.onSuccess(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(new Exception(databaseError.getMessage()));
            }
        });
    }

    @Override
    public void getStory(int id, ResponseListener<Story> responseListener) {

    }

}
