package com.example.android.healthwatch;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class HeartRateService extends Service implements SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private NodeApi.NodeListener nodeListener;
    private String remoteNodeId;

    private int currentHeartRate;

    private SensorManager sensorManager;

    private Sensor sensor;

    public final String HEART_RATE = "/heart_rate";

    public HeartRateService() {


    }

    @Override
    public void onCreate() {
        super.onCreate();

        nodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                remoteNodeId = node.getId();
                Log.i("Node", "Node id connected to is " + remoteNodeId);

            }

            @Override
            public void onPeerDisconnected(Node node) {
                Log.i("Node", "Node disconnected" + currentHeartRate);

            }

        };

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        // Register Node and Message listeners
                        Wearable.NodeApi.addListener(googleApiClient, nodeListener);
                        // If there is a connected node, get it's id that is used when sending messages
                        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                                    remoteNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                }).addApi(Wearable.API).build();

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
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentHeartRate = (int) (sensorEvent.values.length > 0 ? sensorEvent.values[0] : 0.0f);

        String strPayload = Integer.toString(currentHeartRate);
        byte[] payload = strPayload.getBytes();

        String heartRateString = Integer.toString(currentHeartRate);

        // This initialization is the one working.
//        heartRateView = (TextView) findViewById(R.id.heartRateView);
//
//        heartRateView.setText(heartRateString);

        Log.i("sensorChanged", "sensor changed " + currentHeartRate + " " + sensorEvent.sensor.getType());

//        Wearable.MessageApi.sendMessage(googleApiClient, remoteNodeId, HEART_RATE, payload).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
//            @Override
//            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
//
//                if (sendMessageResult.getStatus().isSuccess()) {
//                    Log.i("heart rate", "heart rate sent to phone " + currentHeartRate);
//                } else {
//                    Log.i("heart rate", "problem sending heart rate");
//                }
//
//            }
//        });

        Toast.makeText(getApplicationContext(), "heart rate is " + currentHeartRate, Toast.LENGTH_SHORT).show();
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

    private void measureHeartRate() {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        if (sensorManager != null) {
            if (sensor != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                googleApiClient.reconnect();
            } else {
                Log.w("tag", "No heart rate found");
            }
        }
//        Log.i("heartRate", "measuring");

    }


}
