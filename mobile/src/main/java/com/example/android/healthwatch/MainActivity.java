package com.example.android.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String KEY_LOGIN = "login";
    private FirebaseAuth mAuth;
    Button buttonSignIn;
    Button buttonSignUp;
    Button skip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        buttonSignIn = (Button) findViewById(R.id.btn_signin);
        buttonSignIn.setOnClickListener(this);

        buttonSignUp = (Button) findViewById(R.id.btn_signup);
        buttonSignUp.setOnClickListener(this);

        skip = (Button) findViewById(R.id.skip);
        skip.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();

        if (mAuth.getCurrentUser() != null) {
            //Toast.makeText(this,mAuth.getCurrentUser().getEmail(),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.putExtra(KEY_LOGIN, usernameFromEmail(mAuth.getCurrentUser().getEmail()));
            startActivity(intent);
            finish();
        }
    }

    public void login(View view){
       startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void register(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivityForResult(intent, 999);
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RESULT_OK)
        {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            login(v);
        }
        else if(v == buttonSignUp){
            register(v);
        }
        else if(v == skip){
            Intent intent = new Intent(this, EmergencyContactActivity.class);
            intent.putExtra("login", "testUser");
            startActivity(intent);
        }
    }
}
