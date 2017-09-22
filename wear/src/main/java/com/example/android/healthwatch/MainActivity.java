package com.example.android.healthwatch;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private final String HEART_RATE = "/heart_rate";

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView heartRateView;
    private Button heartRateButton;
    private boolean isMeasuring;

    private GoogleApiClient googleApiClient;
    private NodeApi.NodeListener nodeListener;
    private String remoteNodeId;
    private String t;

    private int currentHeartRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        heartRateView = (TextView) findViewById(R.id.heartRateView);
        heartRateButton = (Button) findViewById(R.id.heartRateButton);
        isMeasuring = true;

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

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
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
        measureHeartRate();
    }

    private void measureHeartRate() {

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        if (sensorManager != null) {
            if (sensor != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.w("tag", "No heart rate found");
            }
        }
        Log.i("heartRate", "measuring");

    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        currentHeartRate = (int)(sensorEvent.values.length > 0 ? sensorEvent.values[0] : 0.0f);

        String heartRateString = Integer.toString(currentHeartRate);
        Log.i("heart rate", heartRateString);

        // This initialization is the one working.
        heartRateView = (TextView) findViewById(R.id.heartRateView);

        heartRateView.setText(heartRateString);

        Log.i("sensorChanged", "sensor changed " + currentHeartRate + " " + sensorEvent.sensor.getType());

        Wearable.MessageApi.sendMessage(googleApiClient, remoteNodeId, HEART_RATE, null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                if (sendMessageResult.getStatus().isSuccess()) {
                    Log.i("heart rate", "heart rate sent to phone " + currentHeartRate);
                } else {
                    Log.i("heart rate", "problem sending heart rate");
                }

            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        googleApiClient.reconnect();
        // Check is Google Play Services available
        /*int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (connectionResult != ConnectionResult.SUCCESS) {
            // Google Play Services is NOT available. Show appropriate error dialog
            GooglePlayServicesUtil.showErrorDialogFragment(connectionResult, this, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        } else {
            googleApiClient.connect();
        }*/
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        // Unregister Node and Message listeners, disconnect GoogleApiClient and disable buttons
        Wearable.NodeApi.removeListener(googleApiClient, nodeListener);
        googleApiClient.disconnect();
        super.onPause();
    }

    public void onHeartRateButton(View v) {

        if (isMeasuring) {

            heartRateButton = (Button) findViewById(R.id.heartRateButton);
            heartRateButton.setText("Start");
            onPause();

            isMeasuring = false;

            heartRateView.setText("---");

            Log.i("stop button", "Paused measuring");

        }
        else {

            heartRateButton = (Button) findViewById(R.id.heartRateButton);
            heartRateButton.setText("Stop");
            onResume();

            isMeasuring = true;

            Log.i("start button", "Resume measuring");
        }

    }

    public void onMenu(View v) {

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
