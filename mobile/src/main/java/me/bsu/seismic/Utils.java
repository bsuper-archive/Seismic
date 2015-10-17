package me.bsu.seismic;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.util.Log;

import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.ResponseBody;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import me.bsu.seismic.api.InstagramClient;
import me.bsu.seismic.dbmodels.Earthquake;
import me.bsu.seismic.dbmodels.ImageUrl;
import me.bsu.seismic.models.instagram.Datum;
import me.bsu.seismic.models.instagram.InstagramResponse;
import me.bsu.seismic.models.usgs.Earthquakes;
import me.bsu.seismic.models.usgs.Feature;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class Utils {

    public static final String TAG = "Utils";
    public static final String INSTAGRAM_CLIENT_ID = "71171bb994f743ab90e92539f22f2530";

    public static int NUMBER_EARTHQUAKES_TO_KEEP = 50;
    public static double MIN_MAGNITUDE = 0;

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
            if (new Select().from(Earthquake.class).where("eventid = ?", e.getId()).count() == 0) {
                Log.d(TAG, "earthquake " + e.getProperties().getPlace() + " does not exist so add to db");
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
            } else {
                Log.d(TAG, "earthquake " + e.getProperties().getPlace() + " exists");
            }
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
                for (ImageUrl i : e.imageUrls()) {
                    i.delete();
                }
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
            return R.color.minorColor;
        } else if (mag < 3) {
            return R.color.lightColor;
        } else if (mag < 4) {
            return R.color.moderateColor;
        } else if (mag < 5) {
            return R.color.strongColor;
        } else if (mag < 6) {
            return R.color.majorColor;
        } else {
            return R.color.greatColor;
        }
    }

    public static boolean downloadImagesForEarthquakeSynch(Earthquake earthquake) {
        Log.d(TAG, "download images called");
        Call<InstagramResponse> call = InstagramClient.getInstagramApiClient().getMedia(earthquake.lat, earthquake.lng, 5000, Utils.INSTAGRAM_CLIENT_ID);
        try {
            InstagramResponse e = call.execute().body();
            saveUrlsToImageUrlForEarthquake(earthquake, getUrls(e));
            Log.d(TAG, "download image success!!");
        } catch (IOException e) {
            Log.d(TAG, "download error: " + e.getMessage());
        }
        return true;
    }

    public static ArrayList<String> getUrls(InstagramResponse r) {
        ArrayList<String> urls = new ArrayList<>();
        List<Datum> data = r.getData();
        Log.d(TAG, "# Images: " + data.size());
        for (Datum datum : data) {
            Log.d(TAG, datum.getCreatedTime());
            if (datum.getImages() != null && datum.getImages().getStandardResolution() != null) {
                String url = datum.getImages().getStandardResolution().getUrl();
                Log.d(TAG, "Image url: " + url);
                urls.add(url);
            }
        }
        return urls;
    }

    public static boolean saveUrlsToImageUrlForEarthquake(Earthquake earthquake, ArrayList<String> urls) {
        for (String url : urls) {
            ImageUrl i = new ImageUrl();
            i.url = url;
            i.earthquake = earthquake;
            i.save();
        }
        Log.d(TAG, "saved " + urls.size() + " urls to " + earthquake.place);
        return true;
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public static String getFormat(Earthquake e) {
        return String.format("M%.2f %s", e.magnitude, e.place);
    }

    public static float distanceBetween(float lat1, float lon1, float lat2, float lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1,
                lat2, lon2, results);
        Log.d(TAG, "distance is " + results[0]);
        return results[0];
    }
}
