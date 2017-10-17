package com.example.android.healthwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EmergencyPopUp extends Activity implements View.OnClickListener {

    private Button add;
    private Button cancel;
    //private String fullName;
    //private String phoneNumber;
    //private Boolean primaryContact;
    private TextView editTextFullName;
    private TextView editTextPhoneNumber;
    private CheckBox pc;
    private String login;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_popup);

        add = (Button) findViewById(R.id.addbttn);
        add.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.cancelbttn);
        add.setOnClickListener(this);
        editTextFullName = (EditText) findViewById(R.id.emName);
        editTextPhoneNumber = (EditText) findViewById(R.id.phonenumber);
        editTextPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        pc = (CheckBox) findViewById(R.id.PrimaryCont);
        Intent intent = getIntent();
        login = intent.getStringExtra(EmContactActivity.KEY_LOGIN);
    }

    public void storeContact(){
        final String name = editTextFullName.getText().toString().trim();
        final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        boolean primaryContact = false;
        if(pc.isChecked()){
            primaryContact = true;
        }
        final boolean pContact = primaryContact;

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
                    String response = "Duplicate name found. Please enter a new name.";
                    editTextFullName.setText("");
                    editTextPhoneNumber.setText("");
                    Toast.makeText(EmergencyPopUp.this,response,Toast.LENGTH_LONG).show();
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
                    Intent myIntent = new Intent();
                    Bundle contact = new Bundle();
                    contact.putString("fullName", name);
                    contact.putString("phoneNumber", phoneNumber);
                    contact.putBoolean("pc", pContact);
                    Toast.makeText(EmergencyPopUp.this,contact.getString("fullName"),Toast.LENGTH_LONG).show();
                    myIntent.putExtras(contact);
                    setResult(RESULT_OK, myIntent);
                    finish();


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
            Toast.makeText(EmergencyPopUp.this, "Please enter 10-digit phone number",
                    Toast.LENGTH_SHORT).show();
            valid = false;

        } else {
            editTextPhoneNumber.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        if(v == add){
            storeContact();
        }
        else if(v == cancel){
            finish();
        }
    }
}
