package com.example.android.healthwatch;

import com.example.android.healthwatch.Model.Contact;
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
    ArrayList<Contact> contacts;
    Callback myCallback;


    public void getEmergencyContactList(final String username){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        contacts = new ArrayList<>();
        myRef.child("contacts").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    String name = childSnapshot.getKey();
                    String phoneNumber = (String) childSnapshot.child("phoneNumber").getValue().toString();
                    boolean primaryContact = (boolean) childSnapshot.child("primaryContact").getValue();
                    Contact c = new Contact(name, phoneNumber, primaryContact);
                    contacts.add(c);
                }
                myCallback.contactList(contacts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getPrimaryContact(final String username){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("primaryContact").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getKey();
                String phoneNumber = (String) dataSnapshot.child("phoneNumber").getValue().toString();
                Contact c = new Contact(name, phoneNumber, true);
                myCallback.primaryContact(c);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Instantiates the callback for the current session
    void registerCallback(Callback callBackClass){
        myCallback = callBackClass;
    }


    interface Callback{
        //Used to get a one-time list of the current emergency contacts associated with a user
        void contactList(ArrayList<Contact> myList);
        //Returns the current primary contact of a user
        void primaryContact(Contact c);
    }

}



