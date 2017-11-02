package com.example.android.healthwatch.Model;

/**
 * Created by Yan Tan on 10/22/2017.
 */

public class EmergencyContact {

    private String name;
    private String phoneNum;
    private boolean isPrimary;

    public EmergencyContact(String name, String phoneNum, boolean isPrimary) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.isPrimary = isPrimary;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public boolean isPrimary() {
        return isPrimary;
    }
}
