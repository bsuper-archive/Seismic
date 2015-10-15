package me.bsu.seismic;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.activeandroid.query.Select;

import java.io.IOException;
import java.util.List;

import me.bsu.seismic.api.USGSClient;
import me.bsu.seismic.dbmodels.Earthquake;
import me.bsu.seismic.models.usgs.Earthquakes;
import retrofit.Call;

public class USGSQueryService extends IntentService {

    public static final String TAG = "QUERY_SERVICE";
    public static final int NOTIFICATION_ID = 998;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

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
            if (numNewItems > 0) {
                sendNotification(numNewItems);
            }
        } catch (IOException e) {
            Log.d(TAG, "IO Error: " + e.getMessage());
        }
    }

    private void sendNotification(int numNewItems) {
        Log.d(TAG, "send notification called");
        List<Earthquake> newEarthquakes = new Select().from(Earthquake.class).orderBy("time DESC").limit(numNewItems).execute();

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

        String notificationTitle = numNewItems == 1 ? "New earthquake" : String.format("%d new earthquakes", numNewItems);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_earthquake_notification)
                        .setContentTitle(notificationTitle)
                        .setContentText(newEarthquakes.get(0).place);

        if (newEarthquakes.size() == 1) {
            mBuilder.setContentText(newEarthquakes.get(0).place);
        } else {
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Earthquakes:");
            for (Earthquake e : newEarthquakes) {
                inboxStyle.addLine(e.place);
            }
            mBuilder.setStyle(inboxStyle);
        }

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
}
