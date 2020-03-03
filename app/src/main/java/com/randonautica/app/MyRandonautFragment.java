package com.randonautica.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.randonautica.app.Attractors.GenerateAttractors;
import com.randonautica.app.Attractors.GenerateEntropy;
import com.randonautica.app.Attractors.GenerateRecyclerView;
import com.randonautica.app.Classes.SingleRecyclerViewLocation;
import com.randonautica.app.Interfaces.API_Classes.SendEntropy;
import com.randonautica.app.Interfaces.MainActivityMessage;
import com.randonautica.app.Interfaces.RandoWrapperApi;
import com.randonautica.app.Interfaces.RandonautAttractorListener;
import com.randonautica.app.Interfaces.RandonautEntropyListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static com.randonautica.app.Attractors.GenerateAttractors.locationList;

public class MyRandonautFragment extends Fragment implements LifecycleOwner, OnMapReadyCallback, PermissionsListener  {

    //Load keys
    static {
        System.loadLibrary("keys");
    }

    //Native Modules
    protected native String getApiKey();
    public static native String getBaseApi();

    //Load Functions
    GenerateEntropy generateEntropy = new GenerateEntropy();
    GenerateAttractors generateAttractors = new GenerateAttractors();
    GenerateRecyclerView generateRecyclerView = new GenerateRecyclerView();

    //Fragment View
    private View rootview;

    //Fragment related
    private PermissionsManager permissionsManager;

    //Mapbox related
    private MapView mapView;
    public  MapboxMap mapboxMap;
    String style = Style.MAPBOX_STREETS;

    //Storing information globally
    public static final String STATS = "stats";

    //Buttons
    public static Button startButton;
    public static Button reportButton;
    public static Button resetButton;

    //Loaded Data
    public static long voids;
    public static long atts;
    public static long psuedo;
    public static long anomalies;
    public static long entropy;

    //Message to mainactivity
    MainActivityMessage SM;

    //Preferences Dialog
    //Preferences Dialog variables
    Dialog preferencesDialog;
    Dialog explanationDialog;
    Dialog rngExplanationDialog;
    Dialog setIntentionDialog;
    private int distance;
    private TextView textViewProgress;
    private SeekBar seekBarProgress;

    //Preferences Dialog Toggle buttons
    ToggleButton AttractorToggleButton;
    ToggleButton VoidToggleButton;
    ToggleButton AnomalyToggleButton;
    ToggleButton PsuedoToggleButton;

    //RNG Dialog Toggle Buttons
    ToggleButton QuantumToggleButton;
    ToggleButton PoolToggleButton;
    ToggleButton GCPToggleButton;
    ToggleButton CameraToggleButton;

    //Selected Option
    private String selected;

    /** create view */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (rootview == null) {
            createMapInstance();
            rootview = inflater.inflate(R.layout.fragment_randonaut, container, false);
            mapView = (MapView) rootview.findViewById(R.id.mapView);
            mapView.getMapAsync(this);
            mapView.onCreate(savedInstanceState);
        }

        getActivity().setTitle("Randonaut");

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                style = Style.DARK;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                style = Style.MAPBOX_STREETS;
                break;
        }


        loadData();
        return rootview;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MySettingsFragment.SHARED_PREFS, Context.MODE_PRIVATE);
        startButton = (Button) view.findViewById(R.id.startButton);
        resetButton = (Button) view.findViewById(R.id.resetRandonaut);
        reportButton = (Button) view.findViewById(R.id.reportButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {

                if(mapboxMap.getLocationComponent().isLocationComponentActivated() == false){
                    enableLocationComponent(mapboxMap.getStyle());
                } else {
                    setPreferencesAlertDialog();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRandonaut();
                //  navigateButton.setVisibility(View.GONE);
                resetButton.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                reportButton.setVisibility(View.GONE);
            }
        });



    }

    /** set map view */
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        MyRandonautFragment.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri(style),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });
    }

    protected void createMapInstance(){

        Mapbox.getInstance(getContext(), new String(Base64.decode(getApiKey(),Base64.DEFAULT)));

    }


    public void setPreferencesAlertDialog(){
        preferencesDialog = new Dialog(getContext());
        preferencesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        preferencesDialog.setContentView(R.layout.dialog_preferences);

        distance = 3000;

        //Buttons
        Button start = (Button) preferencesDialog.findViewById(R.id.preferencesDialogStartButton);
        Button cancel = (Button) preferencesDialog.findViewById(R.id.preferencesDialogCancelButton);
        ImageView helpImagButton = (ImageView) preferencesDialog.findViewById(R.id.imageHelpPreferencesButton);

        //TextView
        textViewProgress = (TextView) preferencesDialog.findViewById(R.id.textViewProgress);
        textViewProgress.setText("" + 30*100);

        //SeekBar
        seekBarProgress = (SeekBar) preferencesDialog.findViewById(R.id.seekBarProgress);

        //Toggle Buttons
        AttractorToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.AttractorToggleButton);
        VoidToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.VoidToggleButton);
        AnomalyToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.AnomalyToggleButton);
        PsuedoToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.PsuedoToggleButton);

        //Set Attractor as default button
        AttractorToggleButton.setChecked(true);
        selected = "Attractor";

        //Check for click
        AttractorToggleButton.setOnCheckedChangeListener(changeChecker);
        VoidToggleButton.setOnCheckedChangeListener(changeChecker);
        AnomalyToggleButton.setOnCheckedChangeListener(changeChecker);
        PsuedoToggleButton.setOnCheckedChangeListener(changeChecker);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PsuedoToggleButton.isChecked()) {
                    preferencesDialog.cancel();
                    generateAttractors.getPsuedo(rootview, mapboxMap, getContext(), distance, selected, new RandonautAttractorListener() {
                        @Override
                        public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                            saveData();
                            generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mapboxMap);
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
                } else {
                    preferencesDialog.cancel();
                    setRngPreferencesDialog();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();

            }

        });

        helpImagButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();
                setExplanationDialog();
            }
        });

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance = (1000+(progress*100));
                textViewProgress.setText("" + distance);
                if(progress == 0){
                    distance = 1000;
                    textViewProgress.setText("" + 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        preferencesDialog.show();

    }

    public void setExplanationDialog(){
        explanationDialog = new Dialog(getActivity());
        explanationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        explanationDialog.setContentView(R.layout.dialog_explanation);

        int  textColor = getResources().getColor(R.color.navSelected);

        String text = "<font color="+textColor+">These are ways the algorithm reads the quantumly randomized information to generate a point<br></br> for you to travel to.</font>";

        TextView textViewExplanationTop = (TextView) explanationDialog.findViewById(R.id.textViewExplanationTop);
        textViewExplanationTop.setText(Html.fromHtml(text));

        ImageView imageExplanationCloseButton = (ImageView) explanationDialog.findViewById(R.id.imageExplanationCloseButton);

        imageExplanationCloseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                explanationDialog.cancel();
                setPreferencesAlertDialog();
            }
        });

        explanationDialog.show();

    }

    public void setRNGExplanationDialog(){
        rngExplanationDialog = new Dialog(getActivity());
        rngExplanationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rngExplanationDialog.setContentView(R.layout.dialog_rngexplanation);

        int  textColor = getResources().getColor(R.color.navSelected);

        String ANU = "<font color="+textColor+"><b>ANU: </b>Australia National University's quantum random number generator</font>";
        String ANULeftovers = "<font color="+textColor+"><b>ANU Leftovers: </b>Mixed intentions from unused ANU entropy</font>";
        String CameraRNG = "<font color="+textColor+"><b>Camera: </b>Generates entropy from your camera (best try keeping the camera on a still surface - although the jury is still out on that!)</font>";

        TextView ANUExplanation = (TextView) rngExplanationDialog.findViewById(R.id.textViewANUExplanation);
        TextView ANULeftoversExplanation = (TextView) rngExplanationDialog.findViewById(R.id.textViewANULeftoversExplanation);
        TextView textViewCameraRNGExplanation = (TextView) rngExplanationDialog.findViewById(R.id.textViewCameraRNGExplanation);

        ANUExplanation.setText(Html.fromHtml(ANU));
        ANULeftoversExplanation.setText(Html.fromHtml(ANULeftovers));
        textViewCameraRNGExplanation.setText(Html.fromHtml(CameraRNG));

        ImageView imageRNGExplanationCloseButton = (ImageView) rngExplanationDialog.findViewById(R.id.imageRNGExplanationCloseButton);

        imageRNGExplanationCloseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                rngExplanationDialog.cancel();
                setRngPreferencesDialog();
            }
        });

        rngExplanationDialog.show();

    }

    public Dialog setRngPreferencesDialog(){
        preferencesDialog = new Dialog(getActivity());
        preferencesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        preferencesDialog.setContentView(R.layout.dialog_rngpreferences);

        //Buttons
        Button start = (Button) preferencesDialog.findViewById(R.id.RNGpreferencesDialogStartButton);
        Button previous = (Button) preferencesDialog.findViewById(R.id.RNGpreferencesDialogPreviousButton);
        ImageView rngHelpImage = (ImageView) preferencesDialog.findViewById(R.id.imageRNGHelpButton);

        //Toggle Buttons
        QuantumToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.AnuToggleButton);
        PoolToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.PoolToggleButton);
        GCPToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.gcpToggleButton);
        CameraToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.cmrngToggleButton);

        // Check if we're running on Android 5.0 or higher and enable the Camera RNG button
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CameraToggleButton.setEnabled(false);
        }

        preferencesDialog.show();

        //Set Attractor as default button
        QuantumToggleButton.setChecked(true);

        //Check for click
        QuantumToggleButton.setOnCheckedChangeListener(RNGchangeChecker);
        PoolToggleButton.setOnCheckedChangeListener(RNGchangeChecker);
        GCPToggleButton.setOnCheckedChangeListener(RNGchangeChecker);
        CameraToggleButton.setOnCheckedChangeListener(RNGchangeChecker);


        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();
                seIntentionDialog();
            }

        });

        previous.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();
                setPreferencesAlertDialog();

            }

        });

        rngHelpImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();
                setRNGExplanationDialog();
            }
        });


        return preferencesDialog;
    }

    public void seIntentionDialog(){
        setIntentionDialog = new Dialog(getActivity());
        setIntentionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setIntentionDialog.setContentView(R.layout.dialog_setinention);

        String setIntentTextTop = "Before you embark on your adventure, take a moment to set an intention for your trip. This can be a word or phrase to help create the story for the journey ahead.";
        String setIntentTextBot = "Use a clear mind, focused thought and visualization. When you're done, hit <b>START</b> and the QRNG will quantumly generate your destination.";

        TextView textViewSetIntentTextTop = (TextView) setIntentionDialog.findViewById(R.id.textViewSetIntentTextTop);
        TextView textViewSetIntentTextBot = (TextView) setIntentionDialog.findViewById(R.id.textViewSetIntentTextBot);

        textViewSetIntentTextTop.setText(Html.fromHtml(setIntentTextTop));
        textViewSetIntentTextBot.setText(Html.fromHtml(setIntentTextBot));

        //Buttons
        Button start = (Button) setIntentionDialog.findViewById(R.id.setintentionDialogStartButton);
        Button cancel = (Button) setIntentionDialog.findViewById(R.id.setintentionDialogCancelButton);

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                if (QuantumToggleButton.isChecked()) {
                    setIntentionDialog.cancel();
                    generateEntropy.getANUQuantumEntropy(getContext(), distance,
                            new RandonautEntropyListener() {
                                @Override
                                public void onData(String GID) {
                                    saveData();
                                    generateAttractors.getAttractors(rootview, mapboxMap, getContext(), GID,false, false, selected, distance, new RandonautAttractorListener() {
                                        @Override
                                        public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                                            saveData();
                                            // generateRecyclerView.initRecyclerView(attractorLocationList);
                                            generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mapboxMap);

                                        }

                                        @Override
                                        public void onFailed() {

                                        }
                                    });

                                }

                                @Override
                                public void onFailed() {
                                    Log.d("work", "2");
                                }
                            });

                } else if (PoolToggleButton.isChecked()) {
                    setIntentionDialog.cancel();
                    generateEntropy.poolQuantumEntropy(getContext(), distance,
                            new RandonautEntropyListener() {
                                @Override
                                public void onData(String GID) {
                                    saveData();
                                    generateAttractors.getAttractors(rootview, mapboxMap, getContext(), GID,true, false, selected, distance, new RandonautAttractorListener() {
                                        @Override
                                        public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                                            saveData();
                                            generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mapboxMap);

                                        }

                                        @Override
                                        public void onFailed() {

                                        }
                                    });

                                }

                                @Override
                                public void onFailed() {
                                    Log.d("work", "2");
                                }
                            });
                } else if (CameraToggleButton.isChecked()) {
                    setIntentionDialog.cancel();
                    generateEntropy.getNeededEntropySize(getContext(), distance,
                            new RandonautEntropyListener() {
                                @Override
                                public void onData(String entropySizeNeeded) {
                                    Log.d("work", "w");
                                    SM.rng(Integer.parseInt(entropySizeNeeded));

                                }
                                @Override
                                public void onFailed() {

                                }
                            });//---> This will run the MyCamRngFragment on success
                } else if (GCPToggleButton.isChecked()) {
                    setIntentionDialog.cancel();
                    generateEntropy.getGCPEntropy(getContext(), distance,
                            new RandonautEntropyListener() {
                                @Override
                                public void onData(String GID) {
                                    generateAttractors.getAttractors(rootview, mapboxMap, getContext(), GID,false, false, selected, distance, new RandonautAttractorListener() {
                                        @Override
                                        public void onData(ArrayList<SingleRecyclerViewLocation> GID) {
                                            //initRecyclerView(GID);

                                        }

                                        @Override
                                        public void onFailed() {

                                        }
                                    });

                                }
                                @Override
                                public void onFailed() {

                                }
                            });
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setIntentionDialog.cancel();

            }

        });


        setIntentionDialog.show();


    }

    /** reset instance */
    public void resetRandonaut() {
        //Empty previous run
        locationList = new ArrayList<>();
        mapboxMap.clear();
        generateRecyclerView.removeRecyclerView(rootview);
    }

    /**
     * Compound buttons listeners
     */

    CompoundButton.OnCheckedChangeListener changeChecker = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                if (buttonView == AttractorToggleButton) {
                    selected = "Attractor";
                    VoidToggleButton.setChecked(false);
                    PsuedoToggleButton.setChecked(false);
                    AnomalyToggleButton.setChecked(false);
                }
                if (buttonView == VoidToggleButton) {
                    selected = "Void";
                    AttractorToggleButton.setChecked(false);
                    PsuedoToggleButton.setChecked(false);
                    AnomalyToggleButton.setChecked(false);
                }
                if (buttonView == PsuedoToggleButton) {
                    selected = "Pseudo";
                    VoidToggleButton.setChecked(false);
                    AttractorToggleButton.setChecked(false);
                    AnomalyToggleButton.setChecked(false);
                }
                if (buttonView == AnomalyToggleButton) {
                    selected = "Anomalie";
                    VoidToggleButton.setChecked(false);
                    AttractorToggleButton.setChecked(false);
                    PsuedoToggleButton.setChecked(false);
                }
            }
        }
    };

    CompoundButton.OnCheckedChangeListener RNGchangeChecker = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                if (buttonView == QuantumToggleButton) {
                    PoolToggleButton.setChecked(false);
                    CameraToggleButton.setChecked(false);
                    GCPToggleButton.setChecked(false);
                }
                if (buttonView == PoolToggleButton) {
                    QuantumToggleButton.setChecked(false);
                    CameraToggleButton.setChecked(false);
                    GCPToggleButton.setChecked(false);
                }
                if (buttonView == CameraToggleButton) {
                    QuantumToggleButton.setChecked(false);
                    PoolToggleButton.setChecked(false);
                    GCPToggleButton.setChecked(false);
                }
                if (buttonView == GCPToggleButton) {
                    QuantumToggleButton.setChecked(false);
                    PoolToggleButton.setChecked(false);
                    CameraToggleButton.setChecked(false);
                }
            }
        }
    };

    public void setQuantumEntropy(int size, String Entropy){

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

        RandoWrapperApi randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<SendEntropy.Response> response = randoWrapperApi.postEntropyJson(String.valueOf(size), Entropy);

        response.enqueue(new Callback<SendEntropy.Response>() {
            @Override
            public void onResponse(Call<SendEntropy.Response> call, Response<SendEntropy.Response> response) {
                Log.d("test", ""+response.body().getGid());
                generateAttractors.getAttractors(rootview, mapboxMap, getContext(), response.body().getGid(),false, false, selected, distance, new RandonautAttractorListener() {
                    @Override
                    public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                        saveData();
                        generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mapboxMap);

                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }
            @Override
            public void onFailure(Call<SendEntropy.Response> call, Throwable t) {
                generateEntropy.onCreateDialogErrorGettingEntropy(getContext());
            }
        });
    }

    /** set attractor from my attractors */
    //From profile attractors
    public void onShowProfileAttractors(int type, double power, double x, double y, double radiusm, double z_score, double pseudo){

        if(type == 1){
            mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(x, y))
                    .title("Attractor"));
        } else {
            mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(x, y))
                    .title("Void"));
        }

        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
        singleLocation.setType((type));
        singleLocation.setRadiusm((radiusm));
        singleLocation.setPower((power));
        singleLocation.setZ_score((z_score));
        singleLocation.setLocationCoordinates(new LatLng(x, y));
        if(pseudo == 1){
            singleLocation.setPsuedo(true);
        } else {
            singleLocation.setPsuedo(false);
        }

        //Set circle of radius
        mapboxMap.addPolygon(generateAttractors.generatePerimeter(
                new LatLng(x, y),
                (radiusm/1000),
                64));

        locationList.add(singleLocation);
        Log.d("test", ""+requireContext());
      //  (Context) activity
        generateRecyclerView.initRecyclerView(locationList, rootview, mapboxMap);


        MyRandonautFragment.startButton.setVisibility(View.GONE);
        MyRandonautFragment.resetButton.setVisibility(View.VISIBLE);
    }

    /**
     * Saving and loading shared preferences
     */
    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(STATS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("ATTRACTORS", atts);
        editor.putLong("VOID", voids);
        editor.putLong("ANOMALIES", anomalies);
        editor.putLong("PSEUDO", psuedo);
        editor.putLong("ENTROPY", entropy);

        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(STATS, Context.MODE_PRIVATE);
        atts = sharedPreferences.getLong("ATTRACTORS", 0);
        anomalies = sharedPreferences.getLong("ANOMALIES", 0);
        voids = sharedPreferences.getLong("VOID", 0);
        entropy = sharedPreferences.getLong("ENTROPY", 0);
        psuedo = sharedPreferences.getLong("PSEUDO", 0);
    }

    /**
     * Permissions for setting the location
     */
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            getActivity().finish();
        }
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            //Check if GPS is disabled
            if(loadedMapStyle == null){
                Toast.makeText(getContext(), "Map was not loaded", Toast.LENGTH_LONG).show();
            } else {
                //Check if GPS is enabled
                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getContext(), "GPS is disabled!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "GPS is enabled!", Toast.LENGTH_LONG).show();

                    LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(getContext())
                            .accuracyAnimationEnabled(true)
                            .build();

                    LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                            .builder(getContext(), loadedMapStyle)
                            .locationComponentOptions(locationComponentOptions)
                            .build();

                    // Get an instance of the component
                    final LocationComponent locationComponent = mapboxMap.getLocationComponent();

                    // Activate with options
                    locationComponent.activateLocationComponent(locationComponentActivationOptions);
                    // locationComponent.activateLocationComponent(
                    //       LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());

                    // Enable to make component visible
                    locationComponent.setLocationComponentEnabled(true);


                    // Set the component's camera mode
                    locationComponent.setCameraMode(CameraMode.TRACKING_GPS);

                    // Set the component's render mode
                    locationComponent.setRenderMode(RenderMode.COMPASS);

                    if (locationComponent.getLastKnownLocation() != null) {
                        // locationComponent.getLastKnownLocation();
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(locationComponent.getLastKnownLocation()))
                                .zoom(13)
                                .build();
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000);

                    }
                }

            }



        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    /**
     * Attach to the mainactivity, used for camrng
     * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            SM = (MainActivityMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    /**
     *  Contains all the mapbox functions
     */

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
