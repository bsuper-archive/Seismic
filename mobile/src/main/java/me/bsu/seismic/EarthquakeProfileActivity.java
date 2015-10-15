package me.bsu.seismic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import me.bsu.seismic.dbmodels.Earthquake;

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
