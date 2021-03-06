package com.example.android.healthwatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.healthwatch.Activity.AskUserActivity;
import com.example.android.healthwatch.Activity.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

import static com.example.android.healthwatch.TimerIntentService.ACTION_RESPONSE;

public class HeartRateService extends Service implements SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private int currentHeartRate;

    private SensorManager sensorManager;

    private Sensor sensor;


    private String TAG = "HeartRateService";

    public static String id = "test_channel_01";
    public static String id2 = "test_channel_02";

    int notificationID = 1;

    private final String HEART_RATE = "/heart_rate";

    private String remoteNodeId;

    private GoogleApiClient googleApiClient;
    private NodeApi.NodeListener nodeListener;

    private int counter;
    private int averageHeartRate;
    private ArrayList<Integer> heartRates;


    public HeartRateService() {


    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v(TAG, "onCreate");

        currentHeartRate = 0;
        averageHeartRate = 0;
        counter = 0;
        heartRates = new ArrayList<>();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "onStartCommand");

        nodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                remoteNodeId = node.getId();
                Log.i("Node", "Node id connected to is " + remoteNodeId);

            }

            @Override
            public void onPeerDisconnected(Node node) {
                Log.i("Node", "Node disconnected" + currentHeartRate);

            }

        };

        if (googleApiClient == null) {

            Log.v(TAG, "google api client is null");

            googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    // Register Node and Message listeners
                    Wearable.NodeApi.addListener(googleApiClient, nodeListener);
                    // If there is a connected node, get it's id that is used when sending messages
                    Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                        @Override
                        public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                            if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                                remoteNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                                Log.v(TAG, "google api client is connected");
                            }
                        }
                    });
                }

                @Override
                public void onConnectionSuspended(int i) {
                }
            }).addApi(Wearable.API).build();
        }

        googleApiClient.connect();

        measureHeartRate();


        showForegroundNoti();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.v(TAG, "onTaskRemoved!!!!!!!");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentHeartRate = (int) (sensorEvent.values.length > 0 ? sensorEvent.values[0] : 0.0f);

        if(counter != 10){
            heartRates.add(currentHeartRate);
            averageHeartRate = (averageHeartRate + currentHeartRate)/2;
            counter++;
        }
        else{
            updateAverageHeartRate(currentHeartRate);
        }


        //Log.i("sensorChanged", "sensor changed " + currentHeartRate + " " + sensorEvent.sensor.getType());

        //Toast.makeText(getApplicationContext(), "heart rate is " + currentHeartRate, Toast.LENGTH_SHORT).show();

        //if (currentHeartRate > 60){

           // showAskingNoti();
       // }

        // Broadcast message to wearable activity for display
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("heartrate", currentHeartRate);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        Log.v(TAG, "heart rate sent to MainActivity");

        sentToMobile();

    }

    private void updateAverageHeartRate(int curr){
        heartRates.remove(0);
        heartRates.add(curr);
        int ave = 0;
        for(int i : heartRates){
            ave += i;
        }
        int newAve = ave / 10;
        averageHeartRate = ave / 10;
        if(newAve <= averageHeartRate*.4 || newAve >= averageHeartRate*1.3){
            showAskingNoti();
        }
        else{
            averageHeartRate = (averageHeartRate + newAve) / 2;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.v(TAG, "onAccuracyChanged");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v(TAG, "onDestroy!!!!!!!");

        sensorManager.unregisterListener(this);
        stopSelf();
    }

    private void measureHeartRate() {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        if (sensorManager != null) {
            if (sensor != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                Log.v(TAG, "sensor manager is registered");
            } else {
                Log.w("tag", "No heart rate found");
            }
        }

    }

    private void createchannel(String channel) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(channel,
                    "HealthWatch",  //name of the channel
                    NotificationManager.IMPORTANCE_HIGH);   //importance level
            //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
            // Configure the notification channel.
            mChannel.enableLights(true);
            //Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);

        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "google api is connneted");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showAskingNoti(){
//        createchannel("id2");

        // pause heart rate
        sensorManager.unregisterListener(this);

        // Create yes action
        Intent notiIntent = new Intent(getApplicationContext(), AskUserActivity.class);


        notiIntent.putExtra("notiID", notificationID);

        notiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action.WearableExtender wearAction =
                new NotificationCompat.Action.WearableExtender()
                        .setHintDisplayActionInline(true)
                        .setHintLaunchesActivity(true);

        NotificationCompat.Action pictureAction =
                new NotificationCompat.Action.Builder(
                        R.drawable.reply_icon,
                        "HealthWatch",
                        viewPendingIntent)
                        .extend(wearAction)
                        .build();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, id2)
                        .setSmallIcon(R.drawable.newhrt)
                        .setContentTitle("HealthWatch")
                        .setContentText("Are you OK?")
                        .setContentIntent(viewPendingIntent)
                        .setChannelId(id)
                        .addAction(pictureAction)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setOnlyAlertOnce(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationID, notificationBuilder.build());
        notificationID++;

        Intent timerIntent = new Intent(this, TimerIntentService.class);
        timerIntent.setAction(ACTION_RESPONSE);
        startService(timerIntent);

    }

    private void showForegroundNoti(){

        // start foreground with notification

        Intent newIntent = new Intent(this, MainActivity.class);

//        newIntent.putExtra("NotiID", "Notification ID is " + notificationID);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);

        // we are going to add an intent to open MainActivity
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent mainPendingIntent =
                PendingIntent.getActivity(this, 0, mainIntent, 0);


        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.common_google_signin_btn_icon_dark_normal_background,
                        "Open HeathWatch", mainPendingIntent)
                        .build();

        //Now create the notification.  We must use the NotificationCompat or it will not work on the wearable.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, id)
                        .setSmallIcon(R.drawable.newhrt)
                        .setContentTitle("HealthWatch reads heart rate in background")
                        .setContentText("Tab to change this")
                        .setContentIntent(pendingIntent)
                        .setChannelId(id)
                        .extend(new NotificationCompat.WearableExtender().addAction(action));

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        Notification newNoti = notificationBuilder.build();

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationID, newNoti);

        startForeground(notificationID, newNoti);

        notificationID++;
    }


    private void sentToMobile(){
        // Send heart rate to phone
        String strPayload = Integer.toString(currentHeartRate);
        byte[] payload = strPayload.getBytes();


        Wearable.MessageApi.sendMessage(googleApiClient, remoteNodeId, HEART_RATE, payload).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                if (sendMessageResult.getStatus().isSuccess()) {
                    Log.i("heart rate", "heart rate sent to phone " + currentHeartRate);
                } else {
                    Log.i("heart rate", "problem sending heart rate");
                }

            }
        });
    }
}
