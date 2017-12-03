package com.example.android.healthwatch.Activities;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.android.healthwatch.ListenerService;
import com.example.android.healthwatch.DatabaseHelper;
import com.example.android.healthwatch.HeartRateService;
import com.example.android.healthwatch.ListenerService;
import com.example.android.healthwatch.Model.Contact;
import com.example.android.healthwatch.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity implements DatabaseHelper.EmergencyContactCallback {
    private TextView textView;
    //private TextView heart_rate;
    Button btnSignOut;
//    Button btnSignOut;

    //Declare authentication
    private FirebaseAuth mAuth;

    private final String HEART_RATE = "/heart_rate";
    private GoogleApiClient googleApiClient;
    private NodeApi.NodeListener nodeListener;
    private String remoteNodeId;
    private MessageApi.MessageListener messageListener;
    private TextView heartRate;
    private String login;
    private Contact primaryContact;
    private ArrayList<Contact> contactList;

    NotificationManager mNotificationManager;
    int numMessages = 0;
    DatabaseHelper dh;

    HomePageActivity.MessageReceiver messageReceiver;

    String medcond_;
    String allergies_;
    String curr_med_;
    String blood_type_;
    String other_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        textView = (TextView) findViewById(R.id.textViewUsername);
        heartRate = (TextView) findViewById(R.id.heartRate);

        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        String payload = null;

        Bundle extras = intent.getExtras();
        login = extras.getString("login");
        String display = "Welcome User " + login;
        textView.setText(display);
        startListenerService(login);
        startHeartRateService(login);

        //Grab primary contact and a list of emergency contacts for user
        dh = new DatabaseHelper();
        dh.registerCallback(this);
        dh.getPrimaryContact(login);
        dh.getEmergencyContactList(login);

        // Register the local broadcast receiver to receive messages from the listener.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        messageReceiver = new HomePageActivity.MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
//        makePhoneCall(80);

    }

    private void startListenerService(String username){
        Intent mIntent = new Intent(this, ListenerService.class);
        mIntent.putExtra("login", username);
        this.startService(mIntent);

    }

    private void startHeartRateService(String username){
        Intent mIntent = new Intent(this, HeartRateService.class);
        mIntent.putExtra("login", username);
        this.startService(mIntent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hmpg:
                Toast.makeText(this, "Homepage", Toast.LENGTH_SHORT).show();
                Intent intt = new Intent(this, HomePageActivity.class);
                intt.putExtra("login", login);
                startActivity(intt);
                return true;
            case R.id.med_tracker:
                Toast.makeText(this, "Medication Tracker", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MedTrackerActivity.class);
                intent.putExtra("login", login);
                startActivity(intent);
                return true;
            case R.id.contact:
                Toast.makeText(this, "Emergency Contact", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, EmergencyContactActivity.class);
                intent2.putExtra("login", login);
                startActivity(intent2);
                return true;
            case R.id.info:
                Toast.makeText(this, "Personal Info", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, MedConditionActivity.class);
                intent3.putExtra("login", login);
                startActivity(intent3);
                return true;
            case R.id.acct:
                Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this, AccountActivity.class);
                startActivity(intent5);
                return true;
            case R.id.history:
                Toast.makeText(this, "Medication History", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, MainActivity.class);
                intent4.putExtra("login", login);
                startActivity(intent4);
                return true;
            case R.id.signout:
                Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void makePhoneCall(int heartRate)
    {
        if (heartRate >= 200 ||  heartRate <= -100)
        {
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
            //textContacts();

            //medicationMessage
            addNotification();
        }
    }

    public void textContacts()
    {
        String phoneNumber = primaryContact.getPhoneNumber();
        String text = "Am not ok, please help!";
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

    private void getMedConditions() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("medInfo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(login))
                {
                    medcond_ = (String) dataSnapshot.child("medcond").getValue().toString();
                    allergies_ = (String) dataSnapshot.child("allergies").getValue().toString();
                    curr_med_ = (String) dataSnapshot.child("curr_med").getValue().toString();
                    blood_type_ = (String) dataSnapshot.child("blood_type").getValue().toString();
                    other_ = (String) dataSnapshot.child("other").getValue().toString();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setProgressBar(int heartRate){
        ProgressBar pb = (ProgressBar)findViewById(R.id.circulaprogbar);
        if(heartRate < 60){
            pb.setProgress(30);
        }
        else if(60 <= heartRate && heartRate < 80){
            pb.setProgress(20);
        }
        else if(80 <= heartRate && heartRate < 100){
            pb.setProgress(10);
        }
        else{
            pb.setProgress(0);
        }
    }

    private void addNotification() {
        getMedConditions();
        Log.i("Start", "medicationMessage");

   /* Invoking the default medicationMessage service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("New Message");
        mBuilder.setContentText("You've received new message.");
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.drawable.newhrt);

   /* Increase medicationMessage number every time a new medicationMessage arrives */
        mBuilder.setNumber(++numMessages);

   /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[5];
        events[0] = new String("Medical condition: Diabetic");
        events[1] = new String("Allergies: Peanut");
        events[2] = new String("current Medication: none");
        events[3] = new String("Blood Type: A");
        events[4] = new String("other: none");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Medication Condition:");

        // Moves events into the big view
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        int notificationID = 990;

   /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this,MedConditionActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MedConditionActivity.class);

   /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

   /* notificationID allows you to update the medicationMessage later on. */
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

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("heartrate");
            Log.v("HomePageActivity", "received message: " + message);

            if (heartRate == null) {
                heartRate = findViewById(R.id.heartRate);
            }
            heartRate.setText(message);
        }
    }
}


