package me.bsu.seismic;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import me.bsu.seismic.other.CursorRecyclerAdapter;

public class EarthquakesCursorRecyclerAdapter extends CursorRecyclerAdapter<EarthquakesCursorRecyclerAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView location, timeSince, magnitude, type;
        public ViewHolder(View v) {
            super(v);
            type = (TextView) v.findViewById(R.id.list_item_type);
            location = (TextView) v.findViewById(R.id.list_item_location);
            timeSince = (TextView) v.findViewById(R.id.list_item_time_since);
            magnitude = (TextView) v.findViewById(R.id.list_item_magnitude);
        }
    }

    private Context mContext;

    public EarthquakesCursorRecyclerAdapter(Cursor cursor, Context context) {
        super(cursor);
        mContext = context;
    }

    @Override
    public EarthquakesCursorRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_earthquake, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EarthquakesCursorRecyclerAdapter.ViewHolder holder, Cursor cursor) {
        holder.type.setText(WordUtils.capitalizeFully(cursor.getString(cursor.getColumnIndexOrThrow("type"))));
        holder.location.setText(cursor.getString(cursor.getColumnIndexOrThrow("place")));
        holder.magnitude.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("magnitude"))));
        holder.magnitude.setTextColor(ContextCompat.getColor(mContext, Utils.getEarthquakeColor(cursor.getDouble(cursor.getColumnIndexOrThrow("magnitude")))));
        holder.timeSince.setText(Utils.getTimeDifferenceFromCurrentTime(cursor.getLong(cursor.getColumnIndexOrThrow("time"))));
    }

}
