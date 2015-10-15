package me.bsu.seismic;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.squareup.okhttp.ResponseBody;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import me.bsu.seismic.api.InstagramClient;
import me.bsu.seismic.dbmodels.Earthquake;
import me.bsu.seismic.models.instagram.Datum;
import me.bsu.seismic.models.instagram.InstagramResponse;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EarthquakeProfileActivity extends Activity {

    public static final String TAG = "PROFILE";
    private String id, name, type, time, moreInfo;
    private float lat, lng;
    private double mag;

    private TextView nameTV, typeTV, magTV, timeTV, moreInfoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_profile);

        parseIntent(getIntent());
        loadViews();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.image_fragment_container, EarthquakeProfileImageFragment.newInstance(lat, lng))
                    .commit();
        }
    }

    public void parseIntent(Intent intent) {
//        id = intent.getStringExtra(EarthquakesActivity.EVENT_ID);
//        name = intent.getStringExtra(EarthquakesActivity.EVENT_TITLE);
//        type = intent.getStringExtra(EarthquakesActivity.EVENT_TYPE);
//        mag = intent.getDoubleExtra(EarthquakesActivity.EVENT_MAG, 0);
//        time = intent.getStringExtra(EarthquakesActivity.EVENT_TIME);
//        moreInfo = intent.getStringExtra(EarthquakesActivity.EVENT_MORE_INFO);
//        lat = intent.getFloatExtra(EarthquakesActivity.EVENT_LAT, 0);
//        lng = intent.getFloatExtra(EarthquakesActivity.EVENT_LNG, 0);
        int index = intent.getIntExtra(EarthquakesActivity.EVENT_INDEX, 0);

        Earthquake e = Utils.getNthEarthquake(index);
        id = e.eventID;
        name = e.place;
        type = e.type;
        mag = e.magnitude;
        time = Utils.convertUnixTimestampToLocalTimestampString(e.time);
        moreInfo = e.url;
        lat = e.lat;
        lng = e.lng;
    }

    public void loadViews() {
        nameTV = (TextView) findViewById(R.id.profile_name);
        typeTV = (TextView) findViewById(R.id.profile_type);
        magTV = (TextView) findViewById(R.id.profile_magnitude);
        timeTV = (TextView) findViewById(R.id.profile_time);
        moreInfoTV = (TextView) findViewById(R.id.profile_more_info);

        nameTV.setText(WordUtils.capitalizeFully(name));
        typeTV.setText(WordUtils.capitalizeFully(type));
        magTV.setText(String.valueOf(mag));
        magTV.setTextColor(ContextCompat.getColor(this, Utils.getEarthquakeColor(mag)));
        timeTV.setText(time);

    }

}
