package com.example.android.healthwatch;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements SensorEventListener, DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

//    private TextView mTextView;

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView heartRateView;
    private Button heartRateButton;
    private boolean isMeasuring;
//    private DataController dataController;

    private GoogleApiClient googleApiClient;

    private int currentHeartRate;
    private int heartRate;

    private static final String KEY_HEART_RATE = "com.example.key.current_heart_rate";

    PutDataRequest putDataReq = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // For some unknown reason, this line does not initialize heartRateView.
        // But I'll leave it here for now
        heartRateView = (TextView) findViewById(R.id.heartRateView);

        heartRateButton = (Button) findViewById(R.id.heartRateButton);

        isMeasuring = true;


//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
////                mTextView = (TextView) stub.findViewById(R.id.text);
//
//
//            }
//        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

        currentHeartRate = 0;

//        setupOnHeartRateChanged();


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

//        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_FASTEST);
        }

        Log.i("heartRate", "measuring");

    }

    private void setupOnHeartRateChanged() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/heartrate");
        putDataMapReq.getDataMap().putInt(KEY_HEART_RATE, currentHeartRate);
        putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(googleApiClient, putDataReq);

//        sendHeartRate(putDataReq);



    }

    public void sendHeartRate(PutDataRequest putDataRequest) {

        Log.i("TAG", "send heart rate !!!!!!");
//        if (validateConnection()) {
//            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
//                @Override
//                public void onResult(DataApi.DataItemResult dataItemResult) {
//                    Log.v("TAG", "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
//                }
//            });
//        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

//        int heartRate = (int)sensorEvent.values[0];
//
//        heartRateView.setText(heartRate);


        currentHeartRate = (int)(sensorEvent.values.length > 0 ? sensorEvent.values[0] : 0.0f);

        String heartRateString = Integer.toString(currentHeartRate);
        Log.i("heart rate", heartRateString);

        // This initialization is the one working.
        heartRateView = (TextView) findViewById(R.id.heartRateView);

        heartRateView.setText(heartRateString);

        Log.i("sensorChanged", "sensor changed " + currentHeartRate + " " + sensorEvent.sensor.getType());

        setupOnHeartRateChanged();

//        onDataChanged();
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i("Accuracy", "Accuracy changed");
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        googleApiClient.connect();
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        googleApiClient.disconnect();
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
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        Log.i("DATA CHANGED: ", "onDataChanged is executed.");

        for (DataEvent event : dataEventBuffer) {
            Log.i("DATA CHANGED: ", "inside for loop.");

            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/heartrate") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateHeartRate(dataMap);

                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    private void updateHeartRate(final DataMap dataMap) {

        Log.i("TAG", "update heart rate.");

        if (putDataReq != null) {
            if (googleApiClient.isConnected()) {
                Wearable.DataApi.putDataItem(googleApiClient, putDataReq).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.i("Sent data: ", putDataReq.toString());
                        Log.i("Success: ", "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
                    }
                });
            }
        }

//        putDataMapReq.asPutDataRequest()


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
