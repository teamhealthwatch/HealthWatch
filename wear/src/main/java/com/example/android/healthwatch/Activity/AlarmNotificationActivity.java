package com.example.android.healthwatch.Activity;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import com.example.android.healthwatch.R;

public class AlarmNotificationActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);

        mTextView = (TextView) findViewById(R.id.text);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MedInfoActivity.notificationId);
    }
}
