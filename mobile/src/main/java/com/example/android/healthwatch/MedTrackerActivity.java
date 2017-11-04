package com.example.android.healthwatch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MedTrackerActivity extends AppCompatActivity implements View.OnClickListener {


    FloatingActionButton floatingButton;
    private static MedCustomAdapter adapter;
    ArrayList<MedModel> CustomListViewValuesArr;
    ListView listView;
    String allTime;
    String allDate;
    String MedName;
    String Dosage;
    String hour;
    String minute;
    Calendar calendar;

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

        myIntent = new Intent(MedTrackerActivity.this, AlarmReceiver.class);

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        CustomListViewValuesArr = new ArrayList<MedModel>();
        CustomListViewValuesArr.add(new MedModel("test", "test", "test", "test"));
        adapter=new MedCustomAdapter(CustomListViewValuesArr, getApplicationContext());
        listView.setAdapter( adapter );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                if(extras != null){
                    MedName = extras.getString("NAME");
                    allTime = extras.getString("TIME");
                    allDate = extras.getString("DATE");
                    Dosage = extras.getString("DOSAGE");
                    hour = extras.getString("HOUR");
                    minute = extras.getString("MIN");
                    Log.i("name", MedName + allTime + allDate + Dosage + hour + minute);
                    CustomListViewValuesArr.add(new MedModel(allTime, allDate, MedName, Dosage));

                }
                else{
                    Toast.makeText(MedTrackerActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public void conditionClick(View v)
    {
        startActivity(new Intent(MedTrackerActivity.this, MedConditionActivity.class));
    }

    @Override
    public void onClick(View v) {

        if(v == floatingButton)
        {
            Intent intent = new Intent(this, MedicationForm.class);
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
