package com.example.android.healthwatch.Model;

import java.io.Serializable;

/**
 * Created by Ryan on 9/28/2017.
 */

public class Contact implements Serializable {

    public String name;
    public String phoneNumber;
    public boolean primary;

    public Contact(String name, String phoneNumber, boolean primaryContact) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.primary = primaryContact;
    }

    public Contact(String phoneNumber, boolean primaryContact) {
        this.name = null;
        this.phoneNumber = phoneNumber;
        this.primary = primaryContact;
    }


    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean getPrimary() {
        return primary;
    }
}
