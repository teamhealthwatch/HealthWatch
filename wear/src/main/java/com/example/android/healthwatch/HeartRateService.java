package com.example.android.healthwatch;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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


    public HeartRateService() {


    }

    @Override
    public void onCreate() {
        super.onCreate();

        currentHeartRate = 0;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);
//        Toast.makeText(getApplicationContext(), "this is a service", Toast.LENGTH_SHORT).show();
        measureHeartRate();


        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//        startService(restartServiceIntent);
//
//        super.onTaskRemoved(rootIntent);
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


}
