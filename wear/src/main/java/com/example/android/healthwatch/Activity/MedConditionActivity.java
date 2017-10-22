package com.example.android.healthwatch.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.AdapterView;

import com.example.android.healthwatch.Adapter.MedAdapter;
import com.example.android.healthwatch.Medication;
import com.example.android.healthwatch.R;

import java.util.ArrayList;

public class MedConditionActivity extends WearableActivity{

    private WearableRecyclerView wearableRecyclerView;
    private MedAdapter medAdapter;
    private ArrayList<Medication> medication;



    private AdapterView.OnItemClickListener onItemClickListener;
    private MedAdapter.MedClickListener medClickListener;

    public static final String MED_ITEM ="med item";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_condition);

        medication = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            medication.add(new Medication("medication"+ i, "dosage" + i));
        }



        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableRecyclerView = stub.findViewById(R.id.med_recycler_view);
//                wearableRecyclerView.setEdgeItemsCenteringEnabled(true);
                wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(MedConditionActivity.this));



                medClickListener = new MedAdapter.MedClickListener() {
                    @Override
                    public void onMedClickListener(int pos) {
                        Log.i("MedClickListener", "clicked " + pos);
                        Intent newIntent = new Intent(MedConditionActivity.this, MedInfoActivity.class);

                        newIntent.putExtra(MED_ITEM, medication.get(pos));
                        startActivity(newIntent);
                    }
                };

                medAdapter = new MedAdapter(medication, MedConditionActivity.this, medClickListener);

                wearableRecyclerView.setAdapter(medAdapter);
                wearableRecyclerView.setFocusable(true);



            }
        });


    }


}
