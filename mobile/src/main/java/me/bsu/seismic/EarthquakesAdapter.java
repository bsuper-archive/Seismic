package me.bsu.seismic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

import me.bsu.seismic.models.usgs.Earthquakes;
import me.bsu.seismic.models.usgs.Feature;

public class EarthquakesAdapter extends RecyclerView.Adapter<EarthquakesAdapter.ViewHolder> {

    private List<Feature> mIncidents;


    public EarthquakesAdapter(Earthquakes earthquakes) {
        mIncidents = earthquakes.getFeatures();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView location, timeSince, magnitude, type;
//        public ImageView img;
        public ViewHolder(View v) {
            super(v);
            type = (TextView) v.findViewById(R.id.list_item_type);
            location = (TextView) v.findViewById(R.id.list_item_location);
            timeSince = (TextView) v.findViewById(R.id.list_item_time_since);
            magnitude = (TextView) v.findViewById(R.id.list_item_magnitude);

//            img = (ImageView) v.findViewById(R.id.list_item_animal_img);
        }
    }

    @Override
    public EarthquakesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_earthquake, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EarthquakesAdapter.ViewHolder holder, int position) {
        Feature e = mIncidents.get(position);
        holder.type.setText(WordUtils.capitalizeFully(e.getProperties().getType()));
        holder.location.setText(e.getProperties().getPlace());
        holder.magnitude.setText(String.valueOf(e.getProperties().getMag()));
        holder.timeSince.setText(Utils.getTimeDifferenceFromCurrentTime(e.getProperties().getTime()));
    }

    @Override
    public int getItemCount() {
        return mIncidents.size();
    }



}
