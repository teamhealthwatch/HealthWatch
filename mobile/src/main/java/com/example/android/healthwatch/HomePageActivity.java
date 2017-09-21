package com.example.android.healthwatch;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity{
    private TextView textView;
    private TextView heart_rate;
    Button btnSignOut;
//    Button btnSignOut;

    //Declare authentication
    private FirebaseAuth mAuth;

    private final String HEART_RATE = "/heart_rate";
    private GoogleApiClient googleApiClient;
    private NodeApi.NodeListener nodeListener;
    private String remoteNodeId;
    private MessageApi.MessageListener messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        textView = (TextView) findViewById(R.id.textViewUsername);
        heart_rate = (TextView) findViewById(R.id.heart_rate);

//        btnSignOut = (Button) findViewById(R.id.btn_signout);
//        btnSignOut.setOnClickListener(this);

        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();

        textView.setText("Welcome User " + intent.getStringExtra(LoginActivity.KEY_LOGIN));

        // Create NodeListener that enables buttons when a node is connected and disables buttons when a node is disconnected
        nodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(Node node) {
                remoteNodeId = node.getId();
            }

            @Override
            public void onPeerDisconnected(Node node) {

            }
        };

        // Create MessageListener that receives messages sent from a wearable
        messageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                if (messageEvent.getPath().equals(HEART_RATE)) {
                    String value = messageEvent.getData().toString();
                    heart_rate.setText(value);
                    //heart_rate.setText(messageEvent.getData().length.);
                }
            }
        };


        // Create GoogleApiClient
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                // Register Node and Message listeners
                Wearable.NodeApi.addListener(googleApiClient, nodeListener);
                Wearable.MessageApi.addListener(googleApiClient, messageListener);
                // If there is a connected node, get it's id that is used when sending messages
                Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                            remoteNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                        }
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE){}
                    //Toast.makeText(getApplicationContext(), getString(R.string.wearable_api_unavailable), Toast.LENGTH_LONG).show();
            }
        }).addApi(Wearable.API).build();
        textView.setText("Welcome " + intent.getStringExtra(LoginActivity.KEY_LOGIN));

        ProgressBar pb = (ProgressBar)findViewById(R.id.circulaprogbar);
        pb.setProgress(30);
        ((TextView)findViewById(R.id.heartRate)).setText("80BMP");
    }

//    @Override
//    public void onClick(View v){
//        if(v == btnSignOut)
//        {
//            mAuth.signOut();
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.med_tracker:
                Toast.makeText(this, "Medication Tracker", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MedTrackerActivity.class);
                startActivity(intent);
                return true;
            case R.id.contact:
                Toast.makeText(this, "Emmergency Contact", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, EmContactActivity.class);
                startActivity(intent2);
                return true;
            case R.id.info:
                Toast.makeText(this, "Personal Info", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, MedConditionActivity.class);
                startActivity(intent3);
                return true;
            case R.id.history:
                Toast.makeText(this, "Medication History", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, MainActivity.class);
                startActivity(intent4);
                return true;
            case R.id.signout:
                Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check is Google Play Services available
        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (connectionResult != ConnectionResult.SUCCESS) {
            // Google Play Services is NOT available. Show appropriate error dialog
            GooglePlayServicesUtil.showErrorDialogFragment(connectionResult, this, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        } else {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        // Unregister Node and Message listeners, disconnect GoogleApiClient and disable buttons
        Wearable.NodeApi.removeListener(googleApiClient, nodeListener);
        Wearable.MessageApi.removeListener(googleApiClient, messageListener);
        googleApiClient.disconnect();
        super.onPause();
    }


}

