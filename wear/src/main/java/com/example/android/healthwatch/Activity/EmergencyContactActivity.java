package com.example.android.healthwatch.Activity;

import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;

import com.example.android.healthwatch.Adapter.ContactAdapter;
import com.example.android.healthwatch.R;

public class EmergencyContactActivity extends WearableActivity {

    private WearableRecyclerView wearableRecyclerView;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableRecyclerView = findViewById(R.id.contact_recycler_view);
                wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(EmergencyContactActivity.this));
            }
        });
    }
}
