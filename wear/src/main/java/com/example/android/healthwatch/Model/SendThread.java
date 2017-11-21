package com.example.android.healthwatch.Model;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Yan Tan on 11/20/2017.
 */

public class SendThread extends Thread {
    String path;
    String message;
    GoogleApiClient googleApiClient;

    String TAG = "Wear SendThread";


    public SendThread(String p, String msg, GoogleApiClient g) {
        path = p;
        message = msg;
        googleApiClient = g;
    }


    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, message.getBytes()).await();
            if (result.getStatus().isSuccess()) {
                Log.v(TAG, "SendThread: message send to " + node.getDisplayName());

            } else {
                Log.v(TAG, "SendThread: message failed to" + node.getDisplayName());
            }
        }
    }
}