package com.example.android.healthwatch.Model;

/**
 * Created by faitholadele on 10/6/17.
 */

public class MedModel {

    private int id;
    String time;
    String date;
    String name;
    String medMessage;

    public MedModel()
    {

    }

    public MedModel(String name, String time, String date, String dosage, int id){
        this.time = time;
        this.date = date;
        this.name = name;
        this.medMessage = dosage;
        this.id = id;
    }

    public MedModel(String time, String date, String dosage, int id){
        this.time = time;
        this.date = date;
        this.medMessage = dosage;
        this.id = id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
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

        return medMessage;
    }

    public void setDosage(String dosage) {
        this.medMessage = dosage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
