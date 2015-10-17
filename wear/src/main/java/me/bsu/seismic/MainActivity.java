package me.bsu.seismic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements SensorEventListener {

    public static final String TAG = "MainActivity";

    private TextView mTitleView;
    private ImageView mMapButton, mImageButton;
    private float lat, lng;
    private String title;
    private Bitmap bitmap;

    private boolean alreadyLaunchedOnPhone = false;

    private Parcelable imageAsset;

    public static final String TITLE = "mainactivity.TITLE";
    public static final String LAT = "mainactivity.LAT";
    public static final String LNG = "mainactivity.LNG";
    public static final String IMAGE = "mainactivity.IMAGE";

    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "on create called");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        resolveNode();
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    /*
     * Resolve the node = the connected device to send the message to
     */
    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "new intent: " + intent);
        setIntent(intent);
    }

    private void handleNewIntent(Intent i) {
        Log.d(TAG, NotificationReceiverService.intentToString(i));
        lat = i.getFloatExtra(NotificationReceiverService.LAT, 0);
        lng = i.getFloatExtra(NotificationReceiverService.LNG, 0);
        title = i.getStringExtra(NotificationReceiverService.TITLE);
        long ts = i.getLongExtra(NotificationReceiverService.TS, -5);
        Log.d(TAG, "main activity got " + title + " lat: " + lat + " lon: " + lng + " ts: " + ts + " " + NotificationReceiverService.TITLE);
        imageAsset = i.getParcelableExtra(NotificationReceiverService.IMAGE);

        mTitleView = (TextView) findViewById(R.id.title);
        if (title != null) {
            mTitleView.setText(title);
        }
        Log.d(TAG, "intent " + i.toString());

        mMapButton = (ImageView) findViewById(R.id.map_button);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                i.putExtra(LAT, lat);
                i.putExtra(LNG, lng);
                i.putExtra(TITLE, title);
                startActivity(i);
            }
        });
        mImageButton = (ImageView) findViewById(R.id.photo_button);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageAsset == null) {
                    Log.d(TAG, "image asset null: " + imageAsset);
                    Toast.makeText(MainActivity.this, "No images available", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(MainActivity.this, ImageActivity.class);
                    i.putExtra(IMAGE, imageAsset);
                    startActivity(i);
                }
            }
        });
    }

    private static final float SHAKE_THRESHOLD = 1.1f;
    private static final int SHAKE_WAIT_TIME_MS = 250;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mSensorType;
    private long mShakeTime = 0;

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "on resume called");
        handleNewIntent(getIntent());
        alreadyLaunchedOnPhone = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.d(TAG, "sensor changed | accuracy " + event.accuracy + " | type: " + event.sensor.getType());
        // If sensor is unreliable, then just return
//        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
//            return;
//        }

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            Log.d(TAG, "accelerometer");
            if (detectShake(event)) {
                if (!alreadyLaunchedOnPhone) {
                    sendMessage();
                    alreadyLaunchedOnPhone = true;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    // References:
    //  - http://jasonmcreynolds.com/?p=388
    //  - http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
    private boolean detectShake(SensorEvent event) {
        long now = System.currentTimeMillis();

        if((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

//            Log.d(TAG, "sensors: " + gX + " " + gY + " " + gZ);

            // gForce will be close to 1 when there is no movement
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            // Change background color if gForce exceeds threshold;
            // otherwise, reset the color
            if(gForce > SHAKE_THRESHOLD) {
                Log.d(TAG, "shake!");
                return true;
            }
            else {
//                Log.d(TAG, "no shake!");
            }
        }
        return false;
    }

    private void sendMessage() {
        Toast.makeText(this, "shake!!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "send message called");
        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), HELLO_WORLD_WEAR_PATH, null).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }else{
            //Improve your code
        }

    }
}
