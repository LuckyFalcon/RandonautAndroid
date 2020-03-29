package com.randonautica.app.Attractors;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;

import com.randonautica.app.Interfaces.API_Classes.Entropy;
import com.randonautica.app.Interfaces.API_Classes.Pools;
import com.randonautica.app.Interfaces.API_Classes.Sizes;
import com.randonautica.app.Interfaces.RandoWrapperApi;
import com.randonautica.app.Interfaces.RandonautEntropyListener;
import com.randonautica.app.MyRandonautFragment;

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

public class GenerateEntropy {

    //Used for Attractor generation
    private String GID;
    private int hexsize;

    //Initialize Class objects
    ProgressDialog progressdialog;
    RandoWrapperApi randoWrapperApi;

    public void getANUQuantumEntropy(final Context context, int distance, final RandonautEntropyListener randonautDialogsListener){

        //Start ProgressDialog
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Getting quantum entropy. Please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(MyRandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                hexsize = response.body().getHexsize();

                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, false, false);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        GID = response.body().getGid();
                        MyRandonautFragment.entropy = MyRandonautFragment.entropy + hexsize;
                        progressdialog.dismiss();
                        randonautDialogsListener.onData(GID);
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        onCreateDialogErrorGettingEntropy(context);

                        progressdialog.dismiss();
                    }
                });
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                if(t instanceof SocketTimeoutException){
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

    public void getTemporalEntropy(final Context context, int distance, final RandonautEntropyListener randonautDialogsListener){

        //Start ProgressDialog
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Getting Temporal entropy. Please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(MyRandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                hexsize = response.body().getHexsize();

                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, false, true);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        GID = response.body().getGid();
                        MyRandonautFragment.entropy = MyRandonautFragment.entropy + hexsize;
                        progressdialog.dismiss();
                        randonautDialogsListener.onData(GID);
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        progressdialog.dismiss();
                    }
                });
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                if(t instanceof SocketTimeoutException){
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

    public void getGCPEntropy(final Context context, int distance, final RandonautEntropyListener randonautDialogsListener){

        //Start ProgressDialog
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Getting GCP entropy. Please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(MyRandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                hexsize = response.body().getHexsize();

                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, true, false);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        GID = response.body().getGid();
                        MyRandonautFragment.entropy = MyRandonautFragment.entropy + hexsize;
                        progressdialog.dismiss();
                        randonautDialogsListener.onData(GID);
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        progressdialog.dismiss();
                    }
                });
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                if(t instanceof SocketTimeoutException){
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
                .baseUrl(new String(Base64.decode(MyRandonautFragment.getBaseApi(),Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);
        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                hexsize = response.body().getHexsize();
                progressdialog.dismiss();
                randonautDialogsListener.onData(String.valueOf(hexsize));
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
                .baseUrl(new String(Base64.decode(MyRandonautFragment.getBaseApi(),Base64.DEFAULT)))
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
