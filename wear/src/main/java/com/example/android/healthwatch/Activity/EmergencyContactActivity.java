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
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;

import com.example.android.healthwatch.Adapter.ContactAdapter;
import com.example.android.healthwatch.Model.Contact;
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

public class EmergencyContactActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private WearableRecyclerView wearableRecyclerView;
    private ContactAdapter contactAdapter;

//    private TextView textView;

    GoogleApiClient googleClient;
    private final static String TAG = "Wear MainActivity";
    final static String EMERGENCY_CONTACT_PATH = "/emergency_contact";

    private ArrayList<Contact> contacts;

    private Contact primaryContact;

    EmergencyContactActivity.MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableRecyclerView = findViewById(R.id.contact_recycler_view);
                wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(EmergencyContactActivity.this));
//                textView = findViewById(R.id.textView3);
//                textView.setText("Emergency Contacts:\n");


            }
        });


        // Register the local broadcast receiver to receive messages from the listener.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        messageReceiver = new EmergencyContactActivity.MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        // Build a new GoogleApiClient that includes the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        contacts = new ArrayList<Contact>();

        requestContacts();


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


    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            byte[] newBytes = intent.getByteArrayExtra("contact");

            if (newBytes != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(newBytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    Contact newContact = (Contact) in.readObject();
                    // Display message in UI
                    Log.v(TAG, "Contact name is " + newContact.getName());
//                textView.append(newContact.getName() + "\n");

                    // store contact in list
                    contacts.add(newContact);

                    // refresh adapter
                    displayContacts(contacts);

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

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }


    private void requestContacts() {

        // send request thread
        String message = "get contacts";
        new SendThread(EMERGENCY_CONTACT_PATH, message, googleClient).start();
    }

    public void displayContacts(ArrayList<Contact> list) {
        contactAdapter = new ContactAdapter(list, EmergencyContactActivity.this);
        wearableRecyclerView.setAdapter(contactAdapter);
        wearableRecyclerView.setFocusable(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "onPause!!!!!");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

}
