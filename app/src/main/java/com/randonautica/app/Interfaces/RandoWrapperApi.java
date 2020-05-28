package com.randonautica.app.Interfaces;

import com.randonautica.app.Classes.Verify;
import com.randonautica.app.Interfaces.API_Classes.Attractors;
import com.randonautica.app.Interfaces.API_Classes.Entropy;
import com.randonautica.app.Interfaces.API_Classes.GoAttractors;
import com.randonautica.app.Interfaces.API_Classes.Point;
import com.randonautica.app.Interfaces.API_Classes.Pools;
import com.randonautica.app.Interfaces.API_Classes.PseudoAttractor;
import com.randonautica.app.Interfaces.API_Classes.SendEntropy;
import com.randonautica.app.Interfaces.API_Classes.SendReport;
import com.randonautica.app.Interfaces.API_Classes.Sizes;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RandoWrapperApi {

    @GET("sizes")
    Call<Sizes> getSizes(@Query("radius") int  radius);

    @GET("entropy")
    Call<Entropy> getEntropy(@Query("size") int  hexSize,
                             @Query("raw") boolean raw,
                             @Query("gcp") boolean gcp,
                             @Query("temporal") boolean temporal);

    @POST("setentropy")
    Call<SendEntropy.Response> postEntropyJson(@Field("size") String size,
                                               @Field("entropy") String entropy);

    @POST("checkreceipt")
    Call<Verify> postReceiptJson(@Body RequestBody params);

    @POST("savePoints")
    Call<Verify> postSavePointsJson(@Body RequestBody params);

    @POST("checkPoints")
    Call<Verify> postcheckPointsJson(@Body RequestBody params);

    @POST("updatepoints")
    Call<Verify> postupdatePointsJson(@Body RequestBody params);


    @POST("reports/save")
    Call<SendReport.Response> postJson(@Body RequestBody params);

    @GET("attractors")
    Call<GoAttractors> getAttractors(@Query("gid") String gid,
                                     @Query("x") double  center0,
                                     @Query("y") double center1,
                                     @Query("radius") int radius,
                                     @Query("pool") boolean pool,
                                     @Query("temporal") boolean temporal,
                                     @Query("gcp") boolean gcp);
    @GET("serversideattractors")
    Call<GoAttractors> getAttractorsUpgrade(@Query("gid") String gid,
                                            @Query("x") double  center0,
                                            @Query("y") double center1,
                                            @Query("radius") int radius,
                                            @Query("pool") boolean pool,
                                            @Query("temporal") boolean temporal,
                                            @Query("gcp") boolean gcp,
                                            @Query("selected") String selected);

    @GET("pseudo")
    Call<List<PseudoAttractor>> getPsuedo(@Query("n") int n,
                                          @Query("x") double  center0,
                                          @Query("y") double center1,
                                          @Query("radius") int radius,
                                          @Query("seed") int seed,
                                          @Query("filtering") int filtering);

    @GET("getpools")
    Call<List<Pools>> getPools();


}
