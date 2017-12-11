package com.example.android.healthwatch.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
Handles logging a user in. LoC = 68
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EmailPassword";

    public static final String KEY_LOGIN="login";

    private EditText editTextLogin;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView buttonCreate;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLogin = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        buttonLogin = (Button) findViewById(R.id.btn_login);
        buttonCreate = (TextView) findViewById(R.id.btn_signup);

        buttonLogin.setOnClickListener(this);
        buttonCreate.setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            openProfile(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (task.isSuccessful()) {
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void openProfile(FirebaseUser user){
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra(KEY_LOGIN, usernameFromEmail(user.getEmail()));
        Intent medIntent = new Intent(this, MedConditionActivity.class);
        medIntent.putExtra(KEY_LOGIN, usernameFromEmail(user.getEmail()));
        startActivity(intent);
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editTextLogin.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editTextPassword.setError("Required.");
            valid = false;
        } else {
            editTextLogin.setError(null);
        }

        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Required.");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        return valid;
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            signIn(editTextLogin.getText().toString().trim(), editTextPassword.getText().toString().trim());
        }
        if(v == buttonCreate){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}