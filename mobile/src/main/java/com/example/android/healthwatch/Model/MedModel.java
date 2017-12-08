package com.example.android.healthwatch.Model;

/**
 * Created by faitholadele on 10/6/17.
 */

public class MedModel {

    private int id;
    private String time;
    private String date;
    private String name;
    private String medMessage;
    private String repeatDays;


    public MedModel()
    {

    }

    public MedModel(String name, String time, String date, String dosage, int id, String repeatDays){
        this.time = time;
        this.date = date;
        this.name = name;
        this.medMessage = dosage;
        this.id = id;
        this.repeatDays = repeatDays;
    }

    public MedModel(String time, String date, String dosage, int id, String repeatDays){
        this.time = time;
        this.date = date;
        this.medMessage = dosage;
        this.id = id;
        this.repeatDays = repeatDays;
    }

    public MedModel(String name, String time, String date, String medMessage, String repeatDays){
        this.name = name;
        this.time = time;
        this.date = date;
        this.medMessage = medMessage;
        this.repeatDays = repeatDays;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMedMessage() {
        return medMessage;
    }

    public void setMedMessage(String medMessage) {
        this.medMessage = medMessage;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }


}
