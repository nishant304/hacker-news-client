package com.propertyguru.nishant.nvpropertyguru.network;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.propertyguru.nishant.nvpropertyguru.api.ApiService;
import com.propertyguru.nishant.nvpropertyguru.model.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 16.03.17.
 */

public class FireBaseImpl implements ApiService {

    private static final String BASE_URL = "https://hacker-news.firebaseio.com";

    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(BASE_URL);

    public static FireBaseImpl getInstance() {
        return firebase;
    }

    private static FireBaseImpl firebase = new FireBaseImpl();

    private FireBaseImpl(){

    }

    @Override
    public void getStoryIds(final ResponseListener<List<Long>> listener) {
        final DatabaseReference ref = firebaseDatabase.child("v0").child("topstories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref.removeEventListener(this);
                ArrayList<Long> list = (ArrayList<Long>) dataSnapshot.getValue();
                listener.onSuccess(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ref.removeEventListener(this);
                listener.onError(new Exception(databaseError.getMessage()));
            }
        });
    }

    @Override
    public void getStory(long id, final ResponseListener<Story> responseListener) {
        final DatabaseReference ref = firebaseDatabase.child("v0").child("item").child(id+"");
          ref.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                    ref.removeEventListener(this);
                    responseListener.onSuccess((Story) dataSnapshot.getValue(Story.class));
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                   ref.removeEventListener(this);
                    responseListener.onError(new Exception(databaseError.getMessage()));
              }
          });
    }

}
