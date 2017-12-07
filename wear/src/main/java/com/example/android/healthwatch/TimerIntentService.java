package com.example.android.healthwatch;

import android.app.IntentService;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class TimerIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_RESPONSE = "com.example.android.healthwatch.action.RESPONSE";
    public static final String ACTION_RESTART = "com.example.android.healthwatch.action.RESTART";

    // TODO: Rename parameters
//    public static final String EXTRA_PARAM1 = "com.example.android.healthwatch.extra.PARAM1";
//    public static final String EXTRA_PARAM2 = "com.example.android.healthwatch.extra.PARAM2";

    public TimerIntentService() {
        super("TimerIntentService");
    }

    public String TAG = "TimerIntentService";

    private boolean simpleKill;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        simpleKill = intent.getBooleanExtra("simpleKill", true);
        Log.v(TAG, "boolean is " + simpleKill);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RESPONSE.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionResponse();
            } else if (ACTION_RESTART.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionRestart();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
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
     * Handle action Baz in the provided background thread with the provided
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
}
