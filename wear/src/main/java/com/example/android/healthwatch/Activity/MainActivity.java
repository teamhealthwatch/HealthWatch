package com.example.android.healthwatch.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.Adapter.NavigationAdapter;
import com.example.android.healthwatch.HeartRateService;
import com.example.android.healthwatch.Model.NavigationItem;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class MainActivity extends WearableActivity implements WearableNavigationDrawerView.OnItemSelectedListener{

    private TextView heartRateView;
    private Button heartRateButton;
    private boolean isMeasuring;




    // Menu
    private WearableNavigationDrawerView mWearableNavigationDrawer;

    private ArrayList<NavigationItem> drawerList;



    private String TAG = "MainActivity";


    MainActivity.MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAmbientEnabled();

        setContentView(R.layout.activity_main);



        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                heartRateButton = (Button) findViewById(R.id.heartRateButton);

                // Top navigation drawer
                intialDrawer();

                startService(new Intent(MainActivity.this, HeartRateService.class));
            }
        });




        heartRateView = (TextView) findViewById(R.id.heartRateView);


        isMeasuring = true;




        // Register the local broadcast receiver to receive messages from the listener.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        messageReceiver = new MainActivity.MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }





    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "onPause!!!!!");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    public void onHeartRateButton(View v) {

        if (isMeasuring) {

            stopMeasuring();

            Log.i("stop button", "Paused measuring");

        } else {

            startMeasuring();

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
                newIntent = new Intent(MainActivity.this, MedicationTrackerActivity.class);
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
                newIntent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                startActivity(newIntent);
                finish();
                break;
        }
    }


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer message = intent.getIntExtra("heartrate", -1);
            Log.v(TAG, "Main Activity received message: " + message);

            if (heartRateView == null) {
                heartRateView = findViewById(R.id.heartRateView);
            }

            if(message != -1){
                heartRateView.setText(message.toString());
            }



        }
    }

    private void stopMeasuring() {

        // stop service
        stopService(new Intent(MainActivity.this, HeartRateService.class));


        // change heartRateView text
        heartRateView = findViewById(R.id.heartRateView);
        heartRateView.setText("---");

        // change button text
        heartRateButton = (Button) findViewById(R.id.heartRateButton);
        heartRateButton.setText("Start");

        // change boolean
        isMeasuring = false;
    }

    private void startMeasuring() {
        // start service
        startService(new Intent(MainActivity.this, HeartRateService.class));


        // change button text
        heartRateButton = (Button) findViewById(R.id.heartRateButton);
        heartRateButton.setText("Stop");

        // change boolean;
        isMeasuring = true;
    }
}
