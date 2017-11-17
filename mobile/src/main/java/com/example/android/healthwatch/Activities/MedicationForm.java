package com.example.android.healthwatch.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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

import com.example.android.healthwatch.DateAndTimeUtil;
import com.example.android.healthwatch.R;

import java.util.Calendar;

public class MedicationForm extends AppCompatActivity {

    int hod;
    int mint;

    ImageView setTime;
    ImageView setDate;
    NumberPicker numberPicker = null;
    Calendar calendar;
    TextView actualTime;
    TextView actualDate;
    TextView alarmTextView;
    TextView MedicationName;
    TextView DosageText;
    String allTime;
    String allDate;
    String MedName;
    String Dsg;
    int ChoosenHour;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_form);


        MedicationName = (TextView)findViewById(R.id.med_name);
        actualTime = (TextView)findViewById(R.id.actualTime);
        setDate = (ImageView)findViewById(R.id.date);
        setTime = (ImageView)findViewById(R.id.Alarm);
        alarmTextView = (TextView)findViewById(R.id.alarmText);
        DosageText = (TextView)findViewById(R.id.Dosagetxt);
        actualDate = (TextView)findViewById(R.id.actualDate);
        numberPicker = (NumberPicker)findViewById(R.id.numberPicker);

        selectDosage();
        calendar = Calendar.getInstance();


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
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void selectTime()
    {

        TimePickerDialog TimePicker = new TimePickerDialog(MedicationForm.this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
            {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                ChoosenHour = hourOfDay;
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
//                Log.i("Time", allTime);
                actualTime.setText(allTime);
                hod = hourOfDay;
                mint = minute;
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        TimePicker.show();
    }

    public void selectDate()
    {
        DatePickerDialog DatePicker = new DatePickerDialog(MedicationForm.this, new DatePickerDialog.OnDateSetListener() {
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
        DatePicker.show();
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
                Log.i("Dosage: ", val);
                Dsg = val;
                DosageText.setText(val);
            }
        });
    }

    public  void getInfoForIntent()
    {
        MedName = MedicationName.getText().toString();
        Intent intent = new Intent(this, MedTrackerActivity.class);
        intent.putExtra("NAME", MedName);
        intent.putExtra("TIME", allTime);
        intent.putExtra("DATE", allDate);
        intent.putExtra("DOSAGE", Dsg);
        intent.putExtra("HOUR", Integer.toString(hod));
        intent.putExtra("MIN", Integer.toString(mint));

        Log.i("Name", MedName + allTime + allDate + Dsg);
        startActivity(intent);
    }

    private boolean validateForm() {
        boolean valid = true;
        Calendar nowCalendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));

        String medName = MedicationName.getText().toString();
        if (TextUtils.isEmpty(medName)) {
            MedicationName.setError("Required.");
            valid = false;
        } else {
            MedicationName.setError(null);
        }

        String dateText = actualDate.getText().toString();
        if (dateText.equals("Today")) {

            String y = Integer.toString( nowCalendar.get(Calendar.YEAR));
            String m = Integer.toString(nowCalendar.get(Calendar.MONTH));
            String day = Integer.toString(nowCalendar.get(Calendar.DAY_OF_MONTH));

            allDate = day + "/" + m +  "/" + y;
            actualDate.setText(allDate);
        }
        // check dates
        else if (DateAndTimeUtil.toLongDateAndTime(calendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
            Toast.makeText(this, "date cannot be in the past", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else {
            actualDate.setError(null);
        }

        String timeText = actualTime.getText().toString();
        if (timeText.equals("Time")) {
            actualTime.setError("Required.");
            valid = false;
        }
        else if(ChoosenHour < nowCalendar.get(Calendar.HOUR_OF_DAY))
        {
            String y = Integer.toString( nowCalendar.get(Calendar.YEAR));
            String m = Integer.toString(nowCalendar.get(Calendar.MONTH));
            String day = Integer.toString(nowCalendar.get(Calendar.DAY_OF_MONTH) + 1);

            allDate = day + "/" + m +  "/" + y;
            actualDate.setText(allDate);
        }
        else {
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
