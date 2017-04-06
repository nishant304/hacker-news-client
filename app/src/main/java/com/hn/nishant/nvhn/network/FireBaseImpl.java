package com.hn.nishant.nvhn.network;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.api.ApiService;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.util.RealmInteger;
import com.hn.nishant.nvhn.util.StoryObjPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by nishant on 16.03.17.
 */

public class FireBaseImpl implements ApiService {

    private static final String BASE_URL = "https://hacker-news.firebaseio.com";
    private static FireBaseImpl firebase = new FireBaseImpl();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(BASE_URL);

    private FireBaseImpl() {
    }

    public static FireBaseImpl getInstance() {
        return firebase;
    }

    @Override
    public void getStoryIds(final ResponseListener<List<Long>> listener) {
        firebaseDatabase.child("v0").child("topstories")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Long> list = (ArrayList<Long>) dataSnapshot.getValue();
                        listener.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(new Exception(databaseError.getMessage()));
                    }
                });
    }

    @Override
    public void getStory(long id, final ResponseListener<Story> responseListener) {
        firebaseDatabase.child("v0").child("item").child(id + "")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> hm = (HashMap<String, Object>) dataSnapshot.getValue();
                        Story story = getFromMap(hm);
                        setCommentsId(story, hm);
                        if (story != null) {
                            responseListener.onSuccess(story);
                        } else {
                            responseListener.onError(new Exception("something went wrong"));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        responseListener.onError(new Exception(databaseError.getMessage()));
                    }
                });
    }

    /***
     *
     * A workaround for Realm issue to sotre list of integers
     * https://github.com/realm/realm-java/issues/575
     * @param story
     * @param hm
     */
    private void setCommentsId(Story story, HashMap<String, Object> hm) {
        if (hm == null) {
            return;
        }
        ArrayList<Long> kids = (ArrayList<Long>) hm.get("kids");
        if (kids != null) {
            RealmList<RealmInteger> realmKids = new RealmList<>();
            for (Long kid : kids) {
                realmKids.add(new RealmInteger(kid));
            }
            story.setKid(realmKids);
        }
    }

    /***
     *  Avoid gson parsing on ui thread
     * @param hm
     * @return
     */
    private Story getFromMap(HashMap<String, Object> hm) {
        Story story = new Story();
        story.setId(((Long) hm.get("id")).intValue());
        story.setBy((String) hm.get("by"));
        story.setType((String) hm.get("type"));
        story.setTitle((String) hm.get("title"));
        story.setText((String) hm.get("text"));
        Long desc = (Long) hm.get("descendants");
        if (desc != null) {
            story.setDescendants(desc.intValue());
        }
        story.setParent((Long) hm.get("parent"));
        return story;
    }

    @Override
    public void getUpdates(final ResponseListener<List<Long>> responseListener) {
        firebaseDatabase.child("v0").child("updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                List<Long> updates = (List<Long>) hashMap.get("items");
                if(updates == null){
                    updates = new ArrayList<Long>();
                }
                responseListener.onSuccess(updates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                responseListener.onError(new Exception(databaseError.getMessage()));
            }
        });
    }

}
