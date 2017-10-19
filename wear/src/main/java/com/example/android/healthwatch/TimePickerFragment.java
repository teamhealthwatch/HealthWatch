package com.example.android.healthwatch;

import android.app.AlertDialog;
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

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private String newTime;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.BUTTON_POSITIVE
                ,this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        Log.i("TimePickerDialog", "onTimeSet!");

        String AM_PM ;
        if(i < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }

        newTime = i + ":" + i1 + " " + AM_PM;


    }

    public String getNewTime() {
        return newTime;
    }
}
