package com.example.android.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class EmContactActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String REGISTER_URL = "http://136.60.172.18/insert_contact.php";

    public static final String KEY_LOGIN = "login";
    public static final String KEY_FULLNAME = "full_name";
    public static final String KEY_PHONENUMBER = "phone_number";
    public static final String KEY_PRIMARYCONTACT = "primary_contact";


    private String login;
    private EditText editTextFullName;
    private EditText editTextPhoneNumber;


    Button buttonNext;
    Button buttonAdd;
    CheckBox pc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emcontact);

        editTextFullName = (EditText) findViewById(R.id.emmName);
        editTextPhoneNumber = (EditText) findViewById(R.id.phonenumber);

        buttonNext = (Button) findViewById(R.id.finishbttn);
        buttonNext.setOnClickListener(this);
        buttonAdd = (Button) findViewById(R.id.addbttn);
        buttonAdd.setOnClickListener(this);
        pc = (CheckBox) findViewById(R.id.PrimaryCont);
        Intent intent = getIntent();
        login = intent.getStringExtra(LoginActivity.KEY_LOGIN);
    }

    public void registerContact(){

        /*final String full_name = editTextFullName.getText().toString().trim();
        final String phone_number = editTextPhoneNumber.getText().toString().trim();
        String primary_contact = "0";
        if(pc.isChecked()){
            primary_contact = "1";
        }
        final String p_contact = primary_contact;
        System.out.println(login + " " + full_name + " " + phone_number + " " + p_contact);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        if(response.contains("Duplicate entry")){
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                            response = "Duplicate contact found, try a new contact.";
                            Toast.makeText(EmContactActivity.this,response,Toast.LENGTH_LONG).show();

                        }
                        else{
                            response = "New contact successfully added.";
                            Toast.makeText(EmContactActivity.this,response,Toast.LENGTH_LONG).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EmContactActivity.this,error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_LOGIN,login);
                params.put(KEY_FULLNAME,full_name);
                params.put(KEY_PHONENUMBER, phone_number);
                params.put(KEY_PRIMARYCONTACT, p_contact);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);*/
    }

    public void finishContact(){
        startActivity(new Intent(EmContactActivity.this, MedTrackerActivity.class));
    }

    public void onClick(View v){
        if(v == buttonNext){
            finishContact();
        }
        else if(v == buttonAdd) {
            registerContact();
        }
    }

}
