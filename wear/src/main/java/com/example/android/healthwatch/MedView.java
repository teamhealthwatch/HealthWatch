package com.example.android.healthwatch;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class MedView extends Activity {

    private TextView medNameView;
    private TextView medDosageView;

    public static final String MED_ITEM ="med item";

    private TimePicker timePicker;

    private Button timeButton;
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_view);
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
                        showTimePicker();
                    }
                });

                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDatePicker();
                    }
                });


            }
        });
    }

    private void showTimePicker(){

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
