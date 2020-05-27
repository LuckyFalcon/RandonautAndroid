package com.randonautica.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class MyUpgradeFragment extends Fragment implements PurchasesUpdatedListener {

    private BillingClient billingClient;

    private static int limitanomalies;
    private static int limitattractors;
    private static int limitvoids;
    private static int waterpoints;
    private static int extendradius;

    SkuDetails skuDetailsInfinte = null;
    SkuDetails skuDetails60 = null;
    SkuDetails skuDetails20 = null;
    SkuDetails skuDetailsWater = null;
    SkuDetails skuDetailsGetPoints = null;
    String currentSKU = null;

    androidx.appcompat.widget.Toolbar toolbar;

    //Storing information globally
    public static final String STATS = "stats";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Set title
        getActivity().setTitle("Upgrade");

        loadData();

        return inflater.inflate(R.layout.fragment_upgrade, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (androidx.appcompat.widget.Toolbar) getActivity().findViewById(R.id.toolbar);

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
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
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
                                        }
                                        else if ("get_more_points".equals(sku)) {
                                            textViewget60PointsPrice.setText(price);
                                            skuDetails60 = skuDetails;

                                        }
                                        else if ("get_points".equals(sku)) {
                                            textViewget20PointsPrice.setText(price);
                                            skuDetails20 = skuDetails;

                                        }
                                        else if ("skip_water_points".equals(sku)) {
                                            textViewWaterPoints.setText("Skip water points");
                                            textViewWaterPointsDesc.setText("Skips Points generated in water");
                                            textViewWaterPointsPrice.setText(price);
                                            skuDetailsWater = skuDetails;

                                        }
                                        else if ("extend_radius".equals(sku)) {
                                            textViewExtendRadius.setText("Extend radius to 20000 meters");
                                            textViewExtendRadiusDesc.setText(skuDesc);
                                            textViewExtendRadiusPrice.setText(price);
                                            skuDetailsGetPoints = skuDetails;

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
                Log.d("test", "test");

            }
        });

        Button Buttoninfinte = (Button) view.findViewById(R.id.buttongetUnlimitedPoints);
        Button Button20 = (Button) view.findViewById(R.id.buttonGet20Points);
        Button Button60 = (Button) view.findViewById(R.id.buttonGet60Points);
        Button ButtonWaterPoints = (Button) view.findViewById(R.id.buttonWaterPoints);
        Button buttonExtendRadius = (Button) view.findViewById(R.id.buttonExtendRadius);

        if(limitattractors == Integer.MAX_VALUE){
            Buttoninfinte.setText("Enabled");
            Buttoninfinte.setEnabled(false);
            Button60.setText("Enabled");
            Button60.setEnabled(false);
            Button20.setText("Enabled");
            Button20.setEnabled(false);
        }

        if(extendradius == 1){
            buttonExtendRadius.setText("Enabled");
            buttonExtendRadius.setEnabled(false);
        }

        if(waterpoints == 1){
            ButtonWaterPoints.setText("Enabled");
            ButtonWaterPoints.setEnabled(false);
        }


        Buttoninfinte.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                test2(skuDetailsInfinte);
            }
        });

        Button60.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                test2(skuDetails60);
            }
        });

        Button20.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                test2(skuDetails20);
            }
        });

        ButtonWaterPoints.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                test2(skuDetailsWater);

            }
        });

        buttonExtendRadius.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                test2(skuDetailsGetPoints);

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

       // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                MainActivity.toggle.setDrawerIndicatorEnabled(true);
//                MainActivity. toggle = new ActionBarDrawerToggle(getActivity(), MainActivity.drawer, toolbar,
//                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//                MainActivity.drawer.addDrawerListener(MainActivity.toggle);
//                MainActivity.toggle.syncState();
//                toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
//                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                androidx.fragment.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//
//
//                String tag = "randonaut";
//
//                Fragment randonautfragment = (Fragment) fragmentManager.findFragmentByTag(tag);
//
//                //Check for existing randonaut fragment
//                if (randonautfragment == null) {
//                    randonautfragment = new RandonautFragment();
//                }
//
//                //Add the fragment in stack with the corresponding tag and start the fragment
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, randonautfragment, tag)
//                        .addToBackStack(tag)
//                        .commit();
//           //     getActivity().onBackPressed();
//
//            }
//        });
    }
    public void test2(SkuDetails skuDetails){
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

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Acknowledge purchase and grant the item to the user
            if ("infinte_points".equals(currentSKU)) {
                limitvoids = Integer.MAX_VALUE;
                limitanomalies = Integer.MAX_VALUE;
                limitattractors = Integer.MAX_VALUE;
                saveDataPremium();
            }
            else if ("get_points_more".equals(currentSKU)) {
                limitattractors = limitattractors+60;
                limitanomalies = limitattractors+60;
                limitvoids = limitattractors+60;
                saveDataPremium();

            }
            else if ("get_points".equals(currentSKU)) {
                limitattractors = limitattractors+20;
                limitanomalies = limitattractors+20;
                limitvoids = limitattractors+20;
                saveDataPremium();
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


    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(STATS, Context.MODE_PRIVATE);
        limitattractors = sharedPreferences.getInt("LIMITATTRACTORS", 0);
        limitanomalies = sharedPreferences.getInt("LIMITANOMALY", 0);
        limitvoids = sharedPreferences.getInt("LIMITVOID", 0);
        waterpoints = sharedPreferences.getInt("WATERPOINTS", 0);
        extendradius = sharedPreferences.getInt("EXTENDRADIUS", 0);

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

        editor.apply();
    }
}
