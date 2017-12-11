package com.example.android.healthwatch.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.Model.SendThread;
import com.example.android.healthwatch.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

public class PersonalInfoActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView inMedCondView;
    private TextView inAllerView;
    private TextView inCurrentMedView;
    private TextView inBloodView;
    private TextView inOtherView;

    public final static String MED_INFO_PATH = "/info_path";

    GoogleApiClient googleClient;

    private final String TAG = "PersonalInfoActivity";

    PersonalInfoActivity.MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);


        // Enables Always-on
        setAmbientEnabled();

        inMedCondView = findViewById(R.id.input_cond_view);
        inAllerView = findViewById(R.id.input_allergies_view);
        inCurrentMedView = findViewById(R.id.input_current_med_view);
        inBloodView = findViewById(R.id.input_current_med_view);
        inOtherView = findViewById(R.id.input_other_view);

        // Build a new GoogleApiClient that includes the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        requestMedInfo();

        // Register the local broadcast receiver to receive messages from the listener.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        messageReceiver = new PersonalInfoActivity.MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    private void requestMedInfo() {
        // send request thread
        String message = "get med info";
        new SendThread(MED_INFO_PATH, message, googleClient).start();
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            byte[] newBytes0 = intent.getByteArrayExtra("info0");
            byte[] newBytes1 = intent.getByteArrayExtra("info1");
            byte[] newBytes2 = intent.getByteArrayExtra("info2");
            byte[] newBytes3 = intent.getByteArrayExtra("info3");
            byte[] newBytes4 = intent.getByteArrayExtra("info4");

            if (newBytes0 != null) {
                inMedCondView.setText(retrieveInfo(newBytes0));
            } else if (newBytes1 != null) {
                inAllerView.setText(retrieveInfo(newBytes1));
            } else if (newBytes2 != null) {
                inAllerView.setText(retrieveInfo(newBytes2));
            } else if (newBytes3 != null) {
                inAllerView.setText(retrieveInfo(newBytes3));
            } else if(newBytes4 != null){
                inAllerView.setText(retrieveInfo(newBytes4));
            }

        }
    }

    private String retrieveInfo(byte[] bytes) {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            String info = (String) in.readObject();

            Log.v(TAG, "info is " + info);

            return info;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return "";

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "onPause!!!!!");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

}
