package me.bsu.seismic.api;

import java.util.List;

import me.bsu.seismic.models.Earthquakes;
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
        Call<Earthquakes> getRecentEarthquakes(@Query("limit") int limit, @Query("format") String format);
    }
}
