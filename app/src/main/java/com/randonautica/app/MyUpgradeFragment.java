package com.randonautica.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.randonautica.app.Classes.DatabaseHelper;
import com.randonautica.app.Classes.Verify;
import com.randonautica.app.Interfaces.API_Classes.Entropy;
import com.randonautica.app.Interfaces.API_Classes.SendEntropy;
import com.randonautica.app.Interfaces.API_Classes.SendReport;
import com.randonautica.app.Interfaces.API_Classes.Sizes;
import com.randonautica.app.Interfaces.RandoWrapperApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyUpgradeFragment extends Fragment implements PurchasesUpdatedListener {

    private BillingClient billingClient;
    DatabaseHelper mDatabaseHelper;

    private static int limitanomalies;
    private static int limitattractors;
    private static int limitvoids;
    private static int waterpoints;
    private static int extendradius;
    private static int showSyncbutton;

    PurchaseHistoryRecord purchaseInfinte = null;
    PurchaseHistoryRecord purchase60 = null;
    PurchaseHistoryRecord purchase20 = null;
    PurchaseHistoryRecord purchaseExtendradius = null;
    PurchaseHistoryRecord purchasewaterPoints = null;


    SkuDetails skuDetailsInfinte = null;
    SkuDetails skuDetails60 = null;
    SkuDetails skuDetails20 = null;
    SkuDetails skuDetailsWater = null;
    SkuDetails skuDetailsGetExtendradius = null;

    Boolean enableInfinte = false;
    Boolean enable60 = false;
    Boolean enable20 = false;
    Boolean enableWater = false;
    Boolean enableExtendradius = false;

    String currentSKU = null;

    androidx.appcompat.widget.Toolbar toolbar;

    //Buttons
    Button Buttoninfinte;
    Button Button20;
    Button Button60;
    Button ButtonWaterPoints;
    Button buttonExtendRadius;
    Button syncButton;

    List<PurchaseHistoryRecord> listHistory;

    //Storing information globally
    public static final String STATS = "stats";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Set title
        getActivity().setTitle("Upgrade");

        loadData();

        return inflater.inflate(R.layout.fragment_upgrade, container, false);
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (androidx.appcompat.widget.Toolbar) getActivity().findViewById(R.id.toolbar);

        //Buttons
        Buttoninfinte = (Button) view.findViewById(R.id.buttongetUnlimitedPoints);
        Button20 = (Button) view.findViewById(R.id.buttonGet20Points);
        Button60 = (Button) view.findViewById(R.id.buttonGet60Points);
        ButtonWaterPoints = (Button) view.findViewById(R.id.buttonWaterPoints);
        buttonExtendRadius = (Button) view.findViewById(R.id.buttonExtendRadius);
        syncButton = (Button) view.findViewById(R.id.syncButton);

        //Infinite points
        final TextView textViewgetUnlimitedPointsPrice = view.findViewById(R.id.getUnlimitedPointsPrice);

        //60 points
        final TextView textViewget60PointsPrice = view.findViewById(R.id.get60PointsPrice);

        //20 points
        final TextView textViewget20PointsPrice = view.findViewById(R.id.get20PointsPrice);

        //Water points
        final TextView textViewWaterPoints = view.findViewById(R.id.textViewWaterPoints);
        final TextView textViewWaterPointsDesc = view.findViewById(R.id.textViewWaterPointsDesc);
        final TextView textViewWaterPointsPrice = view.findViewById(R.id.textViewWaterPointsPrice);

        //Extend radius
        final TextView textViewExtendRadius = view.findViewById(R.id.textViewExtendRadius);
        final TextView textViewExtendRadiusDesc = view.findViewById(R.id.textViewExtendRadiusDesc);
        final TextView textViewExtendRadiusPrice = view.findViewById(R.id.textViewExtendRadiusPrice);

        billingClient = BillingClient.newBuilder(getContext())
                .enablePendingPurchases()
                .setListener(this).build();

        Log.d("test", ""+showSyncbutton);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                            new PurchaseHistoryResponseListener() {
                                @Override
                                public void onPurchaseHistoryResponse(BillingResult billingResult, List<PurchaseHistoryRecord> list) {
                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                                            && list != null) {
                                        for (PurchaseHistoryRecord purchase : list) {
                                            // Process the result.
                                            Log.d("test", ""+purchase);
                                            if ("infinte_points".equals(purchase.getSku())) {
                                                Log.d("test", ""+purchase);
                                                Buttoninfinte.setText("Enable");
                                                enableInfinte = true;
                                                purchaseInfinte = purchase;
                                            }
                                            else if ("get_more_points".equals(purchase.getSku())) {
                                                listHistory = list;
                                                if(showSyncbutton != 1 && limitanomalies != Integer.MAX_VALUE){
                                                    syncButton.setVisibility(view.VISIBLE);
                                                }
                                            }
                                            else if ("get_points".equals(purchase.getSku())) {
                                                listHistory = list;
                                                if(showSyncbutton != 1 && limitanomalies != Integer.MAX_VALUE){
                                                    syncButton.setVisibility(view.VISIBLE);
                                                }
                                            }
                                            else if ("skip_water_points".equals(purchase.getSku())) {
                                                ButtonWaterPoints.setText("Enable");
                                                enableWater = true;
                                                purchasewaterPoints = purchase;

                                            }
                                            else if ("extend_radius".equals(purchase.getSku())) {
                                                buttonExtendRadius.setText("Enable");
                                                enableExtendradius = true;
                                                purchaseExtendradius = purchase;
                                            }

                                        }
                                        if(limitattractors == Integer.MAX_VALUE){
                                            Buttoninfinte.setText("Enabled");
                                            Buttoninfinte.setEnabled(false);
                                            Button60.setText("Enabled");
                                            Button60.setEnabled(false);
                                            Button20.setText("Enabled");
                                            Button20.setEnabled(false);
                                        }

                                        if(waterpoints == 1){
                                            ButtonWaterPoints.setText("Enabled");
                                            ButtonWaterPoints.setEnabled(false);
                                        }
                                        if(extendradius == 1){
                                            buttonExtendRadius.setText("Enabled");
                                            buttonExtendRadius.setEnabled(false);
                                        }
                                    }
                                }
                            });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });


        billingClient = BillingClient.newBuilder(getContext())
                .enablePendingPurchases()
                .setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //The BillingClient is ready. You can query purchases here.
                    List<String> skuList = new ArrayList<>();
                    skuList.add("get_points"); //20
                    skuList.add("get_more_points"); //60
                    skuList.add("infinte_points"); //infinite
                    skuList.add("skip_water_points"); //Skip water
                    skuList.add("extend_radius"); //Extend radius
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    for (SkuDetails skuDetails : skuDetailsList) {
                                        String sku = skuDetails.getSku();
                                        String title = skuDetails.getTitle();
                                        String skuDesc = skuDetails.getDescription();
                                        String price = skuDetails.getPrice();
                                        if ("infinte_points".equals(sku)) {
                                            textViewgetUnlimitedPointsPrice.setText(price);
                                            skuDetailsInfinte = skuDetails;
                                        } else if ("get_more_points".equals(sku)) {
                                            textViewget60PointsPrice.setText(price);
                                            skuDetails60 = skuDetails;

                                        } else if ("get_points".equals(sku)) {
                                            textViewget20PointsPrice.setText(price);
                                            skuDetails20 = skuDetails;

                                        } else if ("skip_water_points".equals(sku)) {
                                            textViewWaterPoints.setText("Skip water points");
                                            textViewWaterPointsDesc.setText("Skips Points generated in water");
                                            textViewWaterPointsPrice.setText(price);
                                            skuDetailsWater = skuDetails;

                                        } else if ("extend_radius".equals(sku)) {
                                            textViewExtendRadius.setText("Extend radius to 20000 meters");
                                            textViewExtendRadiusDesc.setText(skuDesc);
                                            textViewExtendRadiusPrice.setText(price);
                                            skuDetailsGetExtendradius = skuDetails;

                                        }
                                    }
                                }
                            });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

            }
        });

        Buttoninfinte.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                if(enableInfinte == true){
                    validateItem(purchaseInfinte);
                } else {
                    purchaseItem(skuDetailsInfinte);
                }
            }
        });

        Button60.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                    purchaseItem(skuDetails60);
            }
        });

        Button20.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                    purchaseItem(skuDetails20);
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
//                if(enableInfinte == true){
//                    dialogOnEnableInfinte();
//                } else {
                    validatePoints();
           //     }
            }
        });

        ButtonWaterPoints.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                if(enableWater == true){
                    validateItem(purchasewaterPoints);
                } else {
                    purchaseItem(skuDetailsWater);
                }
            }
        });

        buttonExtendRadius.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                if(enableExtendradius == true){
                    validateItem(purchaseExtendradius);
                } else {
                    purchaseItem(skuDetailsGetExtendradius);
                }
            }
        });

        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                toolbar.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_header_selector_night));
                toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                toolbar.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_header_selector));
                toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                toolbar.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_header_selector));
                toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
                break;
        }

    }

    public void validateItem(final PurchaseHistoryRecord purchase){
        currentSKU = purchase.getSku();

        //Start ProgressDialog
        final ProgressDialog progressdialog = new ProgressDialog(getContext());
        progressdialog.setMessage("Enabling... This will take a second");
        progressdialog.show();
        progressdialog.setCancelable(true);
        progressdialog.setCanceledOnTouchOutside(true);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(), Base64.DEFAULT))+"/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RandoWrapperApi randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        final JSONObject obj = new JSONObject();

        try {
            obj.put("packageName",  "com.randonautica.app");
            obj.put("productId", purchase.getSku());
            obj.put("purchaseToken", purchase.getPurchaseToken());
            obj.put("subscription", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());
        final Call<Verify> response = randoWrapperApi.postReceiptJson(body);

        response.enqueue(new Callback<Verify>() {
            @Override
            public void onResponse(Call<Verify> call, Response<Verify> response) {
                try {
                    if(response.body().getValidated().booleanValue() == true){
                        enable(purchase);
                        progressdialog.dismiss();
                    }

                }catch (Exception e) {
                    Log.d("test", ""+e);

                }
            }
            @Override
            public void onFailure(Call<Verify> call, Throwable t) {
                Log.d("test", ""+t);

            }
        });

    }

    public void purchaseItem(SkuDetails skuDetails){
        currentSKU = skuDetails.getSku();
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        BillingResult responseCode = billingClient.launchBillingFlow(getActivity(), flowParams);
    }

    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.

        } else {
            // Handle any other error codes.
        }
    }

    void enable(PurchaseHistoryRecord purchase){
        if ("infinte_points".equals(purchase.getSku())) {
            limitvoids = Integer.MAX_VALUE;
            limitanomalies = Integer.MAX_VALUE;
            limitattractors = Integer.MAX_VALUE;
            Buttoninfinte.setText("Enabled");
            Buttoninfinte.setEnabled(false);
            Button60.setText("Enabled");
            Button60.setEnabled(false);
            Button20.setText("Enabled");
            Button20.setEnabled(false);
            saveDataPremium();
        }
        else if ("get_more_points".equals(purchase.getSku())) {
            limitattractors = limitattractors+60;
            limitanomalies = limitattractors+60;
            limitvoids = limitattractors+60;
            saveDataPremium();

        }
        else if ("get_points".equals(purchase.getSku())) {
            limitattractors = limitattractors+20;
            limitanomalies = limitattractors+20;
            limitvoids = limitattractors+20;
            saveDataPremium();
        }
        else if ("skip_water_points".equals(purchase.getSku())) {
            waterpoints = 1;
            ButtonWaterPoints.setText("Enabled");
            ButtonWaterPoints.setEnabled(false);
            saveDataPremium();
        }
        else if ("extend_radius".equals(purchase.getSku())) {
            extendradius = 1;
            buttonExtendRadius.setText("Enabled");
            buttonExtendRadius.setEnabled(false);
            saveDataPremium();
        }
    }

    public void validatePoints(){
        //Start ProgressDialog
        final ProgressDialog progressdialog = new ProgressDialog(getContext());

        progressdialog.setMessage("Enabling... This will take a second");
        progressdialog.show();
        progressdialog.setCancelable(true);
        progressdialog.setCanceledOnTouchOutside(true);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(), Base64.DEFAULT))+"/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RandoWrapperApi randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        mDatabaseHelper = new DatabaseHelper(getContext(), "Points");

        for (final PurchaseHistoryRecord purchaseItem : listHistory) {
            final JSONObject obj = new JSONObject();

            try {
                obj.put("packageName",  "com.randonautica.app");
                obj.put("productId", purchaseItem.getSku());
                obj.put("purchaseToken", purchaseItem.getPurchaseToken());
                obj.put("subscription", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());
            final Call<Verify> response = randoWrapperApi.postcheckPointsJson(body);
            Log.d("test","start");

            if(purchaseItem.getSku().equals("get_points")) {
                Log.d("test","start1");

                response.enqueue(new Callback<Verify>() {
                    @Override
                    public void onResponse(Call<Verify> call, Response<Verify> response) {
                        try {
                            if(response.body().getValidated().booleanValue() == true){
                                if(response.body().getPoints() > 0){
                                    Log.d("test","test");
                                    showSyncbutton = 1;
                                    AddDataValidate(purchaseItem.getPurchaseToken(), response.body().getPoints());
                                    limitattractors = limitattractors + response.body().getPoints();
                                    limitanomalies = limitanomalies + response.body().getPoints();
                                    limitvoids = limitvoids + response.body().getPoints();
                                    saveDataPremium();
                                    syncButton.setVisibility(View.GONE);
                                    progressdialog.dismiss();
                                }
                            }

                        }catch (Exception e) {
                            Log.d("test", ""+e);

                        }
                    }
                    @Override
                    public void onFailure(Call<Verify> call, Throwable t) {
                        Log.d("test", ""+t);

                    }
                });

            }
            else if( purchaseItem.getSku().equals("get_more_points")) {

                response.enqueue(new Callback<Verify>() {
                    @Override
                    public void onResponse(Call<Verify> call, Response<Verify> response) {
                        try {
                            if(response.body().getPoints() > 0){
                                Log.d("test","fin");
                                showSyncbutton = 1;

                                AddDataValidate(purchaseItem.getPurchaseToken(), response.body().getPoints());
                                limitattractors = limitattractors + response.body().getPoints();
                                limitanomalies = limitanomalies + response.body().getPoints();
                                limitvoids = limitvoids + response.body().getPoints();
                                saveDataPremium();
                                syncButton.setVisibility(View.GONE);
                                progressdialog.dismiss();

                            }

                        }catch (Exception e) {
                            Log.d("test", ""+e);

                        }
                    }
                    @Override
                    public void onFailure(Call<Verify> call, Throwable t) {
                        Log.d("test", ""+t);

                    }
                });
            }

        } //End for loop
    }

    void dialogOnEnabled(){
        new AlertDialog.Builder(getContext())
                .setTitle("Enabled")
                .setMessage("Quantum points reloaded!")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.cancel();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    void dialogOnEnableInfinte(){
        new AlertDialog.Builder(getContext())
                .setTitle("Enable Infinte")
                .setMessage("You have bought the infinte package before, please enable that instead!")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.cancel();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Acknowledge purchase and grant the item to the user
            if ("infinte_points".equals(currentSKU)) {
                limitvoids = Integer.MAX_VALUE;
                limitanomalies = Integer.MAX_VALUE;
                limitattractors = Integer.MAX_VALUE;
                saveDataPremium();

            }
            else if ("get_more_points".equals(currentSKU)) {
                limitattractors = limitattractors+60;
                limitanomalies = limitattractors+60;
                limitvoids = limitattractors+60;
                AddData(purchase.getPurchaseToken(), 60, purchase);

            }
            else if ("get_points".equals(currentSKU)) {
                limitattractors = limitattractors+20;
                limitanomalies = limitattractors+20;
                limitvoids = limitattractors+20;
                AddData(purchase.getPurchaseToken(), 20, purchase);

            }
            else if ("skip_water_points".equals(currentSKU)) {
                waterpoints = 1;
                saveDataPremium();
            }
            else if ("extend_radius".equals(currentSKU)) {
                extendradius = 1;
                saveDataPremium();
            }

            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            MainActivity.toggle.setDrawerIndicatorEnabled(true);
            MainActivity. toggle = new ActionBarDrawerToggle(getActivity(), MainActivity.drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            MainActivity.drawer.addDrawerListener(MainActivity.toggle);
            MainActivity.toggle.syncState();
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            androidx.fragment.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();


            String tag = "randonaut";

            Fragment randonautfragment = (Fragment) fragmentManager.findFragmentByTag(tag);

            //Check for existing randonaut fragment
            if (randonautfragment == null) {
                randonautfragment = new RandonautFragment();
            }

            //Add the fragment in stack with the corresponding tag and start the fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, randonautfragment, tag)
                    .addToBackStack(tag)
                    .commit();
            //     getActivity().onBackPressed();

        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            // Here you can confirm to the user that they've started the pending
            // purchase, and to complete it, they should follow instructions that
            // are given to them. You can also choose to remind the user in the
            // future to complete the purchase if you detect that it is still
            // pending.
        }
    }

    public void saveData(Purchase purchase){

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(), Base64.DEFAULT))+"/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RandoWrapperApi randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        final JSONObject obj = new JSONObject();

        try {
            obj.put("packageName",  "com.randonautica.app");
            obj.put("productId", purchase.getSku());
            obj.put("purchaseToken", purchase.getPurchaseToken());
            obj.put("subscription", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());
        final Call<Verify> response = randoWrapperApi.postSavePointsJson(body);

        response.enqueue(new Callback<Verify>() {
            @Override
            public void onResponse(Call<Verify> call, Response<Verify> response) {
                try {

                    Log.d("test", ""+response);
                    saveDataPremium();

                }catch (Exception e) {
                    Log.d("test", ""+e);
                    saveDataPremium();

                }
            }
            @Override
            public void onFailure(Call<Verify> call, Throwable t) {
                Log.d("test", ""+t);
                saveDataPremium();

            }
        });



    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(STATS, Context.MODE_PRIVATE);
        limitattractors = sharedPreferences.getInt("LIMITATTRACTORS", 0);
        limitanomalies = sharedPreferences.getInt("LIMITANOMALY", 0);
        limitvoids = sharedPreferences.getInt("LIMITVOID", 0);
        waterpoints = sharedPreferences.getInt("WATERPOINTS", 0);
        extendradius = sharedPreferences.getInt("EXTENDRADIUS", 0);
        showSyncbutton = sharedPreferences.getInt("SHOWSYNC", 0);
    }

    //Save data to Shared Preferences
    protected void saveDataPremium() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(STATS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("LIMITVOID", limitvoids);
        editor.putInt("LIMITANOMALY", limitanomalies);
        editor.putInt("LIMITATTRACTORS", limitattractors);
        editor.putInt("WATERPOINTS", waterpoints);
        editor.putInt("EXTENDRADIUS", extendradius);
        editor.putInt("SHOWSYNC", showSyncbutton);

        editor.apply();
    }

    //Write to Database
    //#TODO: FIX THE GID/TID/LID
    public void AddData(String purchaseToken, int points, Purchase purchase) {
        boolean insertData = mDatabaseHelper.addDataPoints("Points", purchaseToken, points);
        saveData(purchase);
    }

    //Write to Database
    //#TODO: FIX THE GID/TID/LID
    public void AddDataValidate(String purchaseToken, int points) {
        boolean insertData = mDatabaseHelper.addDataPoints("Points", purchaseToken, points);
    }

}

