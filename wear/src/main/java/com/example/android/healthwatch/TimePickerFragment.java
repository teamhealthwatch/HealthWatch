package com.example.android.healthwatch;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Yan Tan on 10/17/2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public TimePickerFragment(){

    }

    public static TimePickerFragment newInstance(String title){
        TimePickerFragment frag = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        Log.i("TimePickerFragment", "onTimeSet!");

        // Talk to activity
        MedInfoActivity medInfoActivity = (MedInfoActivity) getActivity();

        String format;

        if (hourOfDay == 0) {
            hourOfDay += 12;
            format = "AM";
        } else if (hourOfDay == 12) {
            format = "PM";
        } else if (hourOfDay > 12) {
            hourOfDay -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        String minuteStr = Integer.toString(minute);
        if (minute < 10){
            minuteStr = "0" + minuteStr;
        }

        String time = hourOfDay + ":" + minuteStr + " " + format;

//        medInfoActivity.addAlarm(time);
        medInfoActivity.setTempTime(time);
    }
}