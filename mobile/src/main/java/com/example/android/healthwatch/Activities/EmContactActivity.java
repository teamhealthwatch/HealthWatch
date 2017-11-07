package com.example.android.healthwatch.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.Adapters.CustomAdapter;
import com.example.android.healthwatch.Fragments.EmergencyContactFragment;
import com.example.android.healthwatch.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmContactActivity extends AppCompatActivity implements View.OnClickListener {

    public String login;
    public static final String KEY_LOGIN="login";
    FloatingActionButton fab;

    ListView listView;
    ArrayList<Contact> contacts;
    Bundle contact;
    private static CustomAdapter adapter;
    int index;
    String fullName;
    String phoneNumber;
    boolean pc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        getSupportActionBar().setTitle("Emergency Contacts");
        Intent intent = getIntent();
        login = intent.getStringExtra(LoginActivity.KEY_LOGIN);

        fab = findViewById(R.id.float_button);
        fab.setOnClickListener(this);

        listView = findViewById(R.id.list);

        contact = null;
        contacts = new ArrayList<>();
        index = 0;


    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EmergencyContactFragment editNameDialogFragment = EmergencyContactFragment.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }


    private void displayContacts(Bundle contact){
        adapter = new CustomAdapter(contacts, getApplicationContext());
        listView.setAdapter(adapter);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                if(extras != null){

                    fullName = extras.getString("fullName");
                    phoneNumber = extras.getString("phoneNumber");
                    pc = extras.getBoolean("pc");
                    storeContact();
                    displayContacts(extras);
                }
                else{
                    Toast.makeText(EmContactActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

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
                    AlertDialog alertDialog = new AlertDialog.Builder(EmContactActivity.this).create();
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
                    usersRef.child(login).child(name).setValue(new Contact(phoneNumber, pContact), new DatabaseReference.CompletionListener(){
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("Data could not be saved " + databaseError.getMessage());
                            } else {
                                System.out.println("Data saved successfully.");
                            }
                        }

                    });
                    contacts.add(new Contact(fullName, pNumber, pContact));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void finishContact(){
        startActivity(new Intent(EmContactActivity.this, MedTrackerActivity.class));
    }

    public void onClick(View v){
        if(v == fab){
            showEditDialog();
        }

    }


}
