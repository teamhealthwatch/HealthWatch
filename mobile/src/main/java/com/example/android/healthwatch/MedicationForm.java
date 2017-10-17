package com.example.android.healthwatch;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MedicationForm extends AppCompatActivity {


    // Alarm Stuff
    AlarmManager alarm_manager;
    private PendingIntent pendingIntent;
    private static MedicationForm inst;

    int hod;
    int mint;

    ImageView setTime;
    ImageView setDate;
    NumberPicker numberPicker = null;
    Calendar calendar;
    TextView actualTime;
    TextView actualDate;
    TextView MedicationName;
    TextView DosageText;
    String allTime;
    String allDate;
    String MedName;
    String Dsg;

    public static MedicationForm instance() {
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
        setContentView(R.layout.activity_medication_form);


        MedicationName = (TextView)findViewById(R.id.med_name);
        actualTime = (TextView)findViewById(R.id.actualTime);
        setDate = (ImageView)findViewById(R.id.date);
        setTime = (ImageView)findViewById(R.id.Alarm);
        DosageText = (TextView)findViewById(R.id.Dosagetxt);
        actualDate = (TextView)findViewById(R.id.actualDate);
        numberPicker = (NumberPicker)findViewById(R.id.numberPicker);

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

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

    public void selectTime()
    {

        TimePickerDialog TimePicker = new TimePickerDialog(MedicationForm.this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
            {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String hour = Integer.toString(hourOfDay);
                String m = Integer.toString(minute);

                if(hourOfDay > 12)
                {
                    hour = Integer.toString(hourOfDay-12);
                }

                if(minute < 10)
                {
                    m = "0" + Integer.toString(minute);
                }
                allTime = hour + " : " + m;
//                Log.i("Time", allTime);
                actualTime.setText(allTime);
                hod = hourOfDay;
                mint = minute;
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        TimePicker.show();
    }

    private void choosenTime(int hod, int mint) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hod);
        cal.set(Calendar.MINUTE, mint);
        Intent myIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        alarm_manager.set(AlarmManager.RTC, cal.getTimeInMillis(), pendingIntent);

        //TODO: logic to cancel alarm
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

        Log.i("Name", MedName + allTime + allDate + Dsg);

        //set time
        choosenTime(hod, mint);
        startActivity(intent);
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
                Toast.makeText(this, "Saved =)", Toast.LENGTH_SHORT).show();
                getInfoForIntent();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
