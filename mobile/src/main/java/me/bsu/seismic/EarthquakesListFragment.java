package me.bsu.seismic;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import me.bsu.seismic.dbmodels.Earthquake;
import me.bsu.seismic.models.usgs.Earthquakes;
import me.bsu.seismic.other.RecyclerItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class EarthquakesListFragment extends Fragment {

    public static final String TAG = "LIST_FRAGMENT";
    private double latitude, longitude;

    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mEarthquakesAdapter;
    private Earthquakes mEarthquakes;

    private boolean hasLocation = false;


    public EarthquakesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "elf create view called");
        View v = inflater.inflate(R.layout.fragment_earthquakes_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.listview_earthquakes);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, String.format("%d clicked", position));
                ((EarthquakesActivity) getActivity()).getEarthquakeProfileUsingDB(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, String.format("%d long press", position));
                ((EarthquakesActivity) getActivity()).moveMapToEarthquake(position);
            }
        }));
        updateEarthquakes();
        return v;
    }

    public void updateEarthquakes() {
        Log.d(TAG, "update earthquakes 1 called");
        mEarthquakesAdapter = new EarthquakesCursorRecyclerAdapter(Earthquake.fetchResultCursor(), getActivity(), hasLocation, latitude, longitude);
        mRecyclerView.swapAdapter(mEarthquakesAdapter, true);
    }


    public void updateEarthquakesWithUserLocation(Location userLocation) {
        Log.d(TAG, "update earthquakes with user location called");
        hasLocation = true;
        if (userLocation != null) {
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
        }
        mEarthquakesAdapter = new EarthquakesCursorRecyclerAdapter(Earthquake.fetchResultCursor(), getActivity(), hasLocation, latitude, longitude);
        mRecyclerView.swapAdapter(mEarthquakesAdapter, true);
    }
}
