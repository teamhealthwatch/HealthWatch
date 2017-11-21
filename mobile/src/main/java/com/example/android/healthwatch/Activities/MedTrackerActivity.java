package com.example.android.healthwatch.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.healthwatch.AlarmReceiver;
import com.example.android.healthwatch.Adapters.MedTrackerAdapter;
import com.example.android.healthwatch.AlarmUtil;
import com.example.android.healthwatch.DatabaseHelper;
import com.example.android.healthwatch.Model.MedModel;
import com.example.android.healthwatch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MedTrackerActivity extends AppCompatActivity implements View.OnClickListener {


    FloatingActionButton floatingButton;
    MedTrackerAdapter adapter;
    ArrayList<MedModel> medications;
    ListView listView;
    String allTime;
    String allDate;
    String medName;
    String dosage;
    String hour;
    String minute;
    Calendar calendar;
    Intent myIntent;

    //Declare authentication, used to know who is signed in
    private FirebaseAuth mAuth;

    String login;
    boolean firstTime;

    PendingIntent pendingIntent;
    AlarmManager alarm_manager;

    ToggleButton toggleButton;
    private static MedTrackerActivity inst;

    public static MedTrackerActivity instance()
    {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_tracker);

        listView = (ListView)findViewById(R.id.listview);
        floatingButton = (FloatingActionButton)findViewById(R.id.fabButton);
        floatingButton.setOnClickListener(this);

        //Initialize Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();

        //Used to tell if user is in registration phase or common use phase
        firstTime = false;
        Intent intent = getIntent();
        login = intent.getExtras().getString("login");
        if(!intent.hasExtra("Not_Registered")){
            getMedications();
        }
        else{
            firstTime = true;
        }
        //DatabaseHelper h = new DatabaseHelper();
        //int medId = h.getLastAlarmID(login);
        //if(medId == -1){

        //}


        myIntent = new Intent(MedTrackerActivity.this, AlarmReceiver.class);
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                if(extras != null){
                    medName = extras.getString("NAME");
                    allTime = extras.getString("TIME");
                    allDate = extras.getString("DATE");
                    dosage = extras.getString("DOSAGE");
                    buildAlarm(allTime, allDate, 3536);
                    storeMedication();
                }
                else{
                    Toast.makeText(MedTrackerActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void displayMedications(ArrayList<MedModel> m){
        adapter=new MedTrackerAdapter(this, m, getApplicationContext());
        listView.setAdapter(adapter);
    }

    public void conditionClick(View v)
    {
        startActivity(new Intent(MedTrackerActivity.this, MedConditionActivity.class));
    }


    public void storeMedication(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("medication").child(login).child(medName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Log.v("Children",""+ childDataSnapshot.getKey()); //displays the key for the node
                        //Log.v("Children",""+ childDataSnapshot.child("name").getValue());   //gives the value for given keyname
                    }
                    /*AlertDialog alertDialog = new AlertDialog.Builder(EmergencyContactActivity.this).create();
                    alertDialog.setTitle("Duplicate Contact");
                    alertDialog.setMessage("A person with that same name was found, please enter a different contact.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();*/
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersRef = database.getReference("medication");
                    usersRef.child(login).child(medName).setValue(new MedModel(allTime, allDate, dosage, 0), new DatabaseReference.CompletionListener(){
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("Data could not be saved " + databaseError.getMessage());
                            } else {
                                System.out.println("Data saved successfully.");
                            }
                        }

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getMedications(){

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        medications = new ArrayList<>();
        myRef.child("medication").child(login).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String medName = dataSnapshot.getKey();
                String medTime = (String) dataSnapshot.child("time").getValue().toString();
                String medDay = (String) dataSnapshot.child("date").getValue().toString();
                String medDosage = (String) dataSnapshot.child("dosage").getValue().toString();
                //int medId = Integer.parseInt(dataSnapshot.child("id").getValue().toString());

                MedModel m = new MedModel(medName,medTime,medDay,medDosage, 0);
                medications.add(m);
                displayMedications(medications);
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

    public void finishMedication(){
        Intent intent = new Intent(this, EmergencyInfo.class);
        intent.putExtra("login", login);
        intent.putExtra("Is_Registered", "TRUE");
        startActivity(intent);
    }

    public void buildAlarm(String allTime, String allDate, int pos){
        //Split allTime form of hh:mm AM/PM
        String[] parsedTime = allTime.split(" ");
        int hour = Integer.parseInt(parsedTime[0]);
        int minute = Integer.parseInt(parsedTime[2]);
        String format = parsedTime[3];
        if(format.equals("PM")){
            hour += 12;
        }
        //Split allDate form of dd/mm/yy
        String[] parsedDate = allDate.split("/");
        int day = Integer.parseInt(parsedDate[0]);
        int month = Integer.parseInt(parsedDate[1]);
        int year = Integer.parseInt(parsedDate[2]);
        //Build calendar
        calendar = calendar.getInstance();
        calendar.set(year, month-1, day, hour, minute, 0);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        AlarmUtil.setAlarm(this, alarmIntent, pos, calendar, "alarm on");

    }

    public void getAlarmPosition(int position){
        MedModel m = medications.get(position);
        String mDate = m.getDate();
        String mTime = m.getTime();
        buildAlarm(mTime, mDate, position);
    }

    public void cancelAlarm(int position){
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        AlarmUtil.cancelAlarm(this, alarmIntent, position);
        alarmIntent.putExtra("extra", "alarm off");
        sendBroadcast(alarmIntent);
    }

    public void onItemClick(int mPosition)
    {
        MedModel tempValues = ( MedModel ) medications.get(mPosition);

        Toast.makeText(this, " "+tempValues.getName()
                        +"Time:"+tempValues.getTime()
                        +"Date:"+tempValues.getDate()
                        +"Dosage:"+tempValues.getDosage(),
                Toast.LENGTH_SHORT).show();
        Log.i("TAG NAME: " , tempValues.getName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firstTime){
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.menu_next, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else{
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.menu, menu);
            return super.onCreateOptionsMenu(menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            case R.id.action_next:
                finishMedication();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {

        if(v == floatingButton)
        {
            Intent intent = new Intent(this, MedTrackerForm.class);
            intent.putExtra("New Alarm", "TRUE");
            startActivityForResult(intent, 1);
        }

    }

}
