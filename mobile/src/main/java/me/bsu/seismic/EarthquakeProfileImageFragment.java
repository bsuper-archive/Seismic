package me.bsu.seismic;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import me.bsu.seismic.api.InstagramClient;
import me.bsu.seismic.models.instagram.Datum;
import me.bsu.seismic.models.instagram.InstagramResponse;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class EarthquakeProfileImageFragment extends Fragment {

    public static final String TAG = "PROFILE_IMAGE_FRAG";

    private static final String LAT = "LAT";
    private static final String LNG = "LNG";

    private float lat, lng;
    RecyclerView mRecyclerView;
    private EarthquakeProfileImageAdapter mAdapter;
    private ArrayList<String> urls;

    private OnFragmentInteractionListener mListener;

    public static EarthquakeProfileImageFragment newInstance(float lat, float lng) {
        EarthquakeProfileImageFragment fragment = new EarthquakeProfileImageFragment();
        Bundle args = new Bundle();
        args.putFloat(LAT, lat);
        args.putFloat(LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    public EarthquakeProfileImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getFloat(LAT);
            lng = getArguments().getFloat(LNG);
        }
        loadPhotos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_earthquake_profile_image, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.listview_earthquake_profile_images);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void loadPhotos() {
        Call<InstagramResponse> call = InstagramClient.getInstagramApiClient().getMedia(lat, lng, 5000, Utils.INSTAGRAM_CLIENT_ID);
        call.enqueue(new Callback<InstagramResponse>() {
            @Override
            public void onResponse(Response<InstagramResponse> response, Retrofit retrofit) {
                Log.d(TAG, response.raw().request().urlString());
//                consumeUSGSApiData(response.body());
                if (response.isSuccess()) {
                    InstagramResponse e = response.body();
                    urls = getUrls(e);
                    showImages();
                    Log.d(TAG, "success!!");
                } else {
                    int statusCode = response.code();
                    Log.d(TAG, "failure!!");

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

    private ArrayList<String> getUrls(InstagramResponse r) {
        ArrayList<String> urls = new ArrayList<>();
        List<Datum> data = r.getData();
        Log.d(TAG, "# Images: " + data.size());
        for (Datum datum : data) {
            Log.d(TAG, datum.getCreatedTime());
            if (datum.getImages() != null && datum.getImages().getStandardResolution() != null) {
                String url = datum.getImages().getStandardResolution().getUrl();
                Log.d(TAG, "Image url: " + url);
                urls.add(url);
            }
        }
        return urls;
    }

    public void showImages() {
        Log.d(TAG, "show images called");
        mAdapter = new EarthquakeProfileImageAdapter(urls, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

}
