package com.example.android.healthwatch.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormatSymbols;

import com.example.android.healthwatch.DateAndTimeUtil;
import com.example.android.healthwatch.Fragments.AlarmFragment;
import com.example.android.healthwatch.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.view.View.VISIBLE;

public class MedTrackerForm extends AppCompatActivity implements AlarmFragment.RepeatSelectionListener{

    ImageView setTime;
    ImageView setDate;
    ImageView repeat;
    Calendar calendar;
    TextView actualTime;
    TextView actualDate;
    TextView alarmTextView;
    TextView repeatText;
    TextView medicationName;
    EditText medicationMessage;
    String medMessage;
    String allTime;
    String allDate;
    String medName;
    String dayOfWeek;
    Boolean isDate;
    Boolean isReapet;
    Button deleteMedication;
    String reapetDays;
    String monthString;
    String position;
    String old_name;
    ArrayList<String> days = new ArrayList<String>();
    ArrayList<String> daysInfull = new ArrayList<String>();

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_form);

        medicationName = (TextView)findViewById(R.id.med_name);
        actualTime = (TextView)findViewById(R.id.actualTime);
        setDate = (ImageView)findViewById(R.id.date);
        setTime = (ImageView)findViewById(R.id.Alarm);
        repeat = (ImageView)findViewById(R.id.repeat_icon);
        repeatText = (TextView)findViewById(R.id.repeat_day);
        medicationMessage = (EditText) findViewById(R.id.notification_content);
        actualDate = (TextView)findViewById(R.id.actualDate);
        reapetDays = " ";
        position = " ";
        old_name = " ";
        isDate = true;
        isReapet = false;
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDate)
                {
                    selectDate();
                }

            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                selectDays();
            }
        });
        calendar = Calendar.getInstance();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null){
            medMessage = b.getString("msg");
            allTime = b.getString("time");
            allDate = b.getString("date");
            medName = b.getString("name");
            reapetDays = b.getString("days");
            position = b.getString("position");
            old_name =b.getString("old_name");

            displayForm();
        }
    }

    private void displayForm(){

        actualDate.setText(allDate);
        actualTime.setText(allTime);
        medicationName.setText(medName);
        medicationMessage.setText(medMessage);
        if(reapetDays.equals(" "))
        {
            repeatText.setText("repeat");
        }
        else
        {
            repeatText.setText(reapetDays);
        }
        deleteMedication = (Button) findViewById(R.id.deleteMed);
        deleteMedication.setVisibility(VISIBLE);
        deleteMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medMessage = " ";
                allTime = " ";
                allDate =  "";
                reapetDays ="";
                String delete = "true";

                Intent intent = new Intent(getApplicationContext(), MedTrackerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("NAME", medName);
                bundle.putString("TIME", allTime);
                bundle.putString("DATE", allDate);
                bundle.putString("MESSAGE", medMessage);
                bundle.putString("DAYS", reapetDays);
                bundle.putString("DELETE", delete);
                bundle.putString("POSITION", position);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void selectDays() {
        if(repeatText.equals("repeat"))
        {
            DialogFragment dialog =  new AlarmFragment();
            dialog.show(getSupportFragmentManager(), "AlarmFragment");
        }
        else
        {
//            DialogFragment dialog =  new AlarmFragment();
//            AlarmFragment al = AlarmFragment.newInstance("days");
//            al.show(getSupportFragmentManager(), "AlarmFragment");

            DialogFragment dialog =  new AlarmFragment();
            dialog.show(getSupportFragmentManager(), "AlarmFragment");
        }
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

                SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                Date date = new Date(year, month, dayOfMonth-1);
                monthString = new DateFormatSymbols().getMonths()[month-1];
                dayOfWeek = simpledateformat.format(date);
                allDate = day + "/" + m +  "/" + y;
                actualDate.setText(dayOfWeek.substring(0,3) + ", " + monthString.substring(0,3) + " " + day);
                Log.i("dayofweek", dayOfWeek);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
      datePicker.show();

    }

    public void getInfoForIntent()
    {
        medName = medicationName.getText().toString();
        Intent intent = new Intent(this, MedTrackerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("NAME", medName);
        bundle.putString("TIME", allTime);
        bundle.putString("DATE", allDate);
        bundle.putString("MESSAGE", medMessage);
        bundle.putString("DAYS", reapetDays);
        if(!position.equals(""))
        {
            bundle.putString("POSITION", position);
            bundle.putString("old_name", old_name);
        }
        intent.putExtras(bundle);

        Log.i("name", medName + allTime + allDate + medMessage + " " + reapetDays);
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

        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date(nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), nowCalendar.get(Calendar.DAY_OF_MONTH)-1);
        dayOfWeek = simpledateformat.format(date);
        monthString = new DateFormatSymbols().getMonths()[nowCalendar.get(Calendar.MONTH)-1];

        String medName = medicationName.getText().toString();
        if (TextUtils.isEmpty(medName)) {
            medicationName.setError("Required.");
            valid = false;
        } else {
            medicationName.setError(null);
        }

        String dateText = actualDate.getText().toString();
        if (dateText.equals("Today") && days.size() == 0) {

            String y = Integer.toString( nowCalendar.get(Calendar.YEAR));
            String m = Integer.toString(nowCalendar.get(Calendar.MONTH)+1);
            String day = Integer.toString(nowCalendar.get(Calendar.DAY_OF_MONTH));
            allDate = day + "/" + m +  "/" + y;
            actualDate.setText(dayOfWeek.substring(0,3) + "," + monthString.substring(0,3) + " " + day);
        }
        else if(dateText.equals("Today") && days.size() != 0)
        {
            if(daysInfull.contains(dayOfWeek))
            {
                String y = Integer.toString( nowCalendar.get(Calendar.YEAR));
                String m = Integer.toString(nowCalendar.get(Calendar.MONTH)+1);
                String day = Integer.toString(nowCalendar.get(Calendar.DAY_OF_MONTH));
                allDate = day + "/" + m +  "/" + y;
                actualDate.setText(dayOfWeek.substring(0));
            }
            else {

                int mofy = nowCalendar.get(Calendar.MONTH);
                int dayInt = nowCalendar.get(Calendar.DAY_OF_MONTH)+1;
                if(mofy != 1 || mofy != 3 || mofy != 5 || mofy != 8 || mofy != 10)
                {
                    while (dayInt <= 31)
                    {
                        Date date1 = new Date(nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), dayInt-1);
                        dayOfWeek = simpledateformat.format(date1);
                        if(daysInfull.contains(dayOfWeek))
                        {
                            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
                            calendar.set(Calendar.DAY_OF_MONTH, dayInt);
                            String y = Integer.toString(nowCalendar.get(Calendar.YEAR));
                            String m = Integer.toString(nowCalendar.get(Calendar.MONTH)+1);
                            String day = Integer.toString(dayInt);
                            allDate = day + "/" + m +  "/" + y;
                            actualDate.setText(dayOfWeek.substring(0));
                            break;
                        }
                        dayInt++;
                    }
                    dayInt = nowCalendar.get(Calendar.DAY_OF_MONTH)+1;
                }
                else if(mofy != 1 || mofy == 3 || mofy == 5 || mofy == 8 || mofy == 10)
                {
                    while (dayInt <= 30)
                    {
                        Date date1 = new Date(nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), dayInt-1);
                        dayOfWeek = simpledateformat.format(date1);
                        if(daysInfull.contains(dayOfWeek))
                        {
                            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
                            calendar.set(Calendar.DAY_OF_MONTH, dayInt);
                            String y = Integer.toString(nowCalendar.get(Calendar.YEAR));
                            String m = Integer.toString(nowCalendar.get(Calendar.MONTH)+1);
                            String day = Integer.toString(dayInt);
                            allDate = day + "/" + m +  "/" + y;
                            actualDate.setText(dayOfWeek.substring(0));
                            break;
                        }
                        dayInt++;
                    }
                    dayInt = nowCalendar.get(Calendar.DAY_OF_MONTH)+1;
                }
                else if(mofy == 1)
                {
                    while (dayInt <= 28)
                    {
                        Date date1 = new Date(nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), dayInt-1);
                        dayOfWeek = simpledateformat.format(date1);
                        if(daysInfull.contains(dayOfWeek))
                        {
                            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
                            calendar.set(Calendar.DAY_OF_MONTH, dayInt);
                            String y = Integer.toString(nowCalendar.get(Calendar.YEAR));
                            String m = Integer.toString(nowCalendar.get(Calendar.MONTH)+1);
                            String day = Integer.toString(dayInt);
                            allDate = day + "/" + m +  "/" + y;
                            actualDate.setText(dayOfWeek.substring(0));
                            break;
                        }
                        dayInt++;
                    }
                    dayInt = nowCalendar.get(Calendar.DAY_OF_MONTH)+1;
                }
                Log.i("","");
            }
        }
        // check dates
        if (DateAndTimeUtil.toLongDateAndTime(calendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
            Toast.makeText(this, "Date cannot be in the past", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else
        {
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
        medMessage = medicationMessage.getText().toString();
        if(medMessage == null){
            medMessage = "";
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

    @Override
    public void onRepeatSelection(ArrayList items) {

      for(int i = 0; i < items.size(); i++)
      {
          int j = (int) items.get(i);
          String day = Integer.toString(j);
          Log.i("Days", day);
          if(j == 0)
          {
              // Sunday == 0
              if(!days.contains("S"))
              {
                  days.add("S");
                  daysInfull.add("Sunday");
              }

          }
          else if( j == 1)
          {
              // Monday == 1
              if(!days.contains("M"))
              {
                  days.add("M");
                  daysInfull.add("Monday");
              }

          }
          else if( j == 2)
          {
              // Tuesday == 2
              if(!days.contains("T"))
              {
                  days.add("T");
                  daysInfull.add("Tuesday");
              }

          }
          else if( j == 3)
          {
              // Wednesday == 3
              if(!days.contains("W"))
              {
                  days.add("W");
                  daysInfull.add("Wednesday");
              }

          }
          else if( j == 4)
          {
              // Thursday == 4
              if(!days.contains("Th"))
              {
                  days.add("Th");
                  daysInfull.add("Thursday");
              }

          }
          else if( j == 5)
          {
              // Friday == 5
              if(!days.contains("F"))
              {
                  days.add("F");
                  daysInfull.add("Friday");
              }

          }
          else if( j == 6)
          {
              // Saturday == 5
              if(!days.contains("Sa"))
              {
                  days.add("Sa");
                  daysInfull.add("Saturday");
              }

          }
      }

        if(days.size() != 0)
        {
            String allReapetDays = " ";
            for(int i = 0; i < days.size(); i++) {
                allReapetDays = allReapetDays + days.get(i) + " ";
            }
            repeatText.setText(allReapetDays);
            reapetDays = allReapetDays;
            isDate = false;
        }

    }
}
