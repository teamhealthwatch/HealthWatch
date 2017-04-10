package com.example.android.healthwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view){
       startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void register(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivityForResult(intent, 999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RESULT_OK)
        {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }


    }
}
