package com.example.android.healthwatch;

/**
 * Created by faitholadele on 10/6/17.
 */

public class MedModel {

    String time;
    String date;
    String name;
    String dosage;

    public MedModel()
    {

    }

    public MedModel(String time, String date, String name, String dosage){
        this.time = time;
        this.date = date;
        this.name = name;
        this.dosage = dosage;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDosage() {

        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
