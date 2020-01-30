package com.randonautica.app.Classes;

import java.util.List;

import okhttp3.RequestBody;
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

    @POST("reports/save")
    Call<SendReport.Response> postJson(@Body RequestBody params);

    @GET("attractors")
    Call<List<Attractors>> getAttractors(@Query("gid") String gid,
                                         @Query("center[0]") double  center0,
                                         @Query("center[1]") double center1,
                                         @Query("radius") int radius,
                                         @Query("pool") boolean pool,
                                         @Query("gcp") boolean gcp);
    @GET("attractors")
    Call<List<Attractors>> getAttractorsTest(@Query("gid") String gid,
                                            @Query("center[0]") double  center0,
                                            @Query("center[1]") double center1,
                                             @Query("radius") int radius,
                                             @Query("pool") boolean pool,
                                             @Query("gcp") boolean gcp);


    @GET("pseudo")
    Call<List<Psuedo>> getPsuedo(@Query("n") int n,
                                 @Query("center[0]") double  center0,
                                 @Query("center[1]") double center1,
                                 @Query("radius") int radius,
                                 @Query("seed") int seed,
                                 @Query("filtering") int filtering);

    @GET("getpools")
    Call<List<Pools>> getPools();


}
