package me.bsu.seismic;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import me.bsu.seismic.other.DismissNotificationCommand;

public class NotificationReceiverService extends WearableListenerService {

    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = "NotificationReceiver";
    private int notificationId = 001;

    public static final String NOTIFICATION_PATH = "/notification";
    public static final String NOTIFICATION_TIMESTAMP = "timestamp";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_CONTENT = "content";
    public static final String NOTIFICATION_LAT = "lat";
    public static final String NOTIFICATION_LNG = "lon";
    public static final String IMAGE1 = "image1";

    public static final String ACTION_DISMISS = "me.bsu.seismic.DISMISS";

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (ACTION_DISMISS.equals(action)) {
                dismissNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "on data changed called");
        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                if (NOTIFICATION_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    String title = dataMapItem.getDataMap().getString(NOTIFICATION_TITLE);
                    String content = dataMapItem.getDataMap().getString(NOTIFICATION_CONTENT);
                    float lat = dataMapItem.getDataMap().getFloat(NOTIFICATION_LAT);
                    float lng = dataMapItem.getDataMap().getFloat(NOTIFICATION_LNG);
                    Asset imageAsset = dataMapItem.getDataMap().getAsset(IMAGE1);
                    sendNotificationWithActivityStart(title, content, imageAsset, lat, lng);
                }
            }
        }
    }

    public static final String TITLE = "notificationreceiverservice.EARTHQUAKE_TITLE";
    public static final String LAT = "notificationreceiverservice.LAT";
    public static final String LNG = "notificationreceiverservice.LNG";
    public static final String IMAGE = "notificationreceiverservice.IMAGE";
    public static final String TS = "notificationreceiverservice.TS";


    private void sendNotificationWithActivityStart(String title, String content, Asset imageAsset, float lat, float lng) {
        Bitmap bitmap = null;
        if (imageAsset != null) {
            bitmap = loadBitmapFromAsset(imageAsset);
        }

        Log.d(TAG, "calling send notification on android wear");
        // this intent will open the activity when the user taps the "open" action on the notification
        Intent viewIntent = new Intent(this, MainActivity.class);
        viewIntent.putExtra(TITLE, content);
        viewIntent.putExtra(LAT, lat);
        viewIntent.putExtra(LNG, lng);
        long ts = System.currentTimeMillis();
        viewIntent.putExtra(TS, ts);

        if (imageAsset != null) {
            viewIntent.putExtra(IMAGE, imageAsset);
        }
        Log.d(TAG, "intent " + intentToString(viewIntent));
        PendingIntent pendingViewIntent = PendingIntent.getActivity(this, notificationId, viewIntent, 0);

        // this intent will be sent when the user swipes the notification to dismiss it
        Intent dismissIntent = new Intent(ACTION_DISMISS);
        PendingIntent pendingDeleteIntent = PendingIntent.getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setGroup(TAG)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setDeleteIntent(pendingDeleteIntent)
                .setContentIntent(pendingViewIntent);

        if (imageAsset != null) {
            Log.d(TAG, "image asset is not null");
            NotificationCompat.WearableExtender wearableExtender =
                    new NotificationCompat.WearableExtender()
                            .setBackground(bitmap);
            builder.extend(wearableExtender);
        }

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId++, notification);
    }

    private void dismissNotification() {
        new DismissNotificationCommand(this).execute();
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        Log.d(TAG, "load asset called");
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(10000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    public static String intentToString(Intent intent) {
        if (intent == null) {
            return null;
        }

        return intent.toString() + " " + bundleToString(intent.getExtras());
    }

    public static String bundleToString(Bundle bundle) {
        StringBuilder out = new StringBuilder("Bundle[");

        if (bundle == null) {
            out.append("null");
        } else {
            boolean first = true;
            for (String key : bundle.keySet()) {
                if (!first) {
                    out.append(", ");
                }

                out.append(key).append('=');

                Object value = bundle.get(key);

                if (value instanceof int[]) {
                    out.append(Arrays.toString((int[]) value));
                } else if (value instanceof byte[]) {
                    out.append(Arrays.toString((byte[]) value));
                } else if (value instanceof boolean[]) {
                    out.append(Arrays.toString((boolean[]) value));
                } else if (value instanceof short[]) {
                    out.append(Arrays.toString((short[]) value));
                } else if (value instanceof long[]) {
                    out.append(Arrays.toString((long[]) value));
                } else if (value instanceof float[]) {
                    out.append(Arrays.toString((float[]) value));
                } else if (value instanceof double[]) {
                    out.append(Arrays.toString((double[]) value));
                } else if (value instanceof String[]) {
                    out.append(Arrays.toString((String[]) value));
                } else if (value instanceof CharSequence[]) {
                    out.append(Arrays.toString((CharSequence[]) value));
                } else if (value instanceof Parcelable[]) {
                    out.append(Arrays.toString((Parcelable[]) value));
                } else if (value instanceof Bundle) {
                    out.append(bundleToString((Bundle) value));
                } else {
                    out.append(value);
                }

                first = false;
            }
        }

        out.append("]");
        return out.toString();
    }


}
