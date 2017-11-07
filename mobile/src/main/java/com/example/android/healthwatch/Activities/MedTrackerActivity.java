package com.example.android.healthwatch.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.healthwatch.AlarmReceiver;
import com.example.android.healthwatch.Adapters.MedCustomAdapter;
import com.example.android.healthwatch.Model.MedModel;
import com.example.android.healthwatch.R;

import java.util.ArrayList;
import java.util.Calendar;

public class MedTrackerActivity extends AppCompatActivity implements View.OnClickListener {


    FloatingActionButton floatingButton;
    MedCustomAdapter adapter;
    public ArrayList<MedModel> CustomListViewValuesArr = new ArrayList<MedModel>();
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

        displayMeds();
        Resources res =getResources();
        adapter=new MedCustomAdapter( this, CustomListViewValuesArr,res );
        listView.setAdapter( adapter );

        myIntent = new Intent(MedTrackerActivity.this, AlarmReceiver.class);

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);


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
            startActivityForResult(intent, 999);
            finish();
        }

    }

    public void displayMeds()
    {

        Intent intent = getIntent();
          if(intent.hasExtra("NAME"))
          {
              MedName = getIntent().getExtras().getString("NAME");
              allTime = getIntent().getExtras().getString("TIME");
              allDate = getIntent().getExtras().getString("DATE");
              Dosage = getIntent().getExtras().getString("DOSAGE");
              hour = getIntent().getExtras().getString("HOUR");
              minute = getIntent().getExtras().getString("MIN");
              Log.i("Name", MedName + allTime + allDate + Dosage + hour + minute);
              setListData();
          }

    }

    private void setListData() {

        final MedModel sched = new MedModel();
        sched.setName(MedName);
        sched.setDate(allDate);
        sched.setTime(allTime);
        sched.setDosage(Dosage);

        CustomListViewValuesArr.add( sched );
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
                        +"Time:"+tempValues.getTime()
                        +"Date:"+tempValues.getDate()
                        +"Dosage:"+tempValues.getDosage(),
                Toast.LENGTH_SHORT).show();
        Log.i("TAG NAME: " , tempValues.getName());

    }

}
