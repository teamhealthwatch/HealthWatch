package com.example.android.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EmContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emcontact);
    }

   public void medicationClick(View v)
   {
       startActivity(new Intent(EmContactActivity.this, MedTrackerActivity.class));
   }
}
