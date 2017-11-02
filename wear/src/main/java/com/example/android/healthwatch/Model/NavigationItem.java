package com.example.android.healthwatch.Model;

/**
 * Created by Yan Tan on 10/21/2017.
 */

public class NavigationItem {

    private String mTitle;
    private int mIcon;

    public NavigationItem(String title, int icon){
        mTitle = title;
        mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIcon() {
        return mIcon;
    }
}
