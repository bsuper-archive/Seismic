package me.bsu.seismic;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.bsu.seismic.models.usgs.Earthquakes;

/**
 * A placeholder fragment containing a simple view.
 */
public class EarthquakesListFragment extends Fragment {

    public static final String TAG = "LIST_FRAGMENT";


    RecyclerView mRecyclerView;
    private EarthquakesAdapter mEarthquakesAdapter;
    private Earthquakes mEarthquakes;


    public EarthquakesListFragment() {
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
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity()
        ));
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(TAG, String.format("%d clicked", position));
                        ((EarthquakesActivity) getActivity()).getEarthquakeProfile(position);
                    }
                })
        );
        return v;
    }

    public void updateEarthquakes(Earthquakes earthquakes) {
        Log.d(TAG, "update earthquakes called");
        mEarthquakes = earthquakes;
        mEarthquakesAdapter = new EarthquakesAdapter(mEarthquakes);
        mRecyclerView.setAdapter(mEarthquakesAdapter);
    }
}
