package com.example.android.healthwatch.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.healthwatch.Adapter.AlarmAdapter;
import com.example.android.healthwatch.DatePickerFragment;
import com.example.android.healthwatch.Medication;
import com.example.android.healthwatch.R;
import com.example.android.healthwatch.TimePickerFragment;

import java.util.ArrayList;

public class MedInfoActivity extends WearableActivity {

    private TextView medNameView;
    private TextView medDosageView;

    public static final String MED_ITEM ="med item";

    private Button timeButton;
//    private Button dateButton;

    private RecyclerView alarmRecyclerView;

    private AlarmAdapter alarmAdapter;

    private ArrayList<String> alarmList;

    private String tempTime;

    private android.app.FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_info);

        alarmList = new ArrayList<>();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                medNameView = findViewById(R.id.med_name);
                medDosageView = findViewById(R.id.med_dosage);

                Intent intent = getIntent();

                Medication medication = intent.getParcelableExtra(MED_ITEM);

                medNameView.setText(medication.getMedName());
                medDosageView.setText(medication.getDosage());

                timeButton = findViewById(R.id.time_button);
//                dateButton = findViewById(R.id.date_button);

                timeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimePicker();
                    }
                });

//                dateButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        showDatePicker();
//                    }
//                });

                alarmRecyclerView = findViewById(R.id.alarm_recycler_view);
                alarmRecyclerView.setLayoutManager(new LinearLayoutManager(MedInfoActivity.this));

                // Create adapter for alarm list

                alarmAdapter = new AlarmAdapter(alarmList, MedInfoActivity.this);
                alarmRecyclerView.setAdapter(alarmAdapter);
                alarmRecyclerView.setFocusable(true);

                // TODO: Add listener




            }
        });
    }

    private void showTimePicker(){
        fm = getFragmentManager();
        TimePickerFragment timePickerFragment = TimePickerFragment.newInstance("Some Title");
        timePickerFragment.show(fm, "fragment_edit_name");
    }

    private void showDatePicker(){
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance("Some Title");
        datePickerFragment.show(fm, "fragment_edit_name");
    }

    public ArrayList<String> getAlarmList() {
        return alarmList;
    }

    public void addAlarm(String alarm){

        tempTime += "\n" + alarm;
        alarmList.add(tempTime);
        alarmAdapter.notifyDataSetChanged();
    }

    public void setTempTime(String tempTime) {
        this.tempTime = tempTime;
        showDatePicker();
    }
}
