package com.example.android.healthwatch;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;
    Button btnSignOut;

    //Declare authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        textView = (TextView) findViewById(R.id.textViewUsername);

        btnSignOut = (Button) findViewById(R.id.btn_signout);
        btnSignOut.setOnClickListener(this);

        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();

        textView.setText("Welcome User " + intent.getStringExtra(LoginActivity.KEY_LOGIN));
    }

    @Override
    public void onClick(View v){
        if(v == btnSignOut)
        {
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


}

