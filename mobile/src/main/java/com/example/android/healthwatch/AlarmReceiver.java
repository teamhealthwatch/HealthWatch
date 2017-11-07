package com.example.android.healthwatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.healthwatch.Activities.MedTrackerActivity;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by faitholadele on 10/11/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        MedTrackerActivity inst = MedTrackerActivity.instance();

        String state = intent.getExtras().getString("extra");
        Intent serviceIntent = new Intent(context,AlarmService.class);
        serviceIntent.putExtra("extra", state);

        Log.e("reciver", state);

        context.startService(serviceIntent);

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
