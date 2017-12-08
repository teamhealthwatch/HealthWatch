package com.example.android.healthwatch;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CircularProgressDrawable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


import com.example.android.healthwatch.Activities.MedConditionActivity;
import com.example.android.healthwatch.Model.Contact;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Yan Tan on 11/29/2017.
 */

public class HeartRateService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private final String HEART_RATE = "/heart_rate";
    private GoogleApiClient googleApiClient;
    private NodeApi.NodeListener nodeListener;
    private String remoteNodeId;
    private MessageApi.MessageListener messageListener;

    private String TAG = "HeartRateService mobile";



    DatabaseHelper dh;

    private String login;

    public static String id = "test_channel_01";



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "onStartCommand");

        if (intent != null && intent.getExtras() != null){
            login = intent.getStringExtra("login");
        }



        // Create NodeListener that enables buttons when a node is connected and disables buttons when a node is disconnected
        nodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                remoteNodeId = node.getId();
            }

            @Override
            public void onPeerDisconnected(Node node) {

            }
        };

        // Create MessageListener that receives messages sent from a wearable
        messageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {

                String payload = null;
                if (messageEvent.getPath().equals(HEART_RATE)) {
                    try {
                        payload = new String(messageEvent.getData(), "utf-8");
                    }
                    catch(UnsupportedEncodingException e){
                        Log.i("Exception", "thrown encoding");
                    }

                    // send heart rate to HomePageActivity
                    Intent messageIntent = new Intent();
                    messageIntent.setAction(Intent.ACTION_SEND);
                    messageIntent.putExtra("heartrate", payload);
                    LocalBroadcastManager.getInstance(HeartRateService.this).sendBroadcast(messageIntent);
                    Log.v(TAG, "heart rate sent to HomePageActivity");

//                    heartRate.setText(payload);
                    if(payload != null) {

                    }
                }
                // TODO:CHANGE THIS PATH
                else if(messageEvent.getPath().equals(HEART_RATE)){

                }else{
                    Log.i("heart rate info", "couldn't get in");
                }
            }
        };


        // Create GoogleApiClient
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                // Register Node and Message listeners
                Wearable.NodeApi.addListener(googleApiClient, nodeListener);
                Wearable.MessageApi.addListener(googleApiClient, messageListener);
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
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE){}

            }
        }).addApi(Wearable.API).build();

        googleApiClient.connect();

        //Grab primary contact and a list of emergency contacts for user
        dh = new DatabaseHelper();


        // avoid crashing when user kills the app and the service still try to start
        if(login != null){
            dh.getPrimaryContact(login);
            dh.getEmergencyContactList(login);
        }

        return super.onStartCommand(intent, flags, startId);
    }



}
