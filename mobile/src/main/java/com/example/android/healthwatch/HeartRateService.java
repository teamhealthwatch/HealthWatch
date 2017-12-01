package com.example.android.healthwatch;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
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
        GoogleApiClient.OnConnectionFailedListener,
        DatabaseHelper.EmergencyContactCallback{

    private final String HEART_RATE = "/heart_rate";
    private GoogleApiClient googleApiClient;
    private NodeApi.NodeListener nodeListener;
    private String remoteNodeId;
    private MessageApi.MessageListener messageListener;

    private String TAG = "HeartRateService mobile";
    NotificationManager mNotificationManager;

    int numMessages = 0;

    DatabaseHelper dh;

    private String login;

    private Contact primaryContact;
    private ArrayList<Contact> contactList;

    public static String id = "test_channel_01";

    private boolean makeCall = false;


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
                        //setProgressBar(Integer.parseInt(payload));
                        int hR = (Integer.parseInt(payload));
                        if(primaryContact != null){
                            makePhoneCall(hR);
                        }
                    }
                }
                else{
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
                //Toast.makeText(getApplicationContext(), getString(R.string.wearable_api_unavailable), Toast.LENGTH_LONG).show();
            }
        }).addApi(Wearable.API).build();

        googleApiClient.connect();

        //Grab primary contact and a list of emergency contacts for user
        dh = new DatabaseHelper();
        dh.registerCallback(this);

        // avoid crashing when user kills the app and the service still try to start
        if(login != null){
            dh.getPrimaryContact(login);
            dh.getEmergencyContactList(login);
        }



        return super.onStartCommand(intent, flags, startId);
    }

    public void makePhoneCall(int heartRate)
    {
        if (heartRate >= 50 ||  heartRate <= 30)
        {
            if(!makeCall) {
                makeCall = true;

                String primaryPhoneNumber = primaryContact.getPhoneNumber();
                Log.i("Phone call", "heart rate is correct");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + primaryPhoneNumber));
                //801-696-0277

                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
                // texting
                textContacts();

                //notification
                addNotification();
            }

        }
    }

    public void textContacts()
    {
        String phoneNumber = primaryContact.getPhoneNumber();
        String text = "Please contact me, I may be need in help.";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void addNotification() {
        Log.i("Start", "notification");

   /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this, id);

        mBuilder.setContentTitle("New Message");
        mBuilder.setContentText("You've received new message.");
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.drawable.newhrt);

   /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);

        // show notification on watch
        // add support to Android 8.0 and above
        mBuilder.setChannelId(id)
                .extend(new NotificationCompat.WearableExtender());

   /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[5];
        events[0] = new String("Medical condition:");
        events[1] = new String("Allergies:");
        events[2] = new String("current Medication:");
        events[3] = new String("Blood Type:");
        events[4] = new String("other: ");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Medication Condition:");

        // Moves events into the big view
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        int notificationID = 990;

        createchannel();

   /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this,MedConditionActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MedConditionActivity.class);

   /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

   /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    @Override
    public void contactList(ArrayList<Contact> myList) {
        contactList = myList;
    }

    @Override
    public void primaryContact(Contact c) {
        primaryContact = c;
    }

    private void createchannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(id,
                    "heart rate channel",  //name of the channel
                    NotificationManager.IMPORTANCE_DEFAULT);   //importance level
            //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
            // Configure the notification channel.
            mChannel.enableLights(true);
            //Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);

        }
    }
}
