package com.example.android.healthwatch;

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
    byte[] bytes;
    GoogleApiClient googleApiClient;
    String TAG = "Mobile SendThread";

    //constructor
    SendThread(String p, byte[] b, GoogleApiClient g) {
        path = p;
        bytes = b;
        googleApiClient = g;
    }

    //sends the message via the thread.  this will send to all wearables connected, but
    //since there is (should only?) be one, no problem.
    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, bytes).await();
            if (result.getStatus().isSuccess()) {
//                    sendmessage("SendThread: message send to " + node.getDisplayName());
                Log.v(TAG, "SendThread: message send to " + node.getDisplayName());

            } else {
                // Log an error
//                    sendmessage("SendThread: message failed to" + node.getDisplayName());
                Log.v(TAG, "SendThread: message failed to" + node.getDisplayName());
            }
        }
    }
}
