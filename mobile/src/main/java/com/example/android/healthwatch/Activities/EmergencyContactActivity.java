package com.example.android.healthwatch.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.healthwatch.Adapters.EmergencyContactAdapter;
import com.example.android.healthwatch.DatabaseHelper;
import com.example.android.healthwatch.Fragments.EmergencyContactFragment;
import com.example.android.healthwatch.DatabaseHelper.EmergencyContactCallback;
import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
Handles Emergency Contact logic. LoC = 180
 */

public class EmergencyContactActivity extends AppCompatActivity implements View.OnClickListener, EmergencyContactCallback {
    //Used to store current user name
    public String login;

    FloatingActionButton fab;

    //Declare authentication
    private FirebaseAuth mAuth;
    //Listview and adapter for displaying
    ListView listView;
    private static EmergencyContactAdapter adapter;

    int index;
    //Global variables for emergency contact fields
    private String fullName;
    private String phoneNumber;
    private boolean pc;
    //Stores current user's emergency contacts
    ArrayList<Contact> contacts;
    //Used to determine if user is registered
    boolean firstTime;
    //Communicates with database
    DatabaseHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        //Setup listview
        listView = findViewById(R.id.list);
        //Get Username and check if this page is being called through registration or through home page
        firstTime = false;
        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        if(!intent.hasExtra("Not_Registered")){
            getContacts();
        }
        else{
            firstTime = true;
            contacts = new ArrayList<>();
            displayContacts(contacts);
        }

        //Initialize Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();
        //Initialize Floating Button
        fab = findViewById(R.id.float_button);
        fab.setOnClickListener(this);

        index = 0;
        getSupportActionBar().setTitle("Emergency Contacts");
        //Build database helper for adding/removing storage
        dh = new DatabaseHelper();
        dh.registerEmergencyCallback(this);
    }

    /*
    Called to update screen with new list view of contacts
     */
    public void displayContacts(ArrayList<Contact> list){
        adapter = new EmergencyContactAdapter(this, list, getApplicationContext());
        listView.setAdapter(adapter);
    }

    /*
    Displays dialog for recieving user input for emergency contacts
     */
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EmergencyContactFragment editNameDialogFragment = EmergencyContactFragment.newInstance("New");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    /*
    A created intent returns here with its request code, result code, and any data passed to it.
    Here we determine if there is data returned and then store this in the database
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                if(extras != null){
                    fullName = extras.getString("fullName");
                    phoneNumber = extras.getString("phoneNumber");
                    pc = extras.getBoolean("pc");
                    if(extras.containsKey("Delete")){
                        dh.removeEmergencyContact(login, fullName);
                        removeFromList(fullName);
                    }
                    else{
                        if(pc){
                            dh.updatePrimaryContact(login, "");
                        }
                        if(extras.getBoolean("Save")){
                            dh.updateEmergencyContact(login, fullName, phoneNumber, pc);
                        }
                        else{
                            storeContact();
                        }
                    }


                }
                else{
                    Toast.makeText(EmergencyContactActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /*
    Original store to the database
    TODO: Send this logic to DatabaseHelper
     */
    public void storeContact(){
        final String name = fullName;
        final String pNumber = phoneNumber;
        final boolean pContact = pc;

        Log.i("Phone Number", phoneNumber);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("contacts").child(login).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Log.v("Children",""+ childDataSnapshot.getKey()); //displays the key for the node
                        Log.v("Children",""+ childDataSnapshot.child("name").getValue());   //gives the value for given keyname
                    }
                    AlertDialog alertDialog = new AlertDialog.Builder(EmergencyContactActivity.this).create();
                    alertDialog.setTitle("Duplicate Contact");
                    alertDialog.setMessage("A person with that same name was found, please enter a different contact.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("contacts");
                    Contact temp = new Contact(pNumber, pContact);
                    usersRef.child(login).child(name).setValue(temp, new DatabaseReference.CompletionListener(){
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("Data could not be saved " + databaseError.getMessage());
                            } else {
                                if(firstTime){
                                    contacts.add(new Contact(name, phoneNumber, pContact));
                                    displayContacts(contacts);
                                }
                                System.out.println("Data saved successfully.");
                            }
                        }

                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    Used to get contacts from the database
    TODO:Send logic to DatabaseHelper
     */
    public void getContacts(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        contacts = new ArrayList<>();
        myRef.child("contacts").child(login).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.getKey();
                String phoneNumber = (String) dataSnapshot.child("phoneNumber").getValue().toString();
                boolean primaryContact = (boolean) dataSnapshot.child("primary").getValue();
                Contact c = new Contact(name, phoneNumber, primaryContact);
                contacts.add(c);
                displayContacts(contacts);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*
    Catches any duplicates found, since the database has a unique name restriction, we can enforce this
    restriction here as well.
     */
    public void removeFromList(String username){
        for(Contact c : contacts){
            if(c.getName().equals(username)) {
                contacts.remove(c);
                break;
            }
        }
        displayContacts(contacts);
    }

    /*
    Called to display the menu. Determines which menu to display if the user is registered or not
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firstTime){
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.menu_next, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else{
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.menu, menu);
            return super.onCreateOptionsMenu(menu);
        }

    }

    /*
    Logic for what to do for which menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hmpg:
                Toast.makeText(this, "Homepage", Toast.LENGTH_SHORT).show();
                Intent intt = new Intent(this, HomePageActivity.class);
                intt.putExtra("login", login);
                startActivity(intt);
                return true;
            case R.id.med_tracker:
                Toast.makeText(this, "Medication Tracker", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MedTrackerActivity.class);
                intent.putExtra("login", login);
                startActivity(intent);
                return true;
            case R.id.contact:
                return true;
            case R.id.info:
                Toast.makeText(this, "Personal Info", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, MedConditionActivity.class);
                intent3.putExtra("login", login);
                startActivity(intent3);
                return true;
            case R.id.signout:
                Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.action_next:
                finishContact();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    Called if the user is not registered. Continues to Medication tracking
     */
    public void finishContact(){
        Intent intent = new Intent(this, MedTrackerActivity.class);
        intent.putExtra("login", login);
        intent.putExtra("Not_Registered", "TRUE");
        startActivity(intent);
    }

    /*
    OnClick handler
     */
    public void onClick(View v){
        if(v == fab){
            showEditDialog();
        }

    }

    /*
    Used with the adapter. Returns here after and item on the screen is clicked. Gets information
    about this item and sends it to a new dialog form for deleting or editing.
     */
    public void onItemClick(int mPosition)
    {
        Contact tempContact = (Contact) contacts.get(mPosition);
        String name = tempContact.getName();
        String phoneNumber = tempContact.getPhoneNumber();
        Boolean pContact = tempContact.getPrimary();

        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("phoneNumber", phoneNumber);
        b.putBoolean("pContact", pContact);

        FragmentManager fm = getSupportFragmentManager();
        EmergencyContactFragment editNameDialogFragment = EmergencyContactFragment.newInstance("Edit");
        editNameDialogFragment.setArguments(b);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    /*
    Callback method for returning from the database helper with a list of contacts
     */
    @Override
    public void contactList(ArrayList<Contact> myList, String path) {
        contacts = myList;
        displayContacts(contacts);
    }

    /*
    Not used but must be implemented with emergency contact callback
     */
    @Override
    public void primaryContact(Contact c, String path) {

    }

    /*
    Called to update with a new primary contact for a user
     */
    public void updatePrimaryContact(String name) {
        dh.updatePrimaryContact(login, name);
    }
}