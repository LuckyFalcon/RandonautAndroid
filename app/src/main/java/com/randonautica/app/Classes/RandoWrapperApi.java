package com.randonautica.app.Classes;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RandoWrapperApi {

    @GET("sizes")
    Call<Sizes> getSizes(@Query("radius") int  radius);

    @GET("entropy")
    Call<Entropy> getEntropy(@Query("size") int  hexSize,
                             @Query("raw") boolean raw,
                             @Query("gcp") boolean gcp);

    @POST("sendreport")
    SendReport postJson(@Body SendReport body);

    @GET("attractors")
    Call<List<Attractors>> getAttractors(@Query("gid") String gid,
                                         @Query("center[0]") double  center0,
                                         @Query("center[1]") double center1,
                                         @Query("radius") int radius,
                                         @Query("pool") boolean pool);

    @GET("pseudo")
    Call<List<Psuedo>> getPsuedo(@Query("n") int n,
                                 @Query("center[0]") double  center0,
                                 @Query("center[1]") double center1,
                                 @Query("radius") int radius,
                                 @Query("seed") int seed,
                                 @Query("filtering") int filtering);

    @GET("getpools")
    Call<List<Pools>> getPools();


    @GET("https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/pin-s-marker+285A98({dest_lat},{dest_long})/{curr_lat},{curr_long},13,10/300x200?access_token=pk.eyJ1IjoiZGF2aWRmYWxjb24iLCJhIjoiY2szbjRzZmd2MTcwNDNkcXhnbTFzbHR0cCJ9.ZgbfsJXtrCFgI0rRJkwUyg")
    Call<ResponseBody> fetchStaticImage(@Path("dest_lat")  double dest_center0,
                                        @Path("dest_long") double dest_center1,
                                        @Path("curr_lat")  double curr_center0,
                                        @Path("curr_long") double curr_center1);

}
