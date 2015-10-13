package me.bsu.seismic;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.squareup.okhttp.ResponseBody;

import org.apache.commons.lang3.text.WordUtils;

import me.bsu.seismic.api.InstagramClient;
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
        loadPhotos();
    }

    public void parseIntent(Intent intent) {
        id = intent.getStringExtra(EarthquakesActivity.EVENT_ID);
        name = intent.getStringExtra(EarthquakesActivity.EVENT_TITLE);
        type = intent.getStringExtra(EarthquakesActivity.EVENT_TYPE);
        mag = intent.getDoubleExtra(EarthquakesActivity.EVENT_MAG, 0);
        time = intent.getStringExtra(EarthquakesActivity.EVENT_TIME);
        moreInfo = intent.getStringExtra(EarthquakesActivity.EVENT_MORE_INFO);
        lat = intent.getFloatExtra(EarthquakesActivity.EVENT_LAT, 0);
        lng = intent.getFloatExtra(EarthquakesActivity.EVENT_LNG, 0);
        Log.d(TAG, "Lat: " + lat);
        Log.d(TAG, "Lng: " + lng);
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
        timeTV.setText(time);

    }

    public void loadPhotos() {
        Call<InstagramResponse> call = InstagramClient.getInstagramApiClient().getMedia(lat, lng, 5000, Utils.INSTAGRAM_CLIENT_ID);
        call.enqueue(new Callback<InstagramResponse>() {
            @Override
            public void onResponse(Response<InstagramResponse> response, Retrofit retrofit) {
                Log.d(TAG, response.raw().request().urlString());
//                consumeUSGSApiData(response.body());
                if (response.isSuccess()) {
                    InstagramResponse e = response.body();
                    Log.d(TAG, "success!!");
                } else {
                    int statusCode = response.code();
                    Log.d(TAG, "failure!!");

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "failure " + t.getMessage());
                Log.d(TAG, "stack " + t.fillInStackTrace().toString());
            }
        });
    }

}
