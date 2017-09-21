package com.example.android.healthwatch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{


    public static final String KEY_LOGIN = "login";
    /*public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
*/
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;

    private static final String TAG = "EmailPassword";

    //finish buttonRegister
    Button buttonRegister;
    boolean isNextButtonClicked = false;

    //used to pass login to next activities
    private String email;

    //Declare authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = (EditText) findViewById(R.id.usertxt);
        editTextPassword = (EditText) findViewById(R.id.passwordText);
        editTextFirstName = (EditText) findViewById(R.id.First_nameText);
        editTextLastName = (EditText) findViewById(R.id.Last_nameText);

        buttonRegister = (Button) findViewById(R.id.finishbttn);
        buttonRegister.setOnClickListener(this);

        //initialize authentication
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void registerUser(){
        email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String first_name = editTextFirstName.getText().toString().trim();
        final String last_name = editTextLastName.getText().toString().trim();

        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("users").child(usernameFromEmail(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String response = "Duplicate here";
                    Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this,"Hi",Toast.LENGTH_LONG).show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("users");
                    usersRef.child(usernameFromEmail(email)).setValue(new User(email, first_name, last_name, password), new DatabaseReference.CompletionListener(){
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("Data could not be saved " + databaseError.getMessage());
                            } else {
                                System.out.println("Data saved successfully.");
                            }
                        }
                    });
                    createAccount(email, password);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void createAccount(String email, String password){
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            onAuthSuccess(task.getResult().getUser());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        //writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        //startActivity(new Intent(RegisterActivity.this, HomePageActivity.class));
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra(KEY_LOGIN, username);
        startActivity(intent);
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editTextEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Required.");
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Required.");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        String fname = editTextFirstName.getText().toString();
        if (TextUtils.isEmpty(fname)) {
            editTextFirstName.setError("Required.");
            valid = false;
        } else {
            editTextFirstName.setError(null);
        }

        String lname = editTextLastName.getText().toString();
        if (TextUtils.isEmpty(lname)) {
            editTextLastName.setError("Required.");
            valid = false;
        } else {
            editTextLastName.setError(null);
        }

        return valid;
    }

    public void emcontact(View view){
        Intent intent = new Intent(this, EmContactActivity.class);
        intent.putExtra(KEY_LOGIN, email);
        startActivity(intent);
    }

    @Override
    public void onClick(View v){
        if(v == buttonRegister && isNextButtonClicked)
        {
            emcontact(v);
        }
        else{

            registerUser();
        }
    }

    public void main_acitivity(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 999);
    }

}
