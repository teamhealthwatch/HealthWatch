package com.example.android.healthwatch.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;

import com.example.android.healthwatch.NavigationItem;

import java.util.ArrayList;

/**
 * Created by Yan Tan on 10/21/2017.
 */

public class NavigationAdapter
        extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter {

    Context mContext;
    ArrayList<NavigationItem> mItemList;

    public NavigationAdapter(Context context, ArrayList<NavigationItem> itemList) {
        mContext = context;
        mItemList = itemList;
    }

    @Override
    public CharSequence getItemText(int pos) {
        return mItemList.get(pos).getTitle();
    }

    @Override
    public Drawable getItemDrawable(int pos) {
        return mContext.getDrawable(mItemList.get(pos).getIcon());
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

}
