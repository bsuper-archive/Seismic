package me.bsu.seismic;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import me.bsu.seismic.api.USGSClient;
import me.bsu.seismic.dbmodels.Earthquake;
import me.bsu.seismic.models.usgs.Earthquakes;
import retrofit.Call;

public class USGSQueryService extends IntentService {

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

    public static final String TAG = "QUERY_SERVICE";
    public static final int NOTIFICATION_ID = 998;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    private GoogleApiClient mGoogleApiClient;

    public USGSQueryService() {
        super("USGSQueryService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getNewEarthquakes();
    }

    private void getNewEarthquakes() {
        Log.d(TAG, "get new earthquakes called from service");
        Call<Earthquakes> call = USGSClient.getUSGSApiClient().getRecentEarthquakesFromFeed();
        try {
            Earthquakes earthquakes = call.execute().body();
            int numNewItems = Utils.saveToDB(earthquakes);
            Utils.deleteOldFromDB(Utils.NUMBER_EARTHQUAKES_TO_KEEP);
            Log.d(TAG, "new earthquakes retrieved from service and saved to DB: " + numNewItems);

            List<Earthquake> newEarthquakes = getNewEarthquakesList(numNewItems);
            for (Earthquake e : newEarthquakes) {
                Utils.downloadImagesForEarthquakeSynch(e);
            }
            if (numNewItems > 0) {
                sendNotification(newEarthquakes);
                sendNotificationToWear(newEarthquakes);
            }

        } catch (IOException e) {
            Log.d(TAG, "IO Error: " + e.getMessage());
        }
    }

    private List<Earthquake> getNewEarthquakesList(int numNewItems) {
        return new Select().from(Earthquake.class).orderBy("time DESC").limit(numNewItems).execute();
    }

    private void sendNotification(List<Earthquake> newEarthquakes) {
        Log.d(TAG, "send notification called");

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, EarthquakesActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(EarthquakesActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        String notificationTitle = newEarthquakes.size() == 1 ? "New earthquake" : String.format("%d new earthquakes", newEarthquakes.size());
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLocalOnly(true)
                        .setSmallIcon(R.drawable.ic_earthquake_notification)
                        .setContentTitle(notificationTitle);

        if (newEarthquakes.size() == 1) {
            mBuilder.setContentText(Utils.getFormat(newEarthquakes.get(0)));
        } else {
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Earthquakes:");
            for (Earthquake e : newEarthquakes) {
                inboxStyle.addLine(Utils.getFormat(e));
            }
            mBuilder.setStyle(inboxStyle);
        }


        mBuilder.setContentIntent(resultPendingIntent);

        // uncomment to only send notifications to the phone and not to the wear device
//        mBuilder.setLocalOnly(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    public static final String NOTIFICATION_PATH = "/notification";
    public static final String NOTIFICATION_TIMESTAMP = "timestamp";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_LAT = "lat";
    public static final String NOTIFICATION_LNG = "lon";
    public static final String NOTIFICATION_CONTENT = "content";
    public static final String IMAGE1 = "image1";


    public static final String ACTION_DISMISS = "me.bsu.seismic.DISMISS";

    private void sendNotificationToWear(List<Earthquake> newEarthquakes) {

        Log.d(TAG, "notification to wear called for " + newEarthquakes.size() + " new earthquakes");
        if (mGoogleApiClient.isConnected()) {
            for (Earthquake e : newEarthquakes) {
                PutDataMapRequest dataMapRequest = PutDataMapRequest.create(NOTIFICATION_PATH);
                // Make sure the data item is unique. Usually, this will not be required, as the payload
                // (in this case the title and the content of the notification) will be different for almost all
                // situations. However, in this example, the text and the content are always the same, so we need
                // to disambiguate the data item by adding a field that contains teh current time in milliseconds.
                dataMapRequest.getDataMap().putDouble(NOTIFICATION_TIMESTAMP, System.currentTimeMillis());
                dataMapRequest.getDataMap().putString(NOTIFICATION_TITLE, "New earthquake");
                dataMapRequest.getDataMap().putFloat(NOTIFICATION_LAT, e.lat);
                dataMapRequest.getDataMap().putFloat(NOTIFICATION_LNG, e.lng);
                dataMapRequest.getDataMap().putString(NOTIFICATION_CONTENT, Utils.getFormat(e));
                if (e.imageUrls() == null || e.imageUrls().size() == 0) {
                    Log.d(TAG, "no images");
                    PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
                } else {
                    Log.d(TAG, "going to get an image");
                    getImageAndSendToWear(e, dataMapRequest);
                }
            }
        }
        else {
            Log.e(TAG, "No connection to wearable available!");
        }
    }

    private void getImageAndSendToWear(Earthquake e, final PutDataMapRequest dataMapRequest) {
        Log.d(TAG, "getimagesendwear called");
        Bitmap bitmap = getBitmapFromURL(e.imageUrls().get(0).url);
        Asset asset = createAssetFromBitmap(bitmap);
        dataMapRequest.getDataMap().putAsset(IMAGE1, asset);
        PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        Log.d(TAG, "getimagesendwear success");
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

}
