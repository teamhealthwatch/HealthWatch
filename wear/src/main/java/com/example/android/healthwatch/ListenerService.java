package com.example.android.healthwatch;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {
    String TAG = "wear listener";
    final static String EMERGENCY_CONTACT_PATH = "/emergency_contact";

    public final static String MEDICATION_PATH = "/medication_path";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(EMERGENCY_CONTACT_PATH)) {

            final byte[] contacts = messageEvent.getData();

            // Broadcast message to wearable activity for display
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("contact", contacts);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        } else if(messageEvent.getPath().equals(MEDICATION_PATH)){

            final byte[] meds = messageEvent.getData();

            // Broadcast message to wearable activity for display
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("med",  meds);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }else{
            super.onMessageReceived(messageEvent);
        }
    }
}
