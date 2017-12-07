package com.example.android.healthwatch.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.example.android.healthwatch.Model.ResponseCountdown;
import com.example.android.healthwatch.R;


public class AskUserActivity extends Activity{

    private String TAG = "AskUserActivity";

    private ResponseCountdown responseCountdown;

    private TextView mTextField;
    AskUserActivity.MessageReceiver messageReceiver;

    private CountDownTimer countDownTimer;

    private int notiID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate!");

        setContentView(R.layout.activity_ask_user);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
//                mTextField = findViewById(R.id.countdown_view);
//
//                countDownTimer = new CountDownTimer(60000, 1000) {
//
//                    public void onTick(long millisUntilFinished) {
//                        mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//                    }
//
//                    public void onFinish() {
//                        // send message to mobile
//
//                    }
//                }.start();
            }
        });


        // Register the local broadcast receiver to receive messages from the listener.
//        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
//        messageReceiver = new AskUserActivity.MessageReceiver();
//        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

//        countDownTimer = new CountDownTimer(60000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
////                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
////                currentSec = millisUntilFinished / 1000;
//            }
//
//            public void onFinish() {
//                // send message to mobile to make calls
//
//            }
//        }.start();
        notiID = getIntent().getIntExtra("notiID", 0);
        Log.v(TAG, "notiID is " + notiID);

    }



    public void onYesButton(View v){
        stopService(new Intent(getApplicationContext(), HeartRateService.class));

        // finish activity
        finish();

        // remove notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.cancel(notiID);
    }

    public void onNoButton(View v){

    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Long message = intent.getLongExtra("countdown", -1);
            Log.v(TAG, "Ask User Activity received message: " + message);

//            if (mTextField == null) {
//                mTextField = findViewById(R.id.countdown_view);
//            }

//            mTextField.setText("seconds remaining: " + message.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        notiID = getIntent().getIntExtra("notiID", 0);
        Log.v(TAG, "notiID is " + notiID);
    }
}
