package com.example.android.healthwatch;

/**
 * Created by Yan Tan on 9/30/2017.
 */

public class Medication {

    private String medName;
    private String dosage;

    public Medication(String medName, String dosage) {
        this.medName = medName;
        this.dosage = dosage;
    }

    public String getMedName() {
        return medName;
    }

    public String getDosage() {
        return dosage;
    }
}
