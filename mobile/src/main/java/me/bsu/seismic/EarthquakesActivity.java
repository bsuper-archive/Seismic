package me.bsu.seismic;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.ResponseBody;

import me.bsu.seismic.api.USGSClient;
import me.bsu.seismic.dbmodels.Earthquake;
import me.bsu.seismic.models.usgs.Earthquakes;
import me.bsu.seismic.models.usgs.Feature;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EarthquakesActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    AlarmReceiver alarm = new AlarmReceiver();

    public static final String TAG = "EARTHQUAKES_ACTIVITY";
    public static final String EVENT_ID = "me.bsu.seismic.EarthquakesActivity.EVENT_ID";
    public static final String EVENT_TITLE = "me.bsu.seismic.EarthquakesActivity.EVENT_TITLE";
    public static final String EVENT_TYPE = "me.bsu.seismic.EarthquakesActivity.EVENT_TYPE";
    public static final String EVENT_MAG = "me.bsu.seismic.EarthquakesActivity.EVENT_MAG";
    public static final String EVENT_TIME = "me.bsu.seismic.EarthquakesActivity.EVENT_TIME";
    public static final String EVENT_MORE_INFO = "me.bsu.seismic.EarthquakesActivity.EVENT_MORE_INFO";
    public static final String EVENT_LAT = "me.bsu.seismic.EarthquakesActivity.EVENT_LAT";
    public static final String EVENT_LNG = "me.bsu.seismic.EarthquakesActivity.EVENT_LNG";

    public static final String EVENT_INDEX = "me.bsu.seismic.EarthquakesActivity.EVENT_INDEX";


    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Earthquakes mEarthquakes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquakes);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.list_container, new EarthquakesListFragment())
                    .commit();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        } else {
            // Reincarnated activity. The obtained map is the same map instance in the previous
            // activity life cycle. There is no need to reinitialize it.
            mMap = mapFragment.getMap();
        }
        buildGoogleApiClient();
        getRecentEarthquakes();
        alarm.setAlarm(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "build google api client called");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void getRecentEarthquakes() {
        Log.d(TAG, "get recent earthquakes called");
        Call<Earthquakes> call = USGSClient.getUSGSApiClient().getRecentEarthquakes(Utils.MIN_MAGNITUDE, Utils.NUMBER_EARTHQUAKES_TO_KEEP, "geojson");
        call.enqueue(new Callback<Earthquakes>() {
            @Override
            public void onResponse(Response<Earthquakes> response, Retrofit retrofit) {
                Log.d(TAG, response.raw().request().urlString());
                if (response.isSuccess()) {
                    Earthquakes e = response.body();
                    consumeUSGSApiData(e);
                } else {
                    int statusCode = response.code();

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

    private void consumeUSGSApiData(Earthquakes earthquakes) {
        mEarthquakes = earthquakes;
        Utils.saveToDB(earthquakes);
        Utils.deleteOldFromDB(Utils.NUMBER_EARTHQUAKES_TO_KEEP);
        Log.d(TAG, "# Earthquakes in DB: " + new Select().from(Earthquake.class).count());
        EarthquakesListFragment listFragment = (EarthquakesListFragment) getFragmentManager().findFragmentById(R.id.list_container);
        if (listFragment != null) {
            Log.d(TAG, "Not null");
            listFragment.updateEarthquakes();
        }
//        } else {
//            EarthquakesListFragment newFragment = new EarthquakesListFragment();
//
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//            // Replace whatever is in the fragment_container view with this fragment,
//            // and add the transaction to the back stack so the user can navigate back
//            transaction.replace(R.id.list_container, newFragment);
//            transaction.addToBackStack(null);
//
//            // Commit the transaction
//            transaction.commitAllowingStateLoss();
//            newFragment.updateEarthquakes();
//        }
        for (Feature f : earthquakes.getFeatures()) {
            LatLng location = new LatLng(f.getGeometry().getCoordinates().get(1), f.getGeometry().getCoordinates().get(0));
            mMap.addMarker(new MarkerOptions().position(location).title(f.getProperties().getTitle()));
        }
    }

    private void showMapOnly() {
        View v = findViewById(R.id.map_container);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        v.setLayoutParams(param);
    }

    private void showListOnly() {
        View v = findViewById(R.id.list_container);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        v.setLayoutParams(param);
    }

    private void showBoth() {
        View listContainer = findViewById(R.id.list_container);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 0.5f);
        listContainer.setLayoutParams(param);
        View mapContainer = findViewById(R.id.map_container);
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 0.5f);
        mapContainer.setLayoutParams(param2);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "on connected called");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null && mMap != null) {
            Log.d(TAG, "got last location");
            LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, connectionResult.getErrorMessage());
    }

    public void getEarthquakeProfile(int item) {
        Feature event = mEarthquakes.getFeatures().get(item);
        Intent intent = new Intent(this, EarthquakeProfileActivity.class);
        intent.putExtra(EVENT_ID, event.getId());
        intent.putExtra(EVENT_TITLE, event.getProperties().getTitle());
        intent.putExtra(EVENT_TYPE, event.getProperties().getType());
        intent.putExtra(EVENT_MAG, event.getProperties().getMag());
        intent.putExtra(EVENT_TIME, Utils.convertUnixTimestampToLocalTimestampString(event.getProperties().getTime()));
        intent.putExtra(EVENT_MORE_INFO, event.getProperties().getUrl());
        intent.putExtra(EVENT_LAT, event.getGeometry().getCoordinates().get(1));
        intent.putExtra(EVENT_LNG, event.getGeometry().getCoordinates().get(0));

        Log.d(TAG, "Lat: " + event.getGeometry().getCoordinates().get(1));
        Log.d(TAG, "Lng: " + event.getGeometry().getCoordinates().get(0));
        startActivity(intent);
    }

    public void getEarthquakeProfileUsingDB(int index) {
        Log.d(TAG, "get event profile using db called with index: " + index);
        Intent intent = new Intent(this, EarthquakeProfileActivity.class);
        intent.putExtra(EVENT_INDEX, index);
        startActivity(intent);
    }

    public void moveMapToEarthquake(int index) {
        Earthquake e = Utils.getNthEarthquake(index);
        LatLng latLng = new LatLng(e.lat, e.lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 6);
        mMap.animateCamera(cameraUpdate);
    }
}
