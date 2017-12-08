

package com.example.android.healthwatch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.healthwatch.Activities.MedConditionActivity;
import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.DatabaseHelper.EmergencyContactCallback;
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
        implements GoogleApiClient.ConnectionCallbacks, EmergencyContactCallback, DatabaseHelper.MedInfoCallback,
        GoogleApiClient.OnConnectionFailedListener {
    String TAG = "mobile Listener";

    GoogleApiClient googleApiClient;

    final static String EMERGENCY_CONTACT_PATH = "/emergency_contact";

    private String login;


    public final static String PHONE_CALL_PATH = "/phone_call_path";

    private Contact primaryContact;

    int numMessages = 0;

    public static String id = "test_channel_01";

    NotificationManager mNotificationManager;

    DatabaseHelper dh;

    String medcond_;
    String allergies_;
    String curr_med_;
    String blood_type_;
    String other_;


    @Override
    public void onCreate() {
        super.onCreate();


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

        //Grab primary contact and a list of emergency contacts for user
        dh = new DatabaseHelper();
        dh.registerEmergencyCallback(this);

        // avoid crashing when user kills the app and the service still try to start
        if (login != null) {
            dh.getPrimaryContact(login, "");
            dh.getEmergencyContactList(login, "");
        }

    }

    /*
    Method used to pass login to the ListenerService so it can call the appropriate database methods
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            login = intent.getStringExtra("login");
        }
        return flags;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        // Uses callback method contactList declared at bottom to send emergency contacts to watch
        DatabaseHelper dh = new DatabaseHelper();
        dh.registerEmergencyCallback(this);


        if (messageEvent.getPath().equals(EMERGENCY_CONTACT_PATH)) {
            final String message = new String(messageEvent.getData());
            Log.v(TAG, "Message path received on phone is: " + messageEvent.getPath());
            Log.v(TAG, "Message received on phone is: " + message);
            //dh.getEmergencyContactList(login);

            // Broadcast message to MainActivity for display
//            Intent messageIntent = new Intent();
//            messageIntent.setAction(Intent.ACTION_SEND);
//            messageIntent.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);


            //Grab username
            if (login != null) {
                dh.getEmergencyContactList(login, "");
            }


        } else if (messageEvent.getPath().equals(PHONE_CALL_PATH)) {

//            dh.getPrimaryContact(login);

            final String message = new String(messageEvent.getData());
            Log.v(TAG, "Message path received on phone is: " + messageEvent.getPath());
            Log.v(TAG, "Message received on phone is: " + message);

            // make phone calls
//                makePhoneCall();
            makePhoneCall();


        } else {
            super.onMessageReceived(messageEvent);
        }
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

    /**
     * EmergencyContactCallback method that runs after the database finishes pulling the list
     *
     * @param myList - A returned list of all primary contacts
     */
    @Override
    public void contactList(ArrayList<Contact> myList, String path) {

        sendList(myList);
    }

    @Override
    public void primaryContact(Contact c, String path) {


        Log.v(TAG, "CALLBACK!!!!");
        primaryContact = c;
        makePhoneCall();
    }

    public void makePhoneCall() {

//        String primaryPhoneNumber = primaryContact.getPhoneNumber();
        Log.i("Phone call", "heart rate is correct");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
//        callIntent.setData(Uri.parse("tel:" + primaryPhoneNumber));
        callIntent.setData(Uri.parse("tel:" + "555-555-5555"));
        //801-696-0277

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
        // texting
        textContacts();

        //notification
        addNotification(medcond_, allergies_, curr_med_, blood_type_, other_);
    }

    public void textContacts() {
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

    private void addNotification(String medcond, String allergies, String medication, String bloodType, String other) {
        Log.i("Start", "notification");

        DatabaseHelper dbhelper = new DatabaseHelper();
        dbhelper.registerMedInfoCallback(this);
        dbhelper.getMedConditions(login);

   /* Invoking the default notification service */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, id);

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
        events[0] = new String("Medical condition: " + medcond);
        events[1] = new String("Allergies: " + allergies);
        events[2] = new String("current Medication: " + medication);
        events[3] = new String("Blood Type: " + bloodType);
        events[4] = new String("other: " + other);

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Medication Condition:");

        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        int notificationID = 990;

        createchannel();

   /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, MedConditionActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MedConditionActivity.class);

   /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

   /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(notificationID, mBuilder.build());
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

    @Override
    public void medInfoValues(String medCond, String allergies, String medications, String bloodType, String other) {
        medCond = medcond_;
        allergies = allergies_;
        medications = curr_med_;
        bloodType = blood_type_;
        other = other_;
    }

}
