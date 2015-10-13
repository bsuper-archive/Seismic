package me.bsu.seismic;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.bsu.seismic.models.Earthquakes;

/**
 * A placeholder fragment containing a simple view.
 */
public class EarthquakesListFragment extends Fragment {

    RecyclerView mRecyclerView;
    private EarthquakesAdapter mEarthquakesAdapter;


    public EarthquakesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_earthquakes_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.listview_earthquakes);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity()
        ));
        mEarthquakesAdapter = new EarthquakesAdapter(new Earthquakes());
        mRecyclerView.setAdapter(mEarthquakesAdapter);

        return v;
    }

    public void updateEarthquakes(Earthquakes earthquakes) {
        mEarthquakesAdapter = new EarthquakesAdapter(earthquakes);
        mRecyclerView.setAdapter(mEarthquakesAdapter);
    }
}
