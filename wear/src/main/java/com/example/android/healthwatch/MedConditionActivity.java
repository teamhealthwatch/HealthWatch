package com.example.android.healthwatch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableRecyclerView;
import android.widget.TextView;

import com.google.android.gms.wearable.Wearable;

public class MedConditionActivity extends WearableActivity {

    private WearableRecyclerView wearableRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_condition);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableRecyclerView = (WearableRecyclerView) stub.findViewById(R.id.med_recycler_view);
                wearableRecyclerView.setCenterEdgeItems(true);
                wearableRecyclerView.setLayoutManager(new LinearLayoutManager(MedConditionActivity.this));

            }
        });

//        wearableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
