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
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.android.healthwatch.Adapter.ContactAdapter;
import com.example.android.healthwatch.Adapter.MedAdapter;
import com.example.android.healthwatch.Adapter.NavigationAdapter;
import com.example.android.healthwatch.Model.MedModel;
import com.example.android.healthwatch.Model.Medication;
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

public class MedicationTrackerActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        WearableNavigationDrawerView.OnItemSelectedListener{

    private WearableRecyclerView wearableRecyclerView;
    private MedAdapter medAdapter;
    private ArrayList<MedModel> medication;

    GoogleApiClient googleClient;

    // Menu
    private WearableNavigationDrawerView mWearableNavigationDrawer;
    private ArrayList<NavigationItem> drawerList;



    private AdapterView.OnItemClickListener onItemClickListener;
    private MedAdapter.MedClickListener medClickListener;

    public static final String MED_ITEM ="med item";
    public final static String MEDICATION_PATH = "/medication_path";
    public final static String TAG = "MedicationTracker";

    MedicationTrackerActivity.MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_condition);

        medication = new ArrayList<>();

//        for (int i = 0; i < 10; i++){
//            medication.add(new Medication("medication"+ i, "dosage" + i));
//        }
//        medication.add(new Medication("Hydrocodone", "Dosage: 3"));
//        medication.add(new Medication("Cephalexin", "Dosage: 1"));
//        medication.add(new Medication("Generic Zocor", "Dosage: 2"));
//        medication.add(new Medication("Lisinopril", "Dosage: 2"));
//        medication.add(new Medication("Generic Synthroid", "Dosage: 2"));



        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableRecyclerView = stub.findViewById(R.id.med_recycler_view);
//                wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
                wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(MedicationTrackerActivity.this));



                medClickListener = new MedAdapter.MedClickListener() {
                    @Override
                    public void onMedClickListener(int pos) {
                        Log.i("MedClickListener", "clicked " + pos);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MED_ITEM, medication.get(pos));
                        Intent newIntent = new Intent(MedicationTrackerActivity.this, MedInfoActivity.class);
                        newIntent.putExtras(bundle);
                        startActivity(newIntent);
                    }
                };

                medAdapter = new MedAdapter(medication, MedicationTrackerActivity.this, medClickListener);

                wearableRecyclerView.setAdapter(medAdapter);
                wearableRecyclerView.setFocusable(true);


                // Top navigation drawer
                intialDrawer();
            }
        });



        // Register the local broadcast receiver to receive messages from the listener.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        messageReceiver = new MedicationTrackerActivity.MessageReceiver();
        LocalBroadcastManager.getInstance(MedicationTrackerActivity.this).registerReceiver(messageReceiver, messageFilter);


        // Build a new GoogleApiClient that includes the Wearable API
        googleClient = new GoogleApiClient.Builder(MedicationTrackerActivity.this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(MedicationTrackerActivity.this)
                .addOnConnectionFailedListener(MedicationTrackerActivity.this)
                .build();

        googleClient.connect();
        //requestMeds();


    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
        requestMeds();
    }

    private void requestMeds() {

        // send request thread
        String message = "get meds";
        new SendThread(MEDICATION_PATH, message, googleClient).start();
    }


    @Override
    public void onItemSelected(int pos) {
        Log.i("Drawer", "OnItemSelected!");

        Intent newIntent;

        switch (pos) {
            case 0:
                Toast.makeText(MedicationTrackerActivity.this, "Main page", Toast.LENGTH_SHORT).show();
                newIntent = new Intent(MedicationTrackerActivity.this, MainActivity.class);
                startActivity(newIntent);
                finish();
                break;
            case 1:
                Toast.makeText(MedicationTrackerActivity.this, "Medication Tracker", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(MedicationTrackerActivity.this, "Emergency Contact", Toast.LENGTH_SHORT).show();
                newIntent = new Intent(MedicationTrackerActivity.this, EmergencyContactActivity.class);
                startActivity(newIntent);
                finish();
                break;
            case 3:
                Toast.makeText(MedicationTrackerActivity.this, "Personal Info", Toast.LENGTH_SHORT).show();
                newIntent = new Intent(MedicationTrackerActivity.this, PersonalInfoActivity.class);
                startActivity(newIntent);
                finish();
                break;
        }
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

    private void intialDrawer(){
        mWearableNavigationDrawer = findViewById(R.id.top_navigation_drawer);

        drawerList = new ArrayList<>();
        drawerList.add(new NavigationItem("Heart Rate", R.mipmap.heart_icon));
        drawerList.add(new NavigationItem("Medication Tracker", R.mipmap.med_icon));
        drawerList.add(new NavigationItem("Emergency Contact", R.mipmap.contact_icon));
        drawerList.add(new NavigationItem("Personal Info", R.mipmap.personal_info_icon));

        mWearableNavigationDrawer.setAdapter(new NavigationAdapter(MedicationTrackerActivity.this, drawerList));
        mWearableNavigationDrawer.addOnItemSelectedListener(this);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            byte[] newBytes = intent.getByteArrayExtra("med");

            if (newBytes != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(newBytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    MedModel newMed = (MedModel) in.readObject();
                    // Display message in UI
                    Log.v(TAG, "medication name is " + newMed.getName());

                    // store contact in list
                    medication.add(newMed);

                    // refresh adapter
                    displayMeds(medication);

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
            }

        }
    }

    public void displayMeds(ArrayList<MedModel> list) {
        medAdapter = new MedAdapter(list, MedicationTrackerActivity.this, medClickListener);
        wearableRecyclerView.setAdapter(medAdapter);
        wearableRecyclerView.setFocusable(true);
    }


}
