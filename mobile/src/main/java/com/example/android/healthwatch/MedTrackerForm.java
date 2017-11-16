package com.example.android.healthwatch;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MedTrackerForm extends AppCompatActivity{

    int hod;
    int mint;

    ImageView setTime;
    ImageView setDate;
    NumberPicker numberPicker = null;
    Calendar calendar;
    TextView actualTime;
    TextView actualDate;
    TextView alarmTextView;
    TextView medicationName;
    TextView dosageText;
    String allTime;
    String allDate;
    String medName;
    String dsg;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_form);


        medicationName = (TextView)findViewById(R.id.med_name);
        actualTime = (TextView)findViewById(R.id.actualTime);
        setDate = (ImageView)findViewById(R.id.date);
        setTime = (ImageView)findViewById(R.id.Alarm);
        alarmTextView = (TextView)findViewById(R.id.alarmText);
        dosageText = (TextView)findViewById(R.id.Dosagetxt);
        actualDate = (TextView)findViewById(R.id.actualDate);
        numberPicker = (NumberPicker)findViewById(R.id.numberPicker);

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });
        selectDosage();
        calendar = Calendar.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void selectTime()
    {

        TimePickerDialog timePicker = new TimePickerDialog(MedTrackerForm.this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
            {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
//                calendar.set(Calendar.AM_PM, Calendar.PM);
                String hour = Integer.toString(hourOfDay);
                String m = Integer.toString(minute);
                String formart;

                if(hourOfDay == 0)
                {
                    hour = Integer.toString(hourOfDay+12);
                    formart = "AM";
                }
                else if(hourOfDay == 12)
                {
                    formart = "PM";
                }
                else if(hourOfDay > 12)
                {
                    hour = Integer.toString(hourOfDay-12);
                    formart = "PM";
                }
                else
                {
                    formart = "AM";
                }

                if(minute < 10)
                {
                    m = "0" + Integer.toString(minute);
                }

                allTime = hour + " : " + m + " " +formart;
//                Log.i("time", allTime);
                actualTime.setText(allTime);
                hod = hourOfDay;
                mint = minute;
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        timePicker.show();
    }

    public void selectDate()
    {
        DatePickerDialog datePicker = new DatePickerDialog(MedTrackerForm.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker DatePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String y = Integer.toString(year);
                String m = Integer.toString(month);
                String day = Integer.toString(dayOfMonth);
                allDate = day + "/" + m +  "/" + y;
                actualDate.setText(allDate);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    public void selectDosage()
    {
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                String val = Integer.toString(newVal);
                Log.i("dosage: ", val);
                dsg = val;
                dosageText.setText(val);
            }
        });
    }

    public void getInfoForIntent()
    {
        medName = medicationName.getText().toString();
        Intent intent = new Intent(this, MedTrackerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("NAME", medName);
        bundle.putString("TIME", allTime);
        bundle.putString("DATE", allDate);
        if(dsg == null){
            dsg = "";
        }
        bundle.putString("DOSAGE", dsg);
        bundle.putString("HOUR", Integer.toString(hod));
        bundle.putString("MIN", Integer.toString(mint));
        intent.putExtras(bundle);

        Log.i("name", medName + allTime + allDate + dsg);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        String medName = medicationName.getText().toString();
        if (TextUtils.isEmpty(medName)) {
            medicationName.setError("Required.");
            valid = false;
        } else {
            medicationName.setError(null);
        }

        String dateText = actualDate.getText().toString();
        if (dateText.equals("Date")) {
            actualDate.setError("Required.");
            valid = false;
        } else {
            actualDate.setError(null);
        }

        String timeText = actualTime.getText().toString();
        if (timeText.equals("Time")) {
            actualTime.setError("Required.");
            valid = false;
        } else {
            actualTime.setError(null);
        }

        return valid;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if(!validateForm()){
                    return true;
                }
                Toast.makeText(this, "Saved =)", Toast.LENGTH_SHORT).show();
                getInfoForIntent();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}