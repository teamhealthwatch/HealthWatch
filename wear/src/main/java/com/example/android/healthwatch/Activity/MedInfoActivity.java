package com.example.android.healthwatch.Activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.Adapter.AlarmAdapter;
import com.example.android.healthwatch.DatePickerFragment;
import com.example.android.healthwatch.Model.Medication;
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

    private AlarmAdapter.AlarmClickListener alarmClickListener;

    private int currentAlarmIndex;

    public static final int notificationId = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_med_info);
        showAlarm();

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

                currentAlarmIndex = -1;

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

                alarmClickListener = new AlarmAdapter.AlarmClickListener() {
                    @Override
                    public void onEditClickListener(View v, int pos) {
                        Log.d("TAG", "editButton at position " + pos);
                        currentAlarmIndex = pos;
                        showTimePicker();
                    }

                    @Override
                    public void onDeleteClickListener(View v, int pos) {
                        Log.d("TAG", "deleteButton at position " + pos);

                        alarmList.remove(pos);
                        alarmAdapter.notifyItemRemoved(pos);
                        alarmAdapter.notifyItemRangeChanged(pos, alarmList.size());
                    }
                };

                alarmAdapter = new AlarmAdapter(alarmList, MedInfoActivity.this, alarmClickListener);
                alarmRecyclerView.setAdapter(alarmAdapter);
                alarmRecyclerView.setFocusable(true);

                // Add listener to alarm


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

        if (currentAlarmIndex < 0) {
            alarmList.add(tempTime);

        } else {
            alarmList.set(currentAlarmIndex, tempTime);
            currentAlarmIndex = -1;
        }

        alarmAdapter.notifyDataSetChanged();
    }

    public void setTempTime(String tempTime) {
        this.tempTime = tempTime;
        showDatePicker();
    }

    private void showAlarm() {
        Toast.makeText(getApplicationContext(), "show alarm!", Toast.LENGTH_SHORT).show();
        // The channel ID of the notification.
        String id = "my_channel_01";


        // Build intent for notification content
        Intent viewIntent = new Intent(this, AlarmNotificationActivity.class);
//        viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Notification channel ID is ignored for Android 7.1.1
        // (API level 25) and lower.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, id)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("title")
                        .setContentText("content")
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Issue the notification with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
