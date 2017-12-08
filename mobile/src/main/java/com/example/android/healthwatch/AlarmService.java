package com.example.android.healthwatch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.android.healthwatch.Activities.MedTrackerActivity;

/**
 * Created by faitholadele on 10/11/17.
 */

public class AlarmService extends Service {

    private NotificationManager alarmNotificationManager;
    private boolean isRunning;
    private Context context;
    MediaPlayer mMediaPlayer;
    String login;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        sendNotification("Medication time!");
        //login = intent.getExtras().getString("login");
        NotificationManager notify_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // set up an intent that goes to the Main Activity
        Intent intent_main_activity = new Intent(this.getApplicationContext(), MedTrackerActivity.class);

        intent_main_activity.putExtra("login", "testUser");
        // set up a pending intent
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0,
                intent_main_activity, 0);

        // make the notification parameters
        Notification notification_popup = new Notification.Builder(this)
                .setContentTitle("An alarm is going off!")
                .setContentText("Click me!")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pending_intent_main_activity)
                .setAutoCancel(true)
                .build();


        //Log.e("service", state);
        String state = intent.getExtras().getString("extra");
        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        if(!this.isRunning && startId == 1)
        {
//            Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
//            v.vibrate(1000);
            mMediaPlayer = MediaPlayer.create(this, R.raw.solemn);
            mMediaPlayer.start();
            this.isRunning = true;
            startId = 0;
        }
        else if(this.isRunning && startId == 0)
        {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            this.isRunning = false;
            startId = 0;
        }
        else if(!this.isRunning && startId == 0)
        {

            this.isRunning = false;
            startId = 0;

        }
        else if(this.isRunning && startId == 1)
        {
            this.isRunning = true;
            startId = 1;
        }
        else
        {
            //Log.e("stop reaching here", state);

        }
        return  START_NOT_STICKY;
    }
    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MedTrackerActivity.class), 0);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }

    @Override
    public void onDestroy() {
        Log.e("JSLog", "on destroy called");
        super.onDestroy();

        this.isRunning = false;
    }
}