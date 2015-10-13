package me.bsu.seismic;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.ResponseBody;

import me.bsu.seismic.api.USGSClient;
import me.bsu.seismic.models.Earthquakes;
import me.bsu.seismic.models.Feature;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener  {

    public static final String TAG = "MAIN_ACTIVITY";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    RecyclerView mRecyclerView;
    private EarthquakesAdapter mEarthquakesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();


        mRecyclerView = (RecyclerView) findViewById(R.id.listview_earthquakes);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                this
        ));
        mEarthquakesAdapter = new EarthquakesAdapter(new Earthquakes());
        mRecyclerView.setAdapter(mEarthquakesAdapter);


        getRecentEarthquakes();

    }

    private void getRecentEarthquakes() {
        Log.d(TAG, "Method called");
        Call<Earthquakes> call = USGSClient.getUSGSApiClient().getRecentEarthquakes(20, "geojson");
        call.enqueue(new Callback<Earthquakes>() {
            @Override
            public void onResponse(Response<Earthquakes> response, Retrofit retrofit) {
//                consumeUSGSApiData(response.body());
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
        mEarthquakesAdapter = new EarthquakesAdapter(earthquakes);
        mRecyclerView.setAdapter(mEarthquakesAdapter);

        for (Feature f : earthquakes.getFeatures()) {
            LatLng location = new LatLng(f.getGeometry().getCoordinates().get(1), f.getGeometry().getCoordinates().get(0));
            mMap.addMarker(new MarkerOptions().position(location).title(f.getProperties().getTitle()));
        }
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

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "build google api client called");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, connectionResult.getErrorMessage());
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
}
