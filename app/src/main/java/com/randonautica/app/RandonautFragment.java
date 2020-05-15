package com.randonautica.app;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.randonautica.app.Attractors.GenerateAttractors.locationList;

public class RandonautFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;

    //Load keys
    static {
        System.loadLibrary("keys");
        System.loadLibrary("Steve");
    }

    //Native Modules
//    protected native String getApiKey();
    public static native String getBaseApi();
//    public static native String hitBooks(int size);

    public static Circle lastUserCircle;
    public static long pulseDuration = 10000;
    public static ValueAnimator lastPulseAnimator;

    //Load Functions
    GenerateEntropy generateEntropy = new GenerateEntropy();
    GenerateAttractors generateAttractors = new GenerateAttractors();
    GenerateRecyclerView generateRecyclerView = new GenerateRecyclerView();

    //Fragment View
    private View rootview;

    //Google style related
    String style = "Normal";

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

    //Preferences Dialogs
    Dialog preferencesDialog;
    Dialog setIntentionDialog;
    Dialog setTemporalDialog;

    //Preferences Explanation Dialogs
    Dialog explanationDialog;
    Dialog rngExplanationDialog;
    Dialog temporalExplanationDialog;

    //Preference Variables
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
    ToggleButton TemporalToggleButton;
    ToggleButton CameraToggleButton;

    //Temporal Dialog Toggle Buttons
    ToggleButton temporalInternetToggleButton;
    ToggleButton temporalLocalToggleButton;

    //Selected Option
    private String selected;

    //Initialize ProgressDialog
    ProgressDialog progressdialog;

    private Boolean mLocationPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 3333;
    private static final float DEFAULT_ZOOM = 15f;


    private FusedLocationProviderClient mFusedLocationProviderClient;

    SupportMapFragment mapFragment;

    public RandonautFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_randonautica, container, false);
            mapFragment =
                    (SupportMapFragment)
                            getChildFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);

        }

        getActivity().setTitle("Randonaut");


        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                style = "Dark";
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                style = "Normal";
                break;
        }

        getLocationPermission();
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
                if (mFusedLocationProviderClient != null) {
                    setPreferencesAlertDialog();
                } else {

                    if (mLocationPermissionsGranted) {
                        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        boolean gps_enabled = false;
                        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if (gps_enabled) {
                            getDeviceLocation();

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        } else {
                            Toast.makeText(getContext(), "GPS is disabled!", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(getContext(), "Permissions not accepted!", Toast.LENGTH_SHORT).show();

                    }
                }


//                if(mapboxMap.getLocationComponent().isLocationComponentActivated() == false){
//                    //enableLocationComponent(mapboxMap.getStyle());
//                } else {
//                    setPreferencesAlertDialog();
//                }
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

    /**
     * reset instance
     */
    public void resetRandonaut() {
        //Empty previous run

        if (lastUserCircle != null) {
            lastUserCircle.remove();
            lastUserCircle = null;
            lastPulseAnimator.cancel();
            if (lastUserCircle != null) {

            }
        }
        locationList = new ArrayList<>();
        if (mMap != null) {
            mMap.clear();
        }
        if (generateRecyclerView != null) {
            try {
                generateRecyclerView.removeRecyclerView(rootview);
            } catch (Exception e) {
                //  Block of code to handle errors
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;

                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps_enabled) {
                getDeviceLocation();

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);


            } else {
                Toast.makeText(getContext(), "GPS is disabled!", Toast.LENGTH_SHORT).show();
            }


        }

        if (style == "Dark") {
            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));
        }


    }

    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            Location currentLocation = (Location) task.getResult();
                            if(currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            }

                        } else {
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity() != null) {


            if (ContextCompat.checkSelfPermission(this.getContext(),
                    FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getContext(),
                        COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = true;

                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            mLocationPermissionsGranted = true;
        }
    }

    public void setPreferencesAlertDialog() {
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
        textViewProgress.setText("" + 30 * 100);

        //SeekBar
        seekBarProgress = (SeekBar) preferencesDialog.findViewById(R.id.seekBarProgress);

        //Toggle Buttons
        AttractorToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.AttractorToggleButton);
        VoidToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.VoidToggleButton);
        AnomalyToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.AnomalyToggleButton);
        PsuedoToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.PsuedoToggleButton);

        //Set Attractor as default button
        AnomalyToggleButton.setChecked(true);
        selected = "Anomalie";

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
                    generateAttractors.getPsuedo(rootview, mMap, getContext(), distance, selected, mFusedLocationProviderClient, new RandonautAttractorListener() {
                        @Override
                        public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                            saveData();
                            generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mMap);
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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();

            }

        });

        helpImagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();
                setExplanationDialog();
            }
        });

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance = (1000 + (progress * 100));
                textViewProgress.setText("" + distance);
                if (progress == 0) {
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

    public Dialog setRngPreferencesDialog() {
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
        // GCPToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.gcpToggleButton);
        TemporalToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.tmprngToggleButton);
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
        TemporalToggleButton.setOnCheckedChangeListener(RNGchangeChecker);
        CameraToggleButton.setOnCheckedChangeListener(RNGchangeChecker);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TemporalToggleButton.isChecked()) {
                    preferencesDialog.cancel();
                    setTemporalDialog();
                } else {
                    preferencesDialog.cancel();
                    setIntentionDialog();
                }
            }

        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();
                setPreferencesAlertDialog();

            }

        });

        rngHelpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();
                setRNGExplanationDialog();
            }
        });


        return preferencesDialog;
    }

    public void setIntentionDialog() {
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

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (QuantumToggleButton.isChecked()) {
                    setIntentionDialog.cancel();
                    generateEntropy.getANUQuantumEntropy(getContext(), distance,
                            new RandonautEntropyListener() {
                                @Override
                                public void onData(String GID) {
                                    saveData();
                                    generateAttractors.getAttractors(rootview, mMap, getContext(), GID, false, false, false, selected, distance, mFusedLocationProviderClient, new RandonautAttractorListener() {
                                        @Override
                                        public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                                            saveData();
                                            generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mMap);

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

                } else if (PoolToggleButton.isChecked()) {
                    setIntentionDialog.cancel();
                    generateEntropy.poolQuantumEntropy(getContext(), distance,
                            new RandonautEntropyListener() {
                                @Override
                                public void onData(String GID) {
                                    saveData();
                                    generateAttractors.getAttractors(rootview, mMap, getContext(), GID, true, false, false, selected, distance, mFusedLocationProviderClient, new RandonautAttractorListener() {
                                        @Override
                                        public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                                            saveData();
                                            generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mMap);

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
                } else if (CameraToggleButton.isChecked()) {
                    setIntentionDialog.cancel();
                    generateEntropy.getNeededEntropySize(getContext(), distance,
                            new RandonautEntropyListener() {
                                @Override
                                public void onData(String entropySizeNeeded) {
                                    SM.rng(Integer.parseInt(entropySizeNeeded));
                                }

                                @Override
                                public void onFailed() {

                                }
                            });//---> This will run the MyCamRngFragment on success
                } else if (TemporalToggleButton.isChecked()) {
                    setIntentionDialog.cancel();
                    if (temporalInternetToggleButton.isChecked()) {
                        generateEntropy.getTemporalEntropy(getContext(), distance,
                                new RandonautEntropyListener() {
                                    @Override
                                    public void onData(String GID) {
                                        generateAttractors.getAttractors(rootview, mMap, getContext(), GID, false, true, false, selected, distance, mFusedLocationProviderClient, new RandonautAttractorListener() {
                                            @Override
                                            public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                                                saveData();
                                                generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mMap);

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
                    } //Temporal from Internet
//                    if (temporalLocalToggleButton.isChecked()) {
//                        generateEntropy.getNeededEntropySize(getContext(), distance,
//                                new RandonautEntropyListener() {
//                                    @Override
//                                    public void onData(String entropySizeNeeded) {
//                                        //Upload Entropy and Generate Attractors in Background Task
//                                        RandonautFragment.generatingTemporalEntropyAsync asyncTask = new RandonautFragment.generatingTemporalEntropyAsync();
//                                        asyncTask.execute(entropySizeNeeded);
//
//                                    }
//                                    @Override
//                                    public void onFailed() {
//
//                                    }
//                                });
//                    } //Temporal from Local generation
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntentionDialog.cancel();

            }

        });


        setIntentionDialog.show();


    }

    public void setExplanationDialog() {
        explanationDialog = new Dialog(getActivity());
        explanationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        explanationDialog.setContentView(R.layout.dialog_explanation);

        int textColor = getResources().getColor(R.color.navSelected);

        String text = "<font color=" + textColor + ">These are ways the algorithm reads the quantumly randomized information to generate a point<br></br> for you to travel to.</font>";

        TextView textViewExplanationTop = (TextView) explanationDialog.findViewById(R.id.textViewExplanationTop);
        textViewExplanationTop.setText(Html.fromHtml(text));

        ImageView imageExplanationCloseButton = (ImageView) explanationDialog.findViewById(R.id.imageExplanationCloseButton);

        imageExplanationCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explanationDialog.cancel();
                setPreferencesAlertDialog();
            }
        });

        explanationDialog.show();

    }

    public void setRNGExplanationDialog() {
        rngExplanationDialog = new Dialog(getActivity());
        rngExplanationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rngExplanationDialog.setContentView(R.layout.dialog_rngexplanation);

        int textColor = getResources().getColor(R.color.navSelected);

        String ANU = "<font color=" + textColor + "><b>ANU: </b>Australia National University's quantum random number generator</font>";
        String ANULeftovers = "<font color=" + textColor + "><b>ANU Leftovers: </b>Mixed intentions from unused ANU entropy</font>";
        String TemporalRNG = "<font color=" + textColor + "><b>Temporal: </b>Get entropy using the server\'s CPU clock</font>";
        String CameraRNG = "<font color=" + textColor + "><b>Camera: </b>Generates entropy from your camera (best try keeping the camera on a still surface - although the jury is still out on that!)</font>";

        TextView ANUExplanation = (TextView) rngExplanationDialog.findViewById(R.id.textViewANUExplanation);
        TextView ANULeftoversExplanation = (TextView) rngExplanationDialog.findViewById(R.id.textViewANULeftoversExplanation);
        TextView TemporalRNGExplanation = (TextView) rngExplanationDialog.findViewById(R.id.textViewTemporalRNGExplanation);
        TextView CameraRNGExplanation = (TextView) rngExplanationDialog.findViewById(R.id.textViewCameraRNGExplanation);

        ANUExplanation.setText(Html.fromHtml(ANU));
        ANULeftoversExplanation.setText(Html.fromHtml(ANULeftovers));
        TemporalRNGExplanation.setText(Html.fromHtml(TemporalRNG));
        CameraRNGExplanation.setText(Html.fromHtml(CameraRNG));

        ImageView imageRNGExplanationCloseButton = (ImageView) rngExplanationDialog.findViewById(R.id.imageRNGExplanationCloseButton);

        imageRNGExplanationCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rngExplanationDialog.cancel();
                setRngPreferencesDialog();
            }
        });

        rngExplanationDialog.show();

    }

    public void setTemporalExplanationDialog() {
        temporalExplanationDialog = new Dialog(getActivity());
        temporalExplanationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        temporalExplanationDialog.setContentView(R.layout.dialog_temporalexplanation);

        int textColor = getResources().getColor(R.color.navSelected);

        String Internet = "<font color=" + textColor + "><b>Internet: </b>Steve, who is physically connected to the server\'s brain via a quartz crystal clock, will divulge temporal randomness</font>";
        String Local = "<font color=" + textColor + "><b>Local: </b>Steve divulges entropy using the phone's CPU clock</font>";

        TextView internetExplanation = (TextView) temporalExplanationDialog.findViewById(R.id.textViewInternetExplanation);
        TextView localExplanation = (TextView) temporalExplanationDialog.findViewById(R.id.textViewLocalExplanation);

        internetExplanation.setText(Html.fromHtml(Internet));
        localExplanation.setText(Html.fromHtml(Local));

        ImageView imageTemporalExplanationCloseButton = (ImageView) temporalExplanationDialog.findViewById(R.id.imageTemporalExplanationCloseButton);

        imageTemporalExplanationCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temporalExplanationDialog.cancel();
                setTemporalDialog();
            }
        });

        temporalExplanationDialog.show();

    }

    public void setTemporalDialog() {
        final Dialog setTemporalDialog = new Dialog(getActivity());
        setTemporalDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTemporalDialog.setContentView(R.layout.dialog_temporal);

        //Toggle Buttons
        temporalInternetToggleButton = (ToggleButton) setTemporalDialog.findViewById(R.id.temporalInternetToggleButton);
        //  temporalLocalToggleButton = (ToggleButton) setTemporalDialog.findViewById(R.id.temporalLocalToggleButton);

        //Check for click
        temporalInternetToggleButton.setOnCheckedChangeListener(temporalChangeChecker);
        //temporalLocalToggleButton.setOnCheckedChangeListener(temporalChangeChecker);

        //Set Temporal Internet as default button
        temporalInternetToggleButton.setChecked(true);

        //Buttons
        Button next = (Button) setTemporalDialog.findViewById(R.id.temporalDialogNextButton);
        Button previous = (Button) setTemporalDialog.findViewById(R.id.temporalDialogPreviousButton);
        ImageView temporalHelpImage = (ImageView) setTemporalDialog.findViewById(R.id.imageTemporalRNGHelpButton);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTemporalDialog.cancel();
                setIntentionDialog();
            }

        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTemporalDialog.cancel();
                setRngPreferencesDialog();
            }

        });

        temporalHelpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTemporalDialog.cancel();
                setTemporalExplanationDialog();
            }
        });

        setTemporalDialog.show();


    }

    CompoundButton.OnCheckedChangeListener changeChecker = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
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
            if (isChecked) {
                if (buttonView == QuantumToggleButton) {
                    PoolToggleButton.setChecked(false);
                    CameraToggleButton.setChecked(false);
                    TemporalToggleButton.setChecked(false);
                }
                if (buttonView == PoolToggleButton) {
                    QuantumToggleButton.setChecked(false);
                    CameraToggleButton.setChecked(false);
                    TemporalToggleButton.setChecked(false);
                }
                if (buttonView == CameraToggleButton) {
                    QuantumToggleButton.setChecked(false);
                    PoolToggleButton.setChecked(false);
                    TemporalToggleButton.setChecked(false);
                }
                if (buttonView == TemporalToggleButton) {
                    QuantumToggleButton.setChecked(false);
                    PoolToggleButton.setChecked(false);
                    CameraToggleButton.setChecked(false);
                }
            }
        }
    };

    CompoundButton.OnCheckedChangeListener temporalChangeChecker = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (buttonView == temporalInternetToggleButton) {
                    temporalInternetToggleButton.setChecked(true);
//                    temporalLocalToggleButton.setChecked(false);
                }
//                if (buttonView == temporalLocalToggleButton) {
//                    temporalInternetToggleButton.setChecked(false);
//                    temporalLocalToggleButton.setChecked(true);
//                }
            }
        }
    };

    public void setQuantumEntropy(int size, String Entropy, String rngType) {


        //Start ProgressDialog
        progressdialog = new ProgressDialog(this.getContext());
        progressdialog.setMessage("Setting " + rngType + " entropy. Please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new String(Base64.decode(RandonautFragment.getBaseApi(), Base64.DEFAULT)))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RandoWrapperApi randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<SendEntropy.Response> response = randoWrapperApi.postEntropyJson(String.valueOf(size), Entropy);

        response.enqueue(new Callback<SendEntropy.Response>() {
            @Override
            public void onResponse(Call<SendEntropy.Response> call, Response<SendEntropy.Response> response) {
                progressdialog.dismiss();
                generateAttractors.getAttractors(rootview, mMap, getContext(), response.body().getGid(), false, false, false, selected, distance, mFusedLocationProviderClient, new RandonautAttractorListener() {
                    @Override
                    public void onData(ArrayList<SingleRecyclerViewLocation> attractorLocationList) {
                        saveData();
                        generateRecyclerView.initRecyclerView(attractorLocationList, rootview, mMap);

                    }

                    @Override
                    public void onFailed() {
                        generateEntropy.onCreateDialogErrorGettingEntropy(getContext());
                        progressdialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(Call<SendEntropy.Response> call, Throwable t) {
                generateEntropy.onCreateDialogErrorGettingEntropy(getContext());
            }
        });
    }

    /**
     * set attractor from my attractors
     */
    //From profile attractors
    public void onShowProfileAttractors(int type, double power, double x, double y, double radiusm, double z_score, double pseudo) {
        resetRandonaut();

        if (type == 2) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(x, y))
                    .title("Void"));

        } else {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(x, y))
                    .title("Attractor"));

        }

        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
        singleLocation.setType((type));
        singleLocation.setRadiusm((radiusm));
        singleLocation.setPower((power));
        singleLocation.setZ_score((z_score));
        singleLocation.setLocationCoordinates(new LatLng(x, y));
        if (pseudo == 1) {
            singleLocation.setPsuedo(true);
        } else {
            singleLocation.setPsuedo(false);
        }

        locationList.add(singleLocation);
        generateRecyclerView.initRecyclerView(locationList, rootview, mMap);

        Location loc = new Location("dummy");
        loc.setLatitude(x);
        loc.setLongitude(y);
        addPulsatingEffect(new LatLng(x, y), mMap, loc);

        RandonautFragment.startButton.setVisibility(View.GONE);
        if (startButton.getVisibility() == View.VISIBLE) {
            RandonautFragment.startButton.setVisibility(View.GONE);
        }
        RandonautFragment.resetButton.setVisibility(View.VISIBLE);
        if (startButton.getVisibility() == View.GONE) {
            RandonautFragment.resetButton.setVisibility(View.VISIBLE);
        }
    }

    private void addPulsatingEffect(final LatLng userLatlng, final GoogleMap map, Location currentLocation) {
        if (lastPulseAnimator != null) {
            lastPulseAnimator.cancel();
        }
        if (lastUserCircle != null)
            lastUserCircle.setCenter(userLatlng);
        lastPulseAnimator = valueAnimate(getDisplayPulseRadius(20, map), pulseDuration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (lastUserCircle != null)
                    lastUserCircle.setRadius((Float) animation.getAnimatedValue());
                else {
                    lastUserCircle = map.addCircle(new CircleOptions()
                            .center(userLatlng)
                            .radius(getDisplayPulseRadius((Float) animation.getAnimatedValue(), map))
                            .strokeColor(Color.RED));
                    //.fillColor(Color.BLUE));
                    lastUserCircle.setFillColor(adjustAlpha(0x220000FF, 1 - animation.getAnimatedFraction()));


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

    protected float getDisplayPulseRadius(float radius, GoogleMap map) {
        float diff = (map.getMaxZoomLevel() - map.getCameraPosition().zoom);
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

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    private class generatingTemporalEntropyAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Start ProgressDialog generating Temporal
            progressdialog = new ProgressDialog(getContext());
            progressdialog.setMessage("Generating Temporal entropy. Please wait....");
            progressdialog.show();
            progressdialog.setCancelable(false);
            progressdialog.setCanceledOnTouchOutside(false);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String temporalEntropy = "null";
                //UNCOMMENT LATER with updated Temporal lib 15-5-2020
                //String temporalEntropy = hitBooks(Integer.parseInt(strings[0]));
                return temporalEntropy;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressdialog.dismiss();

            //Set Quantum Entropy after background task is done
            if (result != null) {
                setQuantumEntropy(result.length(), result, "Temporal");
            } else {
                generateEntropy.onCreateDialogErrorGettingEntropy(getContext());
            }

        }
    }

    private void setupMapIfNeeded() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mMap == null) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment)
                            getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }

    /**
     * Attach to the mainactivity, used for camrng
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            SM = (MainActivityMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment.onResume();

    }


}