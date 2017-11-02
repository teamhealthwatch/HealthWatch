package com.example.android.healthwatch;

import android.widget.Button;

/**
 * Created by faitholadele on 10/6/17.
 */

public class MedModel {

    String Time;
    String Date;
    String Name;
    String Dosage;
    String AlarmButton;

    public MedModel()
    {

    }

    public String getAlarmButton() {
        return AlarmButton;
    }

    public void setAlarmButton(String alarmButton) {
        AlarmButton = alarmButton;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDosage() {

        return Dosage;
    }

    public void setDosage(String dosage) {
        Dosage = dosage;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
