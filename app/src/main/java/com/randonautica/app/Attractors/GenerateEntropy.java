package com.randonautica.app.Attractors;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.randonautica.app.Classes.SingleRecyclerViewLocation;
import com.randonautica.app.Interfaces.API_Classes.Entropy;
import com.randonautica.app.Interfaces.API_Classes.Pools;
import com.randonautica.app.Interfaces.API_Classes.Sizes;
import com.randonautica.app.Interfaces.RandoWrapperApi;
import com.randonautica.app.Interfaces.RandonautEntropyListener;
import com.randonautica.app.RandonautFragment;

import org.jetbrains.annotations.NotNull;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.randonautica.app.Attractors.GenerateAttractors.locationList;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class GenerateEntropy {

    //Used for Attractor generation
    private String GID;
    private int hexsize;

    //Initialize Class objects
    ProgressDialog progressdialog;
    RandoWrapperApi randoWrapperApi;

    public void getANUQuantumEntropy(final Context context, final int distance, final FusedLocationProviderClient mFusedLocationProviderClient, final GoogleMap mapboxMap, final String selected, final RandonautEntropyListener randonautDialogsListener){

        //Start ProgressDialog
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Getting quantum entropy, focus on your intent.");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(175, TimeUnit.SECONDS)
                .readTimeout(175, TimeUnit.SECONDS)
                .writeTimeout(175, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                try {
                    hexsize = response.body().getHexsize();

                    Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, false, false);

                    callGetEntropy.enqueue(new Callback<Entropy>() {

                        @Override
                        public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                            try {
                            GID = response.body().getGid();
                            RandonautFragment.entropy = RandonautFragment.entropy + hexsize;
                            progressdialog.dismiss();
                            randonautDialogsListener.onData(GID);

                            }catch (Exception e) {
                                // This will catch any exception, because they are all descended from Exception
                                final Task location = mFusedLocationProviderClient.getLastLocation();
                                location.addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {

                                            final Location currentLocation = (Location) task.getResult();

                                            createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                            progressdialog.cancel();

                                        } else {
                                            onCreateDialogErrorGettingEntropy(context);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<Entropy> call, Throwable t) {
                            onCreateDialogErrorGettingEntropy(context);

                            progressdialog.dismiss();
                        }
                    });
                }catch (Exception e) {
                    // This will catch any exception, because they are all descended from Exception
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                final Location currentLocation = (Location) task.getResult();

                                createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                progressdialog.cancel();

                            } else {
                                onCreateDialogErrorGettingEntropy(context);
                            }
                        }
                    });
                    }
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                if(t instanceof SocketTimeoutException){
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                final Location currentLocation = (Location) task.getResult();

                                createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                progressdialog.cancel();

                            } else {
                                onCreateDialogErrorGettingEntropy(context);
                            }
                        }
                    });
                    progressdialog.dismiss();
                } else {
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                final Location currentLocation = (Location) task.getResult();

                                createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                progressdialog.cancel();

                            } else {
                                onCreateDialogErrorGettingEntropy(context);
                            }
                        }
                    });
                }
                progressdialog.dismiss();
            }
        });



    }

    public void createDialogEmptyResults(Context context, String selected, final double lat, final double lon, final int radius, final RandonautEntropyListener randonautDialogsListener, final GoogleMap mapboxMap) {

        new AlertDialog.Builder(context)
                .setTitle("Quantum Entropy Attempt")
                .setMessage("There was an error sourcing the entropy needed to randomize a quantum level point. Try a bit later. In the meantime, a random point has been generated for you.")


                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        // getQuantumRandom(lat, lon, radius);

                        double[] testresutlt = getQuantumRandom(lat, lon, radius);

                        Marker marker = mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(testresutlt[0], testresutlt[1]))
                                .title("Void"));

                        marker.showInfoWindow();

                        Location loc = new Location("dummy");
                        loc.setLatitude(testresutlt[0]);
                        loc.setLongitude(testresutlt[1]);

                        addPulsatingEffect(new LatLng(testresutlt[0], testresutlt[1]), mapboxMap, 50);

//                        //Generate Circle
//                        mapboxMap.addPolygon(generatePerimeter(
//                                new LatLng(testresutlt[0], testresutlt[1]),
//                                (attractorLocations[i].getRadiusM()/1000),
//                                64));


                        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();

                        singleLocation.setType(0);
                        singleLocation.setLocationCoordinates(new LatLng(testresutlt[0], testresutlt[1]));

                        locationList.add(singleLocation);
                        randonautDialogsListener.onFailed(locationList);
                        RandonautFragment.startButton.setVisibility(View.GONE);
                        //   navigateButton.setVisibility(View.VISIBLE);
                        RandonautFragment.resetButton.setVisibility(View.VISIBLE);
                        dialog.dismiss();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static double[] getQuantumRandom(double lat, double lon, int radius) {
        double[] result = new double[2];

        Boolean dnn = false;
        while (dnn == false) {
            double lat01 = lat + radius * cos(180 * Math.PI / 180) / (6371000 * Math.PI / 180);
            double dlat = ((lat + radius / (6371000 * Math.PI / 180)) - lat01) * 1000000;
            double lon01 = lon + radius * sin(270 * Math.PI / 180) / cos(lat * Math.PI / 180) / (6371000 * Math.PI / 180);
            double dlon = ((lon + radius * sin(90 * Math.PI / 180) / cos(lat * Math.PI / 180) / (6371000 * Math.PI / 180)) - lon01) * 1000000;
            double lat1 = lat;
            double lon1 = lon;
            double rlat;
            double rlon;
            rlat = ThreadLocalRandom.current().nextInt(0, (int) dlat);
            rlon = ThreadLocalRandom.current().nextInt(0, (int) dlon);
            lat1 = lat01 + (rlat / 1000000);
            lon1 = lon01 + (rlon / 1000000);
            int dif = GetDistance(lat, lon, lat1, lon1);
            if (dif > radius) {
            } else {
                result[0] = lat1;
                result[1] = lon1;
                dnn = true;
            }
        }
        return result;
    }

    public static int GetDistance(double lat0, double lon0, double lat1, double lon1) {
        double dlon = (lon1 - lon0) * Math.PI / 180;
        double dlat = (lat1 - lat0) * Math.PI / 180;

        double a = (sin(dlat / 2) * sin(dlat / 2)) + cos(lat0 * Math.PI / 180) * cos(lat1 * Math.PI / 180) * (sin(dlon / 2) * sin(dlon / 2));
        double angle = 2 * atan2(sqrt(a), sqrt(1 - a));
        return (int) (angle * 6371000);
    }

    private void addPulsatingEffect(final LatLng userLatlng, final GoogleMap map, int radius) {

        if (RandonautFragment.lastPulseAnimator != null) {
            RandonautFragment.lastPulseAnimator.cancel();
        }
        if (RandonautFragment.lastUserCircle != null) {
            RandonautFragment.lastUserCircle.remove();
            RandonautFragment.lastUserCircle.setCenter(userLatlng);
        }

        RandonautFragment.lastPulseAnimator = valueAnimate(getDisplayPulseRadius(radius, map), RandonautFragment.pulseDuration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (RandonautFragment.lastUserCircle != null)
                    RandonautFragment.lastUserCircle.setRadius((Float) animation.getAnimatedValue());
                else {
                    RandonautFragment.lastUserCircle = map.addCircle(new CircleOptions()
                            .center(userLatlng)
                            .radius(getDisplayPulseRadius((Float) animation.getAnimatedValue(), map))
                            .strokeColor(Color.RED));
                    //.fillColor(Color.BLUE));
                    RandonautFragment.lastUserCircle.setFillColor(adjustAlpha(0x220000FF, 1 - animation.getAnimatedFraction()));


                }
            }
        });

    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    protected ValueAnimator valueAnimate(float accuracy, long duration, ValueAnimator.AnimatorUpdateListener updateListener) {
        ValueAnimator va = ValueAnimator.ofFloat(0, accuracy);
        va.setDuration(duration);
        va.addUpdateListener(updateListener);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);

        va.start();
        return va;
    }

    protected float getDisplayPulseRadius(float radius, @NotNull GoogleMap map) {
        float diff = 1;
        if (diff < 3)
            return radius;
        if (diff < 3.7)
            return radius * (diff / 2);
        if (diff < 4.5)
            return (radius * diff);
        if (diff < 5.5)
            return (radius * diff) * 1.5f;
        if (diff < 7)
            return (radius * diff) * 2f;
        if (diff < 7.8)
            return (radius * diff) * 3.5f;
        if (diff < 8.5)
            return (float) (radius * diff) * 5;
        if (diff < 10)
            return (radius * diff) * 10f;
        if (diff < 12)
            return (radius * diff) * 18f;
        if (diff < 13)
            return (radius * diff) * 28f;
        if (diff < 16)
            return (radius * diff) * 40f;
        if (diff < 18)
            return (radius * diff) * 60;
        return (radius * diff) * 80;
    }

    public void getTemporalEntropy(final Context context, final int distance, final FusedLocationProviderClient mFusedLocationProviderClient, final GoogleMap mapboxMap, final String selected, final RandonautEntropyListener randonautDialogsListener){

        //Start ProgressDialog
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Getting quantum entropy, focus on your intent.");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(175, TimeUnit.SECONDS)
                .readTimeout(175, TimeUnit.SECONDS)
                .writeTimeout(175, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                try {
                 hexsize = response.body().getHexsize() / 2;
                 Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, false, true);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        try{
                        GID = response.body().getGid();
                        RandonautFragment.entropy = (RandonautFragment.entropy + hexsize);
                        progressdialog.dismiss();
                        randonautDialogsListener.onData(GID);

                    }catch (Exception e) {
                        // This will catch any exception, because they are all descended from Exception
                        final Task location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {

                                    final Location currentLocation = (Location) task.getResult();

                                    createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                    progressdialog.cancel();

                                } else {
                                    onCreateDialogErrorGettingEntropy(context);
                                }
                            }
                        });
                    }
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        progressdialog.dismiss();
                    }
                });

            } catch (Exception e) {
                // This will catch any exception, because they are all descended from Exception
                    // This will catch any exception, because they are all descended from Exception
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            final Location currentLocation = (Location) task.getResult();

                            createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                            progressdialog.cancel();

                        } else {
                            onCreateDialogErrorGettingEntropy(context);
                        }
                    }
                });
            }
            }

            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                if(t instanceof SocketTimeoutException){
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                final Location currentLocation = (Location) task.getResult();

                                createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                progressdialog.cancel();

                            } else {
                                onCreateDialogErrorGettingEntropy(context);
                            }
                        }
                    });
                } else {
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                final Location currentLocation = (Location) task.getResult();

                                createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                progressdialog.cancel();

                            } else {
                                onCreateDialogErrorGettingEntropy(context);
                            }
                        }
                    });
                }
                progressdialog.dismiss();
            }
        });

    }

    public void getGCPEntropy(final Context context, final int distance, final FusedLocationProviderClient mFusedLocationProviderClient, final GoogleMap mapboxMap, final String selected, final RandonautEntropyListener randonautDialogsListener){

        //Start ProgressDialog
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Getting quantum entropy, focus on your intent.");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(175, TimeUnit.SECONDS)
                .readTimeout(175, TimeUnit.SECONDS)
                .writeTimeout(175, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                try {
                hexsize = response.body().getHexsize();

                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, true, false);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        try{
                        GID = response.body().getGid();
                        RandonautFragment.entropy = RandonautFragment.entropy + hexsize;
                        progressdialog.dismiss();
                        randonautDialogsListener.onData(GID);
                    }catch (Exception e) {
                        // This will catch any exception, because they are all descended from Exception
                        final Task location = mFusedLocationProviderClient.getLastLocation();
                        location.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {

                                    final Location currentLocation = (Location) task.getResult();

                                    createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                    progressdialog.cancel();

                                } else {
                                    onCreateDialogErrorGettingEntropy(context);
                                }
                            }
                        });
                    }
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        progressdialog.dismiss();
                    }
                });
                }catch (Exception e) {
                    // This will catch any exception, because they are all descended from Exception
                    // This will catch any exception, because they are all descended from Exception
                    // This will catch any exception, because they are all descended from Exception
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                final Location currentLocation = (Location) task.getResult();

                                createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                progressdialog.cancel();

                            } else {
                                onCreateDialogErrorGettingEntropy(context);
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                if(t instanceof SocketTimeoutException){
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                final Location currentLocation = (Location) task.getResult();

                                createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                progressdialog.cancel();

                            } else {
                                onCreateDialogErrorGettingEntropy(context);
                            }
                        }
                    });
                } else {
                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                final Location currentLocation = (Location) task.getResult();

                                createDialogEmptyResults(context, selected, currentLocation.getLatitude(), currentLocation.getLongitude(), distance, randonautDialogsListener, mapboxMap);
                                progressdialog.cancel();

                            } else {
                                onCreateDialogErrorGettingEntropy(context);
                            }
                        }
                    });
                }
                progressdialog.dismiss();
            }
        });



    }

    public void getNeededEntropySize(final Context context, int distance, final RandonautEntropyListener randonautDialogsListener){
        //Start ProgressDialog
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Getting quantum entropy size needed. Please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);
        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                try {


                hexsize = response.body().getHexsize();
                progressdialog.dismiss();
                randonautDialogsListener.onData(String.valueOf(hexsize));
                }catch (Exception e) {
                    // This will catch any exception, because they are all descended from Exception
                    onCreateDialogStartingCamera(context);
                    progressdialog.cancel();


                }
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    onCreateDialogErrorGettingEntropy(context);
                    progressdialog.dismiss();
                } else {
                    onCreateDialogErrorGettingSize(context);
                    progressdialog.dismiss();
                }
                progressdialog.dismiss();
            }
        });


    }

    public void poolQuantumEntropy(final Context context, int distance, final RandonautEntropyListener randonautDialogsListener){
        //Start ProgressDialog
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Getting ANU Leftovers quantum entropy. Please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);


        Call<List<Pools>> callGetPools = randoWrapperApi.getPools();

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetPools.enqueue(new Callback<List<Pools>>() {

            @Override
            public void onResponse(Call<List<Pools>> call, Response<List<Pools>> response) {

                int count = 0;
                int current = 0;
                int amount = 0;

                for(Pools pool: response.body()){
                    count ++;
                }

                int randomNum = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    randomNum = ThreadLocalRandom.current().nextInt(0, count);
                } else {
                    randomNum = 2;
                }


                for(Pools pools: response.body()){
                    if(current == randomNum){

                        GID = pools.getPool().substring(0, (pools.getPool().length() -5));

                        progressdialog.dismiss();
                        randonautDialogsListener.onData(GID);

                    }
                    current++;
                }
            }

            @Override
            public void onFailure(Call<List<Pools>> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    onCreateDialogErrorGettingEntropy(context);
                    progressdialog.dismiss();
                } else {
                    onCreateDialogErrorGettingSize(context);
                    progressdialog.dismiss();
                }
                progressdialog.dismiss();
            }
        });
    }

    public void onCreateDialogErrorGettingEntropy(Context context){

        new AlertDialog.Builder(context)
                .setTitle("Error sourcing quantum entropy")
                .setMessage("Sorry, there was an error sourcing quantum entropy needed to randomize. Try a bit later.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onCreateDialogStartingCamera(Context context){

        new AlertDialog.Builder(context)
                .setTitle("Error starting Camera")
                .setMessage("Sorry, there was an error when trying to start the camera. Try a bit later.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onCreateDialogErrorGettingSize(Context context){

        new AlertDialog.Builder(context)
                .setTitle("Error sourcing quantum entropy")
                .setMessage("Sorry, there was an error sourcing quantum entropy needed to randomize. Make sure you are connected to the Internet!")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
