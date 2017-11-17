package com.example.android.healthwatch.Model;

import java.util.ArrayList;

/**
 * Created by Ryan on 9/28/2017.
 */

public class Contact {

    public String name;
    public String phoneNumber;
    public boolean primaryContact;
    public ArrayList<Contact> contactList;

    public Contact(String name, String phoneNumber, boolean primaryContact){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.primaryContact = primaryContact;
    }

    public Contact(String phoneNumber, boolean primaryContact){
        this.name = null;
        this.phoneNumber = phoneNumber;
        this.primaryContact = primaryContact;
    }

    public String getName(){
        return name;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }
}
