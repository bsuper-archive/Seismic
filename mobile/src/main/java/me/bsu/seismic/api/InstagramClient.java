package me.bsu.seismic.api;

import me.bsu.seismic.models.instagram.InstagramResponse;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

public class InstagramClient {

    private static InstagramApiInterface sInstagramApiInterface;

    public static InstagramApiInterface getInstagramApiClient() {
        if (sInstagramApiInterface == null) {
            Retrofit r = new Retrofit.Builder()
                    .baseUrl("https://api.instagram.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            sInstagramApiInterface = r.create(InstagramApiInterface.class);
        }
        return sInstagramApiInterface;
    }

    public interface InstagramApiInterface {
        @GET("/v1/media/search")
        Call<InstagramResponse> getMedia(@Query("lat") float lat, @Query("lng") float lng,
                                         @Query("distance") int distance, @Query("client_id") String clientId);

    }
}