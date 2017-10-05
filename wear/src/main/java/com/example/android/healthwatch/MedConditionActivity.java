package com.example.android.healthwatch;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class MedConditionActivity extends WearableActivity {

    private WearableRecyclerView wearableRecyclerView;
    private MedAdapter medAdapter;
    private ArrayList<Medication> medication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_condition);

        medication = new ArrayList<Medication>();

        for (int i = 0; i < 10; i++){
            medication.add(new Medication("medication"+ i, "dosage" + i));
        }



        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableRecyclerView = (WearableRecyclerView) stub.findViewById(R.id.med_recycler_view);
//                wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
                wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(MedConditionActivity.this));

                medAdapter = new MedAdapter(medication, MedConditionActivity.this);

                wearableRecyclerView.setAdapter(medAdapter);

            }
        });


    }

}
