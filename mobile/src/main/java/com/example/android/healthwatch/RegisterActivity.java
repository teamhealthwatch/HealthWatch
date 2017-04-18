package com.example.android.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    //String needed to connect to database
    private static final String REGISTER_URL = "http://136.60.172.18/insert_user.php";

    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";

    private EditText editTextLogin;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;

    //finish buttonRegister
    Button buttonRegister;
    boolean isNextButtonClicked = false;

    //used to pass login to next activities
    private String login;

    //new instance of a user
    User newUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextLogin = (EditText) findViewById(R.id.usertxt);
        editTextPassword = (EditText) findViewById(R.id.passwordText);
        editTextFirstName = (EditText) findViewById(R.id.First_nameText);
        editTextLastName = (EditText) findViewById(R.id.Last_nameText);

        buttonRegister = (Button) findViewById(R.id.finishbttn);
        buttonRegister.setOnClickListener(this);

    }

    private void registerUser(){
        login = editTextLogin.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String first_name = editTextFirstName.getText().toString().trim();
        final String last_name = editTextLastName.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("Duplicate entry")){
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                            response = "Duplicate account found, try a new login.";
                            Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();

                        }
                        else{
                            response = "New account successfully created.";
                            Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();
                            isNextButtonClicked = true;
                            buttonRegister.setText("NEXT");

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_LOGIN,login);
                params.put(KEY_PASSWORD,password);
                params.put(KEY_FIRST_NAME, first_name);
                params.put(KEY_LAST_NAME, last_name);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void emcontact(View view){
        Intent intent = new Intent(this, EmContactActivity.class);
        intent.putExtra(KEY_LOGIN, login);
        startActivity(intent);
    }

    @Override
    public void onClick(View v){
        if(v == buttonRegister && isNextButtonClicked == true)
        {
            emcontact(v);
        }
        else{

            registerUser();
        }
    }
}
