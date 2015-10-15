package me.bsu.seismic;

import android.util.Log;

import com.activeandroid.query.Select;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import me.bsu.seismic.dbmodels.Earthquake;
import me.bsu.seismic.models.usgs.Earthquakes;
import me.bsu.seismic.models.usgs.Feature;


public class Utils {

    public static final String TAG = "Utils";
    public static final String INSTAGRAM_CLIENT_ID = "71171bb994f743ab90e92539f22f2530";

    public static int NUMBER_EARTHQUAKES_TO_KEEP = 20;
    public static double MIN_MAGNITUDE = 0.5;

    public static String convertUnixTimestampToLocalTimestampString(long unixTS) {
        Date date = new Date(unixTS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(getUserTimezone());
        return sdf.format(date);
    }

    public static String getTimeDifferenceFromCurrentTime(long unixTS) {
        Duration duration = new Duration(System.currentTimeMillis() - unixTS);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" days ")
                .appendHours()
                .appendSuffix(" hours ")
                .appendMinutes()
                .appendSuffix(" min ago")
                .toFormatter();
        return formatter.print(duration.toPeriod());
    }

    public static TimeZone getUserTimezone() {
        return SimpleTimeZone.getDefault();
    }

    public static int saveToDB(Earthquakes earthquakes) {
        int beforeCount = new Select().from(Earthquake.class).count();
        for (Feature e : earthquakes.getFeatures()) {
            Earthquake earthquake = new Earthquake();
            earthquake.eventID = e.getId();
            earthquake.place = e.getProperties().getPlace();
            earthquake.time = e.getProperties().getTime();
            earthquake.url = e.getProperties().getUrl();
            earthquake.type = e.getProperties().getType();
            earthquake.magnitude = e.getProperties().getMag();
            earthquake.lat = e.getGeometry().getCoordinates().get(1);
            earthquake.lng = e.getGeometry().getCoordinates().get(0);
            earthquake.save();
        }
        int afterCount = new Select().from(Earthquake.class).count();
        return afterCount - beforeCount;
    }

    public static ArrayList<Earthquake> deleteOldFromDB(int numberOfEarthquakesToKeep) {
        List<Earthquake> earthquakes = new Select()
                .from(Earthquake.class)
                .orderBy("time DESC")
                .execute();
        ArrayList<Earthquake> deleted = new ArrayList<>();
        int order = 0;
        for (Earthquake e : earthquakes) {
            if (order >= numberOfEarthquakesToKeep) {
                Log.d(TAG, "Deleting earthquake at " + e.time);
                deleted.add(e);
                e.delete();
            } else {
                Log.d(TAG, "Keeping earthquake at " + e.time);
            }
            order++;
        }
        return deleted;
    }

    public static Earthquake getNthEarthquake(int n) {
        Earthquake e = (Earthquake) new Select()
                                        .from(Earthquake.class)
                                        .orderBy("time DESC")
                                        .offset(n)
                                        .limit(1)
                                        .execute()
                                        .get(0);
        return e;
    }

    public static int getEarthquakeColor(double mag) {

        if (mag < 2) {
            Log.d(TAG, "2 magnitude is: " + mag);
            return R.color.minorColor;
        } else if (mag < 3) {
            Log.d(TAG, "3 magnitude is: " + mag);
            return R.color.lightColor;
        } else if (mag < 4) {
            Log.d(TAG, "4 magnitude is: " + mag);
            return R.color.moderateColor;
        } else if (mag < 5) {
            Log.d(TAG, "5 magnitude is: " + mag);
            return R.color.strongColor;
        } else if (mag < 6) {
            return R.color.majorColor;
        } else {
            return R.color.greatColor;
        }
    }
}
