package me.bsu.seismic.api;

import me.bsu.seismic.models.usgs.Earthquakes;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

public class USGSClient {

    private static USGSApiInterface sUSGSApiInterface;

    public static USGSApiInterface getUSGSApiClient() {
        if (sUSGSApiInterface == null) {
            Retrofit r = new Retrofit.Builder()
                    .baseUrl("http://earthquake.usgs.gov/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            sUSGSApiInterface = r.create(USGSApiInterface.class);
        }
        return sUSGSApiInterface;
    }

    public interface USGSApiInterface {
        @GET("/fdsnws/event/1/query")
        Call<Earthquakes> getRecentEarthquakes(@Query("minmagnitude") double minMagnitude, @Query("limit") int limit, @Query("format") String format);

        @GET("/earthquakes/feed/v1.0/summary/all_hour.geojson")
        Call<Earthquakes> getRecentEarthquakesFromFeed();
//
//        @GET("/fdsnws/event/1/query")
//        Call<Feature> getEarthquake(@Query("eventid") String eventid, @Query("format") String format);
    }
}
