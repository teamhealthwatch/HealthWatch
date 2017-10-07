package com.example.android.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmContactActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_LOGIN = "email";
    public static final String KEY_FULLNAME = "full_name";
    public static final String KEY_PHONENUMBER = "phone_number";
    public static final String KEY_PRIMARYCONTACT = "primary_contact";


    private String login;
    private EditText editTextFullName;
    private EditText editTextPhoneNumber;


    Button buttonNext;
    Button buttonAdd;
    CheckBox pc;

    ListView listView;
    ArrayList<String> values;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emcontact);

        editTextFullName = (EditText) findViewById(R.id.emName);
        editTextPhoneNumber = (EditText) findViewById(R.id.phonenumber);
        editTextPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        buttonNext = (Button) findViewById(R.id.finishbttn);
        buttonNext.setOnClickListener(this);
        buttonAdd = (Button) findViewById(R.id.addbttn);
        buttonAdd.setOnClickListener(this);
        pc = (CheckBox) findViewById(R.id.PrimaryCont);
        Intent intent = getIntent();
        login = intent.getStringExtra(LoginActivity.KEY_LOGIN);

        listView = (ListView) findViewById(R.id.list);

        values = new ArrayList<String>();
        index = 0;
    }

    public void storeContact(){
        final String name = editTextFullName.getText().toString().trim();
        final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        boolean primaryContact = false;
        if(pc.isChecked()){
            primaryContact = true;
        }
        final boolean pContact = primaryContact;
        values.add(name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);


        Log.i("Phone Number", phoneNumber);

        //Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("contacts").child(login).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Log.v("Children",""+ childDataSnapshot.getKey()); //displays the key for the node
                        Log.v("Children",""+ childDataSnapshot.child("name").getValue());   //gives the value for given keyname
                    }
                    String response = "Duplicate account found. Please make a new account.";
                    Toast.makeText(EmContactActivity.this,response,Toast.LENGTH_LONG).show();
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
                    //createAccount(email, password);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Used to ensure the input given by the user is correct and can be stored in the database
    private boolean validateForm() {
        boolean valid = true;

        String contactName = editTextFullName.getText().toString();
        if (TextUtils.isEmpty(contactName)) {
            editTextFullName.setError("Required.");
            valid = false;
        } else {
            editTextFullName.setError(null);
        }

        String phone = editTextPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            editTextPhoneNumber.setError("Required.");
            valid = false;
        } else if (phone.length() < 14) {
            Toast.makeText(EmContactActivity.this, "Please enter 10-digit phone number",
                    Toast.LENGTH_SHORT).show();
            valid = false;

        } else {
            editTextPhoneNumber.setError(null);
        }

        return valid;
    }

    public void finishContact(){
        startActivity(new Intent(EmContactActivity.this, MedTrackerActivity.class));
    }

    public void onClick(View v){
        if(v == buttonNext){
            finishContact();
        }
        else if(v == buttonAdd) {
            storeContact();
        }
    }

}
