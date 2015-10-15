package me.bsu.seismic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EarthquakeProfileImageAdapter extends RecyclerView.Adapter<EarthquakeProfileImageAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;

        public ViewHolder(View v) {
            super(v);
            img = (ImageView) v.findViewById(R.id.profile_img_view);
        }
    }

    private ArrayList<String> urls;
    private Context context;

    public EarthquakeProfileImageAdapter(ArrayList<String> urls, Context context) {
        this.urls = urls;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_earthquake_profile_image, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context).load(urls.get(position)).resize(150, 150)
                .centerCrop().into(holder.img);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }
}

