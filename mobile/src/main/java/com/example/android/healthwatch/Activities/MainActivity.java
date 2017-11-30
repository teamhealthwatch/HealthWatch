package com.example.android.healthwatch.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.android.healthwatch.R;
import com.google.firebase.auth.FirebaseAuth;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.SEND_SMS;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String KEY_LOGIN = "login";
    private FirebaseAuth mAuth;
    Button buttonSignIn;
    Button buttonSignUp;
    Button skip;

    private static final int PERMISSION_REQUEST_CODE = 200;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove medicationMessage bar
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        buttonSignIn = (Button) findViewById(R.id.btn_signin);
        buttonSignIn.setOnClickListener(this);

        buttonSignUp = (Button) findViewById(R.id.btn_signup);
        buttonSignUp.setOnClickListener(this);

        skip = (Button) findViewById(R.id.skip);
        skip.setOnClickListener(this);

        if(!checkPermission()){
            requestPermission();
        }

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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE, SEND_SMS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean phoneAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean smsAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (phoneAccepted && smsAccepted) {
                        Log.i("Permissions","Permissions Accepted");
                    }

                    else {

                        Log.i("Permissions","Permissions Denied");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CALL_PHONE, SEND_SMS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.putExtra("login", "testUser");
            startActivity(intent);
        }
    }
}
