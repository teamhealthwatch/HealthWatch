package com.example.android.healthwatch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.healthwatch.Model.Contact;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    String TAG = "mobile Listener";

    GoogleApiClient googleApiClient;

    final static String EMERGENCY_CONTACT_PATH = "/emergency_contact";

    @Override
    public void onCreate() {
        super.onCreate();


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(EMERGENCY_CONTACT_PATH)) {
            final String message = new String(messageEvent.getData());
            Log.v(TAG, "Message path received on phone is: " + messageEvent.getPath());
            Log.v(TAG, "Message received on phone is: " + message);

            // Broadcast message to MainActivity for display
//            Intent messageIntent = new Intent();
//            messageIntent.setAction(Intent.ACTION_SEND);
//            messageIntent.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);


            // TODO: get list of contacts from databases

            /* for now just create a local list */
            ArrayList<Contact> newList = implementTestList();

            // send contact list
            sendList(newList);


        } else {
            super.onMessageReceived(messageEvent);
        }
    }


    private ArrayList<Contact> implementTestList() {
        ArrayList<Contact> resultList = new ArrayList<Contact>();
        resultList.add(new Contact("aaa", "(656) 565-6565", false));
        resultList.add(new Contact("bbb", "(656) 535-6585", true));
        resultList.add(new Contact("ccc", "(655) 533-6285", false));
        return resultList;
    }

    private void sendList(ArrayList<Contact> list) {


        ObjectOutput out = null;
        try {


            // covert each contact to byte array, and send to wear using sendthread
            for (int i = 0; i < list.size(); i++) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                out = new ObjectOutputStream(bos);

                out.writeObject(list.get(i));
                out.flush();
                byte[] newBytes = bos.toByteArray();

                // sending in threads causing random order on receiving items
                new SendThread(EMERGENCY_CONTACT_PATH, newBytes, googleApiClient).start();
                Log.v(TAG, "sending " + i + " item");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

}
