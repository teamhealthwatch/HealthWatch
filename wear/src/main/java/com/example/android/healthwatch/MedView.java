package com.example.android.healthwatch;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Calendar;

public class MedView extends WearableActivity {

    private TextView medNameView;
    private TextView medDosageView;

    public static final String MED_ITEM ="med item";

    private Button timeButton;
    private Button dateButton;

    private RecyclerView alarmRecyclerView;

    private AlarmAdapter alarmAdapter;

    private ArrayList<String> alarmList;

    private Calendar calendar;

    private int selectedHour;
    private int selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_view);

        alarmList = new ArrayList<>();

//        for (int i = 0; i < 10; i++){
//            alarmList.add("12:0" + i + " PM");
//        }

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
                dateButton = findViewById(R.id.date_button);

                timeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Log.i("TIME ICON", "time icon is clicked!");

                        TimePickerDialog timePickerDialog = new TimePickerDialog(MedView.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                Log.i("TimePickerDialog", "onTimeSet is called!");

                            }
                        }, 0, 0, true);
                        timePickerDialog.show();

//                        showTimePicker();

                    }
                });

                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDatePicker();
                    }
                });

                alarmRecyclerView = findViewById(R.id.alarm_recycler_view);
                alarmRecyclerView.setLayoutManager(new LinearLayoutManager(MedView.this));

                // Create adapter for alarm list

                alarmAdapter = new AlarmAdapter(alarmList, MedView.this);
                alarmRecyclerView.setAdapter(alarmAdapter);
                alarmRecyclerView.setFocusable(true);

                // TODO: Add listener




            }
        });
    }

    private void showTimePicker(){

        // TODO: Why is onTimeSet never called??!?!??!?!?
        calendar = Calendar.getInstance();
        selectedHour = calendar.get(calendar.HOUR_OF_DAY);
        selectedMinute =calendar.get(calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MedView.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {


            }
        }, 12, 0, false);
        timePickerDialog.show();

    }

    private void showDatePicker(){

        DatePickerDialog datePickerDialog = new DatePickerDialog(MedView.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            }
        }, 2017, 1, 1);
        datePickerDialog.show();

    }
}
