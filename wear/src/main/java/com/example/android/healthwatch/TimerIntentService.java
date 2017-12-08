package com.example.android.healthwatch;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.healthwatch.Model.SendThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class TimerIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final String ACTION_RESPONSE = "com.example.android.healthwatch.action.RESPONSE";
    public static final String ACTION_RESTART = "com.example.android.healthwatch.action.RESTART";


    public TimerIntentService() {
        super("TimerIntentService");
    }

    public String TAG = "TimerIntentService";

    private boolean simpleKill;

    public final static String PHONE_CALL_PATH = "/phone_call_path";

    GoogleApiClient googleClient;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        simpleKill = intent.getBooleanExtra("simpleKill", true);
        Log.v(TAG, "boolean is " + simpleKill);

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RESPONSE.equals(action)) {
                handleActionResponse();
            } else if (ACTION_RESTART.equals(action)) {
                handleActionRestart();
            }
        }
    }

    /**
     * Handle action Response in the provided background thread with the provided
     * parameters.
     */
    private void handleActionResponse() {
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                Log.v(TAG, "second remaining: " + l/1000);
            }

            @Override
            public void onFinish() {
                // send message to mobile to make phone calls
                String message = "make phone call";
                new SendThread(PHONE_CALL_PATH, message, googleClient).start();

                // broadcast kill activity and cancel noti
                Intent messageIntent = new Intent();
                messageIntent.setAction(Intent.ACTION_SEND);
                messageIntent.putExtra("kill", "kill");
                LocalBroadcastManager.getInstance(TimerIntentService.this).sendBroadcast(messageIntent);
                Log.v(TAG, "kill request sent to AskUserActivity");


                Looper.myLooper().quit();
            }
        }.start();

        Looper.loop();

        Log.v(TAG, "HERE!!!!!");

        if(!simpleKill){
            Intent timerIntent = new Intent(this, TimerIntentService.class);
            timerIntent.setAction(ACTION_RESTART);
            startService(timerIntent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "on destroy!!!");
    }

    /**
     * Handle action Restart in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRestart() {
        CountDownTimer countDownTimer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long l) {
                Log.v(TAG, "second remaining: " + l/1000);
            }

            @Override
            public void onFinish() {
                Looper.myLooper().quit();
            }
        }.start();

        Looper.loop();

        Intent heartrateIntent = new Intent(this, HeartRateService.class);
        heartrateIntent.setAction(ACTION_RESTART);
        startService(heartrateIntent);
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
}
