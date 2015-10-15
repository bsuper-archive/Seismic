package me.bsu.seismic;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.bsu.seismic.dbmodels.Earthquake;
import me.bsu.seismic.models.usgs.Earthquakes;
import me.bsu.seismic.other.RecyclerItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class EarthquakesListFragment extends Fragment {

    public static final String TAG = "LIST_FRAGMENT";


    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mEarthquakesAdapter;
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
//        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
//                getActivity()
//        ));
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
        Log.d(TAG, "update earthquakes called");
        mEarthquakesAdapter = new EarthquakesCursorRecyclerAdapter(Earthquake.fetchResultCursor(), getActivity());
        mRecyclerView.swapAdapter(mEarthquakesAdapter, true);
    }
}
