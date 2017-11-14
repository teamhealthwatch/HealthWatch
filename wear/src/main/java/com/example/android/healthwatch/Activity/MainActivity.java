package com.example.android.healthwatch.Activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.Adapter.NavigationAdapter;
import com.example.android.healthwatch.Model.NavigationItem;
import com.example.android.healthwatch.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends WearableActivity implements SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        WearableNavigationDrawerView.OnItemSelectedListener{

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



//    private CircleMenu circleMenu;

    // Menu
    private WearableNavigationDrawerView mWearableNavigationDrawer;

    private ArrayList<NavigationItem> drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAmbientEnabled();

        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                // Top navigation drawer
                intialDrawer();

            }
        });




        heartRateView = (TextView) findViewById(R.id.heartRateView);
        heartRateButton = (Button) findViewById(R.id.heartRateButton);
//        circleMenu = (CircleMenu)findViewById(R.id.circle_menu);


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

        String strPayload = Integer.toString(currentHeartRate);
        byte [] payload = strPayload.getBytes();

        String heartRateString = Integer.toString(currentHeartRate);

        // This initialization is the one working.
        heartRateView = (TextView) findViewById(R.id.heartRateView);

        heartRateView.setText(heartRateString);

        Log.i("sensorChanged", "sensor changed " + currentHeartRate + " " + sensorEvent.sensor.getType());

        Wearable.MessageApi.sendMessage(googleApiClient, remoteNodeId, HEART_RATE, payload).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
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

        } else {

            heartRateButton = (Button) findViewById(R.id.heartRateButton);
            heartRateButton.setText("Stop");
            onResume();

            isMeasuring = true;

            Log.i("start button", "Resume measuring");
        }

    }

    private void intialDrawer(){
        mWearableNavigationDrawer = findViewById(R.id.top_navigation_drawer);

        drawerList = new ArrayList<>();
        drawerList.add(new NavigationItem("Heart Rate", R.mipmap.heart_icon));
        drawerList.add(new NavigationItem("Medication Tracker", R.mipmap.med_icon));
        drawerList.add(new NavigationItem("Emergency Contact", R.mipmap.contact_icon));
        drawerList.add(new NavigationItem("Personal Info", R.mipmap.personal_info_icon));

        mWearableNavigationDrawer.setAdapter(new NavigationAdapter(MainActivity.this, drawerList));
        mWearableNavigationDrawer.addOnItemSelectedListener(this);

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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemSelected(int pos) {
        Log.i("Drawer", "OnItemSelected!");

        Intent newIntent;

        switch (pos) {
            case 0:
                Toast.makeText(MainActivity.this, "Main page", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, "Medication Tracker", Toast.LENGTH_SHORT).show();
                newIntent = new Intent(MainActivity.this, MedConditionActivity.class);
                startActivity(newIntent);
                finish();
                break;
            case 2:
                Toast.makeText(MainActivity.this, "Emergency Contact", Toast.LENGTH_SHORT).show();
                newIntent = new Intent(MainActivity.this, EmergencyContactActivity.class);
                startActivity(newIntent);
                finish();
                break;
            case 3:
                Toast.makeText(MainActivity.this, "Personal Info", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
