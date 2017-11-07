package com.example.android.healthwatch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MedTrackerActivity extends AppCompatActivity implements View.OnClickListener {


    FloatingActionButton floatingButton;
    private static MedTrackerAdapter adapter;
    ArrayList<MedModel> CustomListViewValuesArr;
    ListView listView;
    String allTime;
    String allDate;
    String medName;
    String dosage;
    String hour;
    String minute;
    Calendar calendar;
    String login;

    Intent myIntent;

    PendingIntent pendingIntent;
    AlarmManager alarm_manager;

    ToggleButton toggleButton;
    private static MedTrackerActivity inst;



    public static MedTrackerActivity instance() {
        return inst;
    }

 /*   @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_tracker);

        listView = (ListView)findViewById(R.id.listview);
        floatingButton = (FloatingActionButton)findViewById(R.id.fabButton);
        floatingButton.setOnClickListener(this);

        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        if(!intent.hasExtra("Not_Registered")){
            getMedications();
        }

        myIntent = new Intent(MedTrackerActivity.this, AlarmReceiver.class);

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        CustomListViewValuesArr = new ArrayList<>();
        CustomListViewValuesArr.add(new MedModel(login, "test", "test", "test"));

        displayMedications();
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
                    hour = extras.getString("HOUR");
                    minute = extras.getString("MIN");
                    storeMedication();
                    Log.d("name", medName + allTime + allDate + dosage + hour + minute);
                    CustomListViewValuesArr.add(new MedModel(allTime, allDate, medName, dosage));
                    displayMedications();

                }
                else{
                    Toast.makeText(MedTrackerActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void displayMedications(){
        adapter=new MedTrackerAdapter(CustomListViewValuesArr, getApplicationContext());
        listView.setAdapter( adapter );
    }

    public void storeMedication(){
        final String finalName = medName;
        final String finalTime = allTime;
        final String finalDate = allDate;
        final String finalDosage = dosage;


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("contacts").child(login).child(medName).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    usersRef.child(login).child(medName).setValue(new MedModel(allTime, allDate, dosage), new DatabaseReference.CompletionListener(){
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

    }

    public void conditionClick(View v)
    {
        startActivity(new Intent(MedTrackerActivity.this, MedConditionActivity.class));
    }

    @Override
    public void onClick(View v) {

        if(v == floatingButton)
        {
            Intent intent = new Intent(this, MedTrackerForm.class);
            startActivityForResult(intent, 1);
        }

    }

    public void turnAlarmOnOrOff(int id, boolean ck) {


        String n = Integer.toString(id);
            if (ck )
            {
                int hod = Integer.parseInt(hour);
                int mint = Integer.parseInt(minute);
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hod);
                calendar.set(Calendar.MINUTE, mint);
                Log.d("MyActivity", "Alarm ON " + n);
                myIntent.putExtra("extra", "alarm on");
                pendingIntent = PendingIntent.getBroadcast(MedTrackerActivity.this, id, myIntent, 0);
                alarm_manager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

            }
            else
            {
//                pendingIntent.cancel();
                alarm_manager.cancel(pendingIntent);
                Log.d("MyActivity", "Alarm OFF " + n);
                myIntent.putExtra("extra", "alarm off");
                sendBroadcast(myIntent);
            }
    }

    public void onItemClick(int mPosition)
    {
        MedModel tempValues = ( MedModel ) CustomListViewValuesArr.get(mPosition);

        Toast.makeText(this, " "+tempValues.getName()
                        +"time:"+tempValues.getTime()
                        +"date:"+tempValues.getDate()
                        +"dosage:"+tempValues.getDosage(),
                Toast.LENGTH_SHORT).show();
        Log.i("TAG NAME: " , tempValues.getName());

    }

}
