package com.example.android.healthwatch.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wear.widget.CircularProgressLayout;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.healthwatch.R;


public class AskUserActivity extends Activity implements
        CircularProgressLayout.OnTimerFinishedListener, View.OnClickListener{

    private CircularProgressLayout mCircularProgress;
    private String TAG = "AskUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate!");

        setContentView(R.layout.activity_ask_user);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mCircularProgress =
                        (CircularProgressLayout) findViewById(R.id.circular_progress);
                mCircularProgress.setOnTimerFinishedListener(AskUserActivity.this);
                mCircularProgress.setOnClickListener(AskUserActivity.this);

                // 1 min to click the action
                mCircularProgress.setTotalTime(60000);
                // Start the timer
                mCircularProgress.startTimer();
            }
        });


    }

    /* User did not response */
    @Override
    public void onTimerFinished(CircularProgressLayout layout) {
        Toast.makeText(this, "time finished!", Toast.LENGTH_SHORT).show();
    }


    /* User responses */
    @Override
    public void onClick(View view) {
        mCircularProgress.stopTimer();

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                "something");
        startActivity(intent);
    }
}
