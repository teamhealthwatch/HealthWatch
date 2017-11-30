package com.example.android.healthwatch.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by faitholadele on 11/28/17.
 */

public class MedInfoModel implements Serializable {

    public String medcond;
    public String allergies;
    public String curr_med;
    public String blood_type;
    public String other;

    public MedInfoModel(String medcond, String allergies, String curr_med, String blood_type, String other)
    {
        this.medcond = medcond;
        this.allergies = allergies;
        this.curr_med = curr_med;
        this.blood_type = blood_type;
        this.other = other;
    }

    public String getMedcond() {
        return medcond;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getCurr_med() {
        return curr_med;
    }

    public String getBlood_type() {
        return blood_type;
    }

    public String getOther() {
        return other;
    }
}
