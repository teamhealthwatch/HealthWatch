package com.example.android.healthwatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.healthwatch.Activity.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class HeartRateService extends Service implements SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private int currentHeartRate;

    private SensorManager sensorManager;

    private Sensor sensor;


    private String TAG = "HeartRateService";

    public static String id = "test_channel_01";

    int notificationID = 1;


    public HeartRateService() {


    }

    @Override
    public void onCreate() {
        super.onCreate();

        currentHeartRate = 0;

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.v(TAG, "onStartCommand");

        createchannel();

        // start foreground with notification

        Intent newIntent = new Intent(this, MainActivity.class);

        newIntent.putExtra("NotiID", "Notification ID is " + notificationID);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);

        // we are going to add an intent to open the camera here.
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(this, 0, mainIntent, 0);


        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.common_google_signin_btn_icon_dark_normal_background,
                        "Open HeathWatch", mainPendingIntent)
                        .build();

        //Now create the notification.  We must use the NotificationCompat or it will not work on the wearable.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, id)
                        .setSmallIcon(R.drawable.heart)
                        .setContentTitle("HealthWatch reads heart rate in background")
                        .setContentText("Tab to change this")
                        .setContentIntent(pendingIntent)
                        .setChannelId(id)
                        .extend(new NotificationCompat.WearableExtender());

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        Notification newNoti = notificationBuilder.build();

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationID, newNoti);

        startForeground(notificationID, newNoti);

        notificationID++;

        measureHeartRate();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.v(TAG, "onTaskRemoved!!!!!!!");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentHeartRate = (int) (sensorEvent.values.length > 0 ? sensorEvent.values[0] : 0.0f);



        Log.i("sensorChanged", "sensor changed " + currentHeartRate + " " + sensorEvent.sensor.getType());

        Toast.makeText(getApplicationContext(), "heart rate is " + currentHeartRate, Toast.LENGTH_SHORT).show();

        // Pass heart rate to MainActivity

        // Broadcast message to wearable activity for display
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("heartrate", currentHeartRate);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        Log.v(TAG, "heart rate sent to MainActivity");

        // Pass heart rate to Listener Service
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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
    public void onDestroy() {
        super.onDestroy();

        Log.v(TAG, "onDestroy!!!!!!!");

        sensorManager.unregisterListener(this);
        stopSelf();
    }

    private void measureHeartRate() {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        if (sensorManager != null) {
            if (sensor != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                Log.v(TAG, "sensor manager is registered");
            } else {
                Log.w("tag", "No heart rate found");
            }
        }

    }

    private void createchannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(id,
                    "heart rate channel",  //name of the channel
                    NotificationManager.IMPORTANCE_DEFAULT);   //importance level
            //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
            // Configure the notification channel.
            mChannel.enableLights(true);
            //Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);

        }
    }


}
