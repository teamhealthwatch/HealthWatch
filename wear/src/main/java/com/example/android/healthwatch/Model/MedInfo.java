package com.example.android.healthwatch.Model;

/**
 * Created by Yan Tan on 12/9/2017.
 */

public class MedInfo {

    private String title;
    private String input;

    public MedInfo(String title, String input) {
        this.title = title;
        this.input = input;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getTitle() {
        return title;
    }

    public String getInput() {
        return input;
    }
}
