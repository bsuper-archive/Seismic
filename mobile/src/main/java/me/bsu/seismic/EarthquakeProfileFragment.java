package me.bsu.seismic;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * A placeholder fragment containing a simple view.
 */
public class EarthquakeProfileFragment extends Fragment {

    public EarthquakeProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(Utils.TWITTER_KEY, Utils.TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_profile, container, false);
    }
}
