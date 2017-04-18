package com.example.android.healthwatch;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

//    private TextView mTextView;

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView heartRateView;
    private Button heartRateButton;
    private boolean isMeasuring;


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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

//        int heartRate = (int)sensorEvent.values[0];
//
//        heartRateView.setText(heartRate);


        int heartRate = (int)(sensorEvent.values.length > 0 ? sensorEvent.values[0] : 0.0f);

        String heartRateString = Integer.toString(heartRate);
        Log.i("heart rate", heartRateString);

        // This initialization is the one working.
        heartRateView = (TextView) findViewById(R.id.heartRateView);

        heartRateView.setText(heartRateString);

        Log.i("sensorChanged", "sensor changed " + heartRate + " " + sensorEvent.sensor.getType());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
//        if (sensor == mValuen) {
//            switch (i) {
//                case 0:
//                    System.out.println("Unreliable");
//                    con=0;
//                    break;
//                case 1:
//                    System.out.println("Low Accuracy");
//                    con=0;
//                    break;
//                case 2:
//                    System.out.println("Medium Accuracy");
//                    con=0;
//
//                    break;
//                case 3:
//                    System.out.println("High Accuracy");
//                    con=1;
//                    break;
//            }
//        }
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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
}
