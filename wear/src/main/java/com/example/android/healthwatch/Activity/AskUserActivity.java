package com.example.android.healthwatch.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wear.widget.CircularProgressLayout;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.HeartRateService;
import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.Model.SendThread;
import com.example.android.healthwatch.R;
import com.example.android.healthwatch.TimerIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;


public class AskUserActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private String TAG = "AskUserActivity";

    private int notiID;

    public final static String PHONE_CALL_PATH = "/phone_call_path";

    GoogleApiClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate!");

        setContentView(R.layout.activity_ask_user);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
            }
        });

        notiID = getIntent().getIntExtra("notiID", 0);
        Log.v(TAG, "notiID is " + notiID);

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();

        // Register the local broadcast receiver to receive messages from the listener.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        AskUserActivity.MessageReceiver messageReceiver = new AskUserActivity.MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }



    public void onYesButton(View v){

        Toast.makeText(getApplicationContext(), "Start heart rate monitor in 5 minutes", Toast.LENGTH_SHORT);

        // stop heart rate service
        stopService(new Intent(getApplicationContext(), HeartRateService.class));

        // stop timer service
        Intent yesIntent = new Intent(getApplicationContext(), TimerIntentService.class);
        yesIntent.putExtra("simpleKill", false);
        startService(yesIntent);
        stopService(yesIntent);

        // finish activity
        finish();

        // remove notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.cancel(notiID);
    }

    public void onNoButton(View v){

        // send message to mobile to make phone calls
        String message = "make phone call";
        new SendThread(PHONE_CALL_PATH, message, googleClient).start();

        // stop heart rate service
        stopService(new Intent(getApplicationContext(), HeartRateService.class));

        // start then stop timer service in order to pass extra
        Intent noIntent = new Intent(getApplicationContext(), TimerIntentService.class);
        noIntent.putExtra("simpleKill", true);
        startService(noIntent);
        stopService(noIntent);


        // finish activity
        finish();

        // remove notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.cancel(notiID);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        notiID = getIntent().getIntExtra("notiID", 0);
        Log.v(TAG, "notiID is " + notiID);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("kill");
            Log.v(TAG, "Emergency Contact received message: " + message);

            if(message != null){
                // finish activity
                finish();

                // remove notification
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(AskUserActivity.this);

                // Build the notification and issues it with notification manager.
                notificationManager.cancel(notiID);
            }

        }
    }
}
