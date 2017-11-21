package com.example.android.healthwatch.Activities;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.DateAndTimeUtil;
import com.example.android.healthwatch.R;

import java.util.Calendar;

public class MedTrackerForm extends AppCompatActivity{

    ImageView setTime;
    ImageView setDate;
//    NumberPicker numberPicker = null;
    Calendar calendar;
    TextView actualTime;
    TextView actualDate;
    TextView alarmTextView;
    TextView medicationName;
    EditText notification;
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
        notification = (EditText) findViewById(R.id.notification_content);
        actualDate = (TextView)findViewById(R.id.actualDate);
//        numberPicker = (NumberPicker)findViewById(R.id.numberPicker);

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
        medInfo();
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
                allTime = buildTime(hourOfDay, minute);
                actualTime.setText(allTime);
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
                String m = Integer.toString(month+1);
                String day = Integer.toString(dayOfMonth);
                allDate = day + "/" + m +  "/" + y;
                actualDate.setText(allDate);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    public void medInfo()
    {

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
        intent.putExtras(bundle);

        Log.i("name", medName + allTime + allDate + dsg);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;
        Calendar nowCalendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));

        String medName = medicationName.getText().toString();
        if (TextUtils.isEmpty(medName)) {
            medicationName.setError("Required.");
            valid = false;
        } else {
            medicationName.setError(null);
        }

        String dateText = actualDate.getText().toString();
        if (dateText.equals("Today")) {

            String y = Integer.toString( nowCalendar.get(Calendar.YEAR));
            String m = Integer.toString(nowCalendar.get(Calendar.MONTH)+1);
            String day = Integer.toString(nowCalendar.get(Calendar.DAY_OF_MONTH));
            allDate = day + "/" + m +  "/" + y;
            actualDate.setText(allDate);
        }
        // check dates
        if (DateAndTimeUtil.toLongDateAndTime(calendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
            Toast.makeText(this, "Date cannot be in the past", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else {
            actualDate.setError(null);
        }

        String timeText = actualTime.getText().toString();
        if (timeText.equals("Now")) {
            calendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));
            allTime = buildTime(nowCalendar.get(Calendar.HOUR_OF_DAY), nowCalendar.get(Calendar.MINUTE));
            actualTime.setText(allTime);
        }
        else if(calendar.get(Calendar.HOUR_OF_DAY) < nowCalendar.get(Calendar.HOUR_OF_DAY))
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

    private String buildTime(int hour, int minute){

        String m = Integer.toString(minute);
        String format;

        if(hour <= 11)
        {
            format = "AM";
        }
        else if(hour == 12)
        {
            format = "PM";
        }
        else
        {
            hour = hour - 12;
            format = "PM";
        }
        if(minute < 10)
        {
            m = "0" + Integer.toString(minute);
        }
        String h = Integer.toString(hour);
        return new String (h + " : " + m + " " + format);
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
