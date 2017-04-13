package com.example.android.healthwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    //finish button
    Button button;

    //new instance of a user
    User newUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        OnNextClick();
    }

    public void OnNextClick()
    {
        button = (Button) findViewById(R.id.finishbttn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userNametxt = (EditText) findViewById(R.id.usertxt);
                newUser.setUserName(userNametxt.getText().toString());
//                Log.i("TAG: ", "Reaching");
//                Intent intent = new Intent();
//                intent.putExtra("result", 999);
//                setResult(RESULT_OK, intent);
//                finish();

                startActivity(new Intent(RegisterActivity.this, EmContactActivity.class));
            }
        });
    }
}
