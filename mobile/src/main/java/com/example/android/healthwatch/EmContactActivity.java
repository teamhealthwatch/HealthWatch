package com.example.android.healthwatch;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    public String login;
    public static final String KEY_LOGIN="login";
    FloatingActionButton fab;

    ListView listView;
    ArrayList<Contact> contacts;
    Bundle contact;
    private static CustomAdapter adapter;
    int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        getSupportActionBar().setTitle("Emergency Contacts");
        Intent intent = getIntent();
        login = intent.getStringExtra(LoginActivity.KEY_LOGIN);

        fab = (FloatingActionButton) findViewById(R.id.float_button);
        fab.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.list);

        contact = null;
        contacts = new ArrayList<Contact>();
        index = 0;

    }

    private void displayContacts(Bundle contact){


        contacts.add(new Contact(contact.getString("fullName"), contact.getString("phoneNumber"), contact.getBoolean("pc")));
        adapter = new CustomAdapter(contacts, getApplicationContext());
        listView.setAdapter(adapter);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                if(extras != null){
                    
                    String d = extras.getString("fullName");
                    //Log.e("Name is", d);
                    if(d == null){
                        Toast.makeText(EmContactActivity.this,"Made it to displayContacts.",Toast.LENGTH_LONG).show();
                    }
                    displayContacts(extras);
                }
                else{
                    Toast.makeText(EmContactActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    public void startPopUp(){
        Intent intent = new Intent(this, EmergencyPopUp.class);
        intent.putExtra(KEY_LOGIN, login);
        startActivityForResult(intent, 1);
    }

    public void finishContact(){
        startActivity(new Intent(EmContactActivity.this, MedTrackerActivity.class));
    }

    public void onClick(View v){
        if(v == fab){
            startPopUp();
        }

    }


}
