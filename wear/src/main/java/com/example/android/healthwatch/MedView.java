package com.example.android.healthwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class MedView extends Activity {

    private TextView medNameView;
    private TextView medDosageView;

    public static final String MED_ITEM ="med item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_view);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                medNameView = findViewById(R.id.med_name);
                medDosageView = findViewById(R.id.med_dosage);

                Intent intent = getIntent();

                Medication medication = intent.getParcelableExtra(MED_ITEM);

                medNameView.setText(medication.getMedName());
                medDosageView.setText(medication.getDosage());


            }
        });
    }
}
