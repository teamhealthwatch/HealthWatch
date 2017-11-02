package com.example.android.healthwatch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by faitholadele on 10/11/17.
 */

public class AlarmService extends Service {

    private NotificationManager alarmNotificationManager;
    private boolean isRunning;
    private Context context;
    MediaPlayer mMediaPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        sendNotification("Medication Time!");

        String state = intent.getExtras().getString("extra");
        Log.e("service", state);

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
            mMediaPlayer = MediaPlayer.create(this, R.raw.annoying_alarm_clock);
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
            Log.e("stop reaching here", state);

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
