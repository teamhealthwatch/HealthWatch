package com.example.android.healthwatch;

import com.example.android.healthwatch.Model.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ryan on 11/19/2017.
 */

public class DatabaseHelper {
    ArrayList<Contact> contacts;
    EmergencyContactCallback contactListCallback;


    public void getEmergencyContactList(final String username){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        contacts = new ArrayList<>();
        myRef.child("contacts").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    String name = childSnapshot.getKey();
                    String phoneNumber = (String) childSnapshot.child("phoneNumber").getValue().toString();
                    boolean primaryContact = (boolean) childSnapshot.child("primary").getValue();
                    Contact c = new Contact(name, phoneNumber, primaryContact);
                    contacts.add(c);
                }
                contactListCallback.contactList(contacts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Updates Emergency Contacts with the new state of the Primary Contact.
     * If newPrimary is empty set all current contact's primary field to false.
     * Else, set the primary field for the name contained in newPrimary as true and set all
     * other contacts to false.
     * @param username: The user that we need to send to the database to return all of their contacts
     * @param newPrimary: Has two conditions, empty or a String containing the new primary contact
     */
    public void updatePrimaryContact(final String username, final String newPrimary){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("contacts").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts = new ArrayList<>();
                if(newPrimary.isEmpty()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String name = childSnapshot.getKey();
                        String phoneNumber = (String) childSnapshot.child("phoneNumber").getValue().toString();
                        boolean primaryContact = (boolean) childSnapshot.child("primary").getValue();
                        if (primaryContact) {
                            Map<String, Object> postValues = new HashMap<String, Object>();
                            postValues.put("phoneNumber", phoneNumber);
                            postValues.put("primary", false);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = database.getReference("contacts");
                            usersRef.child(username).child(name).updateChildren(postValues);
                        }
                        contacts.add(new Contact(name, phoneNumber, false));

                    }
                }

                else{
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String phoneNumber = (String) childSnapshot.child("phoneNumber").getValue().toString();
                        boolean primaryContact = (boolean) childSnapshot.child("primary").getValue();
                        String name = childSnapshot.getKey();
                        if(name.equals(newPrimary)){
                            Map<String, Object> postValues = new HashMap<String, Object>();
                            postValues.put("phoneNumber", phoneNumber);
                            postValues.put("primary", true);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = database.getReference("contacts");
                            usersRef.child(username).child(name).updateChildren(postValues);
                            contacts.add(new Contact(name, phoneNumber, true));
                        }
                        else{
                            if (primaryContact) {
                                Map<String, Object> postValues = new HashMap<String, Object>();
                                postValues.put("phoneNumber", phoneNumber);
                                postValues.put("primary", false);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference usersRef = database.getReference("contacts");
                                usersRef.child(username).child(name).updateChildren(postValues);
                            }
                            contacts.add(new Contact(name, phoneNumber, false));
                        }

                    }

                }

                contactListCallback.contactList(contacts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getPrimaryContact(String username){

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("contacts").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if((boolean) childSnapshot.child("primary").getValue()){
                        Contact c = new Contact(childSnapshot.getKey(), (String) childSnapshot.child("phoneNumber").getValue().toString(), true);
                        contactListCallback.primaryContact(c);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Instantiates the callback for the current session
    public void registerCallback(EmergencyContactCallback callBackClass){
        contactListCallback = callBackClass;
    }


    public interface EmergencyContactCallback {
        //Used to get a one-time list of the current emergency contacts associated with a user
        void contactList(ArrayList<Contact> myList);
        //Returns the current primary contact of a user
        void primaryContact(Contact c);
    }


}



