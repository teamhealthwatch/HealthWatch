package com.example.android.healthwatch.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.Adapter.InfoAdapter;
import com.example.android.healthwatch.Adapter.NavigationAdapter;
import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.Model.MedInfo;
import com.example.android.healthwatch.Model.NavigationItem;
import com.example.android.healthwatch.Model.SendThread;
import com.example.android.healthwatch.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class PersonalInfoActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        WearableNavigationDrawerView.OnItemSelectedListener{


    public final static String MED_INFO_PATH = "/info_path";

    GoogleApiClient googleClient;

    private final String TAG = "PersonalInfoActivity";

    PersonalInfoActivity.MessageReceiver messageReceiver;

    // Menu
    private WearableNavigationDrawerView mWearableNavigationDrawer;
    private ArrayList<NavigationItem> drawerList;

    private WearableRecyclerView wearableRecyclerView;

    private InfoAdapter infoAdapter;

    private ArrayList<MedInfo> medInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);


        // Enables Always-on
        setAmbientEnabled();

        intialDrawer();

        wearableRecyclerView = findViewById(R.id.info_recycler_view);
        wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(PersonalInfoActivity.this));

        // Build a new GoogleApiClient that includes the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        medInfos = new ArrayList<MedInfo>();

        requestMedInfo();

        // Register the local broadcast receiver to receive messages from the listener.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        messageReceiver = new PersonalInfoActivity.MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
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
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    private void requestMedInfo() {
        // send request thread
        String message = "get med info";
        new SendThread(MED_INFO_PATH, message, googleClient).start();
    }

    @Override
    public void onItemSelected(int pos) {
        Log.i("Drawer", "OnItemSelected!");

        Intent newIntent;

        switch (pos) {
            case 0:
                Toast.makeText(PersonalInfoActivity.this, "Main page", Toast.LENGTH_SHORT).show();
                newIntent = new Intent(PersonalInfoActivity.this, MainActivity.class);
                startActivity(newIntent);
                finish();
                break;
            case 1:
                Toast.makeText(PersonalInfoActivity.this, "Medication Tracker", Toast.LENGTH_SHORT).show();
                newIntent = new Intent(PersonalInfoActivity.this, MedicationTrackerActivity.class);
                startActivity(newIntent);
                finish();
                break;
            case 2:
                Toast.makeText(PersonalInfoActivity.this, "Emergency Contact", Toast.LENGTH_SHORT).show();
                newIntent = new Intent(PersonalInfoActivity.this, EmergencyContactActivity.class);
                startActivity(newIntent);
                finish();
                break;
            case 3:
                Toast.makeText(PersonalInfoActivity.this, "Personal Info", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            byte[] newBytes0 = intent.getByteArrayExtra("info0");
            byte[] newBytes1 = intent.getByteArrayExtra("info1");
            byte[] newBytes2 = intent.getByteArrayExtra("info2");
            byte[] newBytes3 = intent.getByteArrayExtra("info3");
            byte[] newBytes4 = intent.getByteArrayExtra("info4");

            if (newBytes0 != null) {
                medInfos.add(0, new MedInfo("Medical conditions", retrieveInfo(newBytes0)));
            } else if (newBytes1 != null) {
                medInfos.add(1, new MedInfo("Allergies", retrieveInfo(newBytes1)));
            } else if (newBytes2 != null) {
                medInfos.add(2, new MedInfo("Current medications", retrieveInfo(newBytes2)));
            } else if (newBytes3 != null) {
                medInfos.add(3, new MedInfo("Blood type", retrieveInfo(newBytes3)));
            } else if(newBytes4 != null){
                medInfos.add(4, new MedInfo("Other", retrieveInfo(newBytes4)));

                displayInfos(medInfos);
            }

        }
    }

    private String retrieveInfo(byte[] bytes) {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            String info = (String) in.readObject();

            Log.v(TAG, "info is " + info);

            return info;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return "";

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "onPause!!!!!");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    private void intialDrawer(){
        mWearableNavigationDrawer = findViewById(R.id.top_navigation_drawer);

        drawerList = new ArrayList<>();
        drawerList.add(new NavigationItem("Heart Rate", R.mipmap.heart_icon));
        drawerList.add(new NavigationItem("Medication Tracker", R.mipmap.med_icon));
        drawerList.add(new NavigationItem("Emergency Contact", R.mipmap.contact_icon));
        drawerList.add(new NavigationItem("Personal Info", R.mipmap.personal_info_icon));

        mWearableNavigationDrawer.setAdapter(new NavigationAdapter(PersonalInfoActivity.this, drawerList));
        mWearableNavigationDrawer.addOnItemSelectedListener(this);
    }

    public void displayInfos(ArrayList<MedInfo> list) {
        infoAdapter = new InfoAdapter(list, PersonalInfoActivity.this);
        wearableRecyclerView.setAdapter(infoAdapter);
        wearableRecyclerView.setFocusable(true);
    }
}
