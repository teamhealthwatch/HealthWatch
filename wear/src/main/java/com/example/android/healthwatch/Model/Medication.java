package com.example.android.healthwatch.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yan Tan on 9/30/2017.
 */

public class Medication implements Parcelable{

    private String medName;
    private String dosage;


    public Medication(String medName, String dosage) {
        this.medName = medName;
        this.dosage = dosage;
    }

    protected Medication(Parcel in) {
        medName = in.readString();
        dosage = in.readString();
    }

    public static final Creator<Medication> CREATOR = new Creator<Medication>() {
        @Override
        public Medication createFromParcel(Parcel in) {
            return new Medication(in);
        }

        @Override
        public Medication[] newArray(int size) {
            return new Medication[size];
        }
    };

    public String getMedName() {
        return medName;
    }

    public String getDosage() {
        return dosage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(medName);
        parcel.writeString(dosage);
    }
}
