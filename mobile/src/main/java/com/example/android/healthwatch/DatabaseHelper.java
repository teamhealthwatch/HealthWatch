package com.example.android.healthwatch;

import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.Model.MedInfoModel;
import com.example.android.healthwatch.Model.MedModel;
import com.google.firebase.database.ChildEventListener;
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
    ArrayList<MedModel> medications;
    EmergencyContactCallback contactListCallback;
    MedInfoCallback medInfoCallback;
    MedicationCallback medicationCallback;


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

    public void updateEmergencyContact(final String login, final String username, final String phoneNumber, final boolean pc){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("contacts").child(login).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String name = childSnapshot.getKey();
                    String pNumber = (String) childSnapshot.child("phoneNumber").getValue().toString();
                    boolean primaryContact = (boolean) childSnapshot.child("primary").getValue();
                    if (name.equals(username)) {
                        Map<String, Object> postValues = new HashMap<String, Object>();
                        postValues.put("phoneNumber", phoneNumber);
                        postValues.put("primary", pc);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference("contacts");
                        usersRef.child(login).child(name).updateChildren(postValues);
                        contacts.add(new Contact(name, phoneNumber, pc));
                    }
                    else{
                        contacts.add(new Contact(name, pNumber, primaryContact));
                    }
                }
                contactListCallback.contactList(contacts);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeEmergencyContact(String login, String username){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("contacts").child(login).child(username).removeValue();
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

    public void getMedConditions(final String login) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("medInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean a = dataSnapshot.hasChild(login);
                if(dataSnapshot.hasChild(login))
                {
                    String medcond_ = (String) dataSnapshot.child(login).child("medcond").getValue().toString();
                    String allergies_ = (String) dataSnapshot.child(login).child("allergies").getValue().toString();
                    String curr_med_ = (String) dataSnapshot.child(login).child("curr_med").getValue().toString();
                    String blood_type_ = (String) dataSnapshot.child(login).child("blood_type").getValue().toString();
                    String other_ = (String) dataSnapshot.child(login).child("other").getValue().toString();
                    medInfoCallback.medInfoValues(medcond_, allergies_, curr_med_, blood_type_, other_);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateMedConditions(final String login, final String medCond, final String allergies,
                                    final String medications, final String bloodType, final String other){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("medInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference medInfoRef = database.getReference();
                for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                    String name = childSnapshot.getKey();
                    if(name.equals(login)){
                        MedInfoModel m = new MedInfoModel(medCond, allergies, medications, bloodType, other);
                        medInfoRef.child("medInfo").child(login).setValue(m);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteMedConditions(String login){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("medInfo").child(login).removeValue();
    }

    public void getMedications(String login){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        medications = new ArrayList<>();
        myRef.child("medication").child(login).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    String name = childSnapshot.getKey();
                    String date = (String) childSnapshot.child("date").getValue().toString();
                    String repeats = (String) childSnapshot.child("repeatDays").getValue().toString();
                    String time = (String) childSnapshot.child("time").getValue().toString();
                    String notification = (String) childSnapshot.child("medMessage").getValue().toString();
                    MedModel m = new MedModel(name, time, date, notification, repeats);
                    medications.add(m);
                }
                medicationCallback.medicationList(medications);
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Instantiates the callback for the current session
    public void registerEmergencyCallback(EmergencyContactCallback callBackClass){
        contactListCallback = callBackClass;
    }

    public void registerMedInfoCallback(MedInfoCallback callBackClass){
        medInfoCallback = callBackClass;
    }

    public void registerMedicationCallback(MedicationCallback callBackClass){
        medicationCallback = callBackClass;
    }


    public interface EmergencyContactCallback {
        //Used to get a one-time list of the current emergency contacts associated with a user
        void contactList(ArrayList<Contact> myList);
        //Returns the current primary contact of a user
        void primaryContact(Contact c);
    }

    public interface MedInfoCallback{
        void medInfoValues(String medCond, String allergies, String medications, String bloodType, String other);
    }

    public interface MedicationCallback{
        void medicationList(ArrayList<MedModel> medList);
    }


}



