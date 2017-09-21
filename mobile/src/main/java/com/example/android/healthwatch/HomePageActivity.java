package com.example.android.healthwatch;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity{
    private TextView textView;
//    Button btnSignOut;

    //Declare authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        textView = (TextView) findViewById(R.id.textViewUsername);

//        btnSignOut = (Button) findViewById(R.id.btn_signout);
//        btnSignOut.setOnClickListener(this);

        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();

        textView.setText("Welcome " + intent.getStringExtra(LoginActivity.KEY_LOGIN));

        ProgressBar pb = (ProgressBar)findViewById(R.id.circulaprogbar);
        pb.setProgress(30);
        ((TextView)findViewById(R.id.heartRate)).setText("80BMP");
    }

//    @Override
//    public void onClick(View v){
//        if(v == btnSignOut)
//        {
//            mAuth.signOut();
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.med_tracker:
                Toast.makeText(this, "Medication Tracker", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MedTrackerActivity.class);
                startActivity(intent);
                return true;
            case R.id.contact:
                Toast.makeText(this, "Emmergency Contact", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, EmContactActivity.class);
                startActivity(intent2);
                return true;
            case R.id.info:
                Toast.makeText(this, "Personal Info", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, MedConditionActivity.class);
                startActivity(intent3);
                return true;
            case R.id.history:
                Toast.makeText(this, "Medication History", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, MainActivity.class);
                startActivity(intent4);
                return true;
            case R.id.signout:
                Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

