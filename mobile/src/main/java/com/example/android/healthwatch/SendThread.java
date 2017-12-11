package com.example.android.healthwatch;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Yan Tan on 11/20/2017.
 */

public class SendThread extends Thread {
    String path;
    byte[] message;
    GoogleApiClient googleApiClient;

    String TAG = "Mobile SendThread";


    public SendThread(String p, byte[] msg, GoogleApiClient g) {
        path = p;
        message = msg;
        googleApiClient = g;
    }


    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        for (Node node : nodes.getNodes()) {
            Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, message).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                    if (sendMessageResult.getStatus().isSuccess()) {
                        Log.i(TAG, "Successful PATH is " + path);
                    } else {
                        Log.i(TAG, "Unsuccessful PATH is " + path);
                    }

                }
            });
        }
    }
}

