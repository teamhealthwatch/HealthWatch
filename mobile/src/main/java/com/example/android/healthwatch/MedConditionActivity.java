package com.example.android.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MedConditionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_condition);
    }

    public void homeClick(View v)
    {
        startActivity(new Intent(MedConditionActivity.this, HomePageActivity.class));
    }
}
