package com.example.android.healthwatch;

import com.example.android.healthwatch.Model.MedModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Ryan on 11/19/2017.
 */

public class DatabaseHelper {
    public int lastID = -1;

    public int getLastAlarmID(String login){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        getDatabaseAlarms(myRef.child("medication").child(login), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    int currID = (Integer)(childDataSnapshot.child(childDataSnapshot.getKey()).child("id").getValue());
                    if( currID > lastID){
                        lastID = currID;
                    }
                }

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {
                lastID = -1;
            }
        });
        return lastID;
    }

    private void getDatabaseAlarms(DatabaseReference ref, final OnGetDataListener listener){
        final ArrayList<Integer> lastID = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }
}

interface OnGetDataListener {
    //this is for callbacks
    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
