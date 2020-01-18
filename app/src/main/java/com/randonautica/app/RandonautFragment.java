package com.randonautica.app;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;


import com.randonautica.app.Classes.Attractors;
import com.randonautica.app.Classes.DatabaseHelper;
import com.randonautica.app.Classes.Entropy;
import com.randonautica.app.Classes.Pools;
import com.randonautica.app.Classes.Psuedo;
import com.randonautica.app.Classes.RandoWrapperApi;
import com.randonautica.app.Classes.Sizes;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class RandonautFragment extends Fragment implements LifecycleOwner, OnMapReadyCallback, PermissionsListener  {

    //Fragment View
    private  View v;

    //Fragment related
    private PermissionsManager permissionsManager;

    //Mapbox related
    private MapView mapView;
    private MapboxMap mapboxMap;

    //Mapbox storing
    String style = Style.MAPBOX_STREETS;
    private final List<DirectionsRoute> directionsRouteList = new ArrayList<>();
    ArrayList<SingleRecyclerViewLocation> locationList = new ArrayList<>();
    private static final  LatLng[] possibleDestinations = new LatLng[]{};
    private LocationComponent locationComponent;
    private Point directionsOriginPoint;

    //Mapbox Route generation
    private static final String TAG = "RVDirectionsActivity";
    public static final String STATS = "stats";
    private static final String SYMBOL_ICON_ID = "SYMBOL_ICON_ID";
    private static final String PERSON_ICON_ID = "PERSON_ICON_ID";
    private static final String MARKER_SOURCE_ID = "MARKER_SOURCE_ID";
    private static final String PERSON_SOURCE_ID = "PERSON_SOURCE_ID";
    private static final String DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID = "DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final String PERSON_LAYER_ID = "PERSON_LAYER_ID";
    private static final String DASHED_DIRECTIONS_LINE_LAYER_ID = "DASHED_DIRECTIONS_LINE_LAYER_ID";

    private DrawerLayout drawer;

    //RandoWrapperAPI
    private RandoWrapperApi randoWrapperApi;
    private FeatureCollection dashedLineDirectionsFeatureCollection;

    //Buttons
    private Button startButton;
    private Button navigateButton;
    private Button resetButton;


    private String auth_token;
    private TextView textViewProgress;
    private TextView bitTextView;

    private SeekBar seekBarProgress;

    //Preferences Dialog variables
    Dialog preferencesDialog;

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

    //Save data
    JSONObject attractorObj = new JSONObject();
    JSONArray attractorsArray = new JSONArray();

    private long voids;
    private long atts;
    private long psuedo;
    private long anomalies;
    private long entropy;
    private long reports;

    private OutputStream outputStream;

    //Load data
    private boolean waterPointsEnabled;

    //Dialogs
    ProgressDialog progressdialog;
    Dialog reportDialog;

    //Attractor generation
    private int distance;
    private String selected;
    private String GID;
    private String Type;
    private int N;
    private int spot;
    private int hexsize;

    //Database storing
    DatabaseHelper mDatabaseHelper;

    String attractorTable = "Attractors";
    String voidTable = "Voids";
    String anomalyTable = "Anomalies";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (v == null) {
            Mapbox.getInstance(getContext(), "pk.eyJ1IjoiZGF2aWRmYWxjb24iLCJhIjoiY2szbjRzZmd2MTcwNDNkcXhnbTFzbHR0cCJ9.ZgbfsJXtrCFgI0rRJkwUyg");
            v = inflater.inflate(R.layout.fragment_randonaut, container, false);
            mapView = (MapView) v.findViewById(R.id.mapView);
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
        return v;
    }

    /** after view is created - set map view */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MySettingsFragment.SHARED_PREFS, Context.MODE_PRIVATE);
        waterPointsEnabled = sharedPreferences.getBoolean("enableWaterPoints", false);

            startButton = (Button) view.findViewById(R.id.startButton);
           // navigateButton = (Button) view.findViewById(R.id.startNavigation);
            resetButton = (Button) view.findViewById(R.id.resetRandonaut);

            startButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onClick(View v) {
                    setPreferencesAlertDialog();
               //    SM.rng(); Starts camRNG instance fragment


                }
            });

//            navigateButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    setPreferencesAlertDialog();
//
//                }
//
//            });

            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetRandonaut();
                  //  navigateButton.setVisibility(View.GONE);
                    resetButton.setVisibility(View.GONE);
                    startButton.setVisibility(View.VISIBLE);
                }
            });

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        RandonautFragment.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri(style)

                // Set up the source and layer for the direction route LineLayer
                .withSource(new GeoJsonSource(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID))
                .withLayerBelow(
                        new LineLayer(DASHED_DIRECTIONS_LINE_LAYER_ID, DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)
                                .withProperties(
                                        lineWidth(7f),
                                        lineJoin(LINE_JOIN_ROUND),
                                        lineColor(Color.parseColor("#2096F3"))
                                ), PERSON_LAYER_ID), new Style.OnStyleLoaded() {
            @Override
                    public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                    }
        });

    }

    //From profile attractors
    protected void onShowProfileAttractors(int type, double power, double x, double y, double radiusm, double z_score, double pseudo){

        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(x, y))
                .title("Attractor"));
        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
        singleLocation.setType((type));
        singleLocation.setRadiusm((radiusm));
        singleLocation.setPower((power));
        singleLocation.setZ_score((z_score));
        singleLocation.setLocationCoordinates(new LatLng(x, y));
        singleLocation.setPsuedo(true);

        locationList.add(singleLocation);
        initRecyclerView();

        startButton.setVisibility(View.GONE);
       // navigateButton.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.VISIBLE);
    }

    //Generate points functions

    public void getAttractors(boolean pool){

        //Empty previous run
        locationList = new ArrayList<>();
        mapboxMap.clear();
        removeRecyclerView();

        //Start ProgressDialog
        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Looking for " + selected + "s " + "please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        Call<List<Attractors>> callGetAttractors = randoWrapperApi.getAttractors(GID,
                                mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(), distance, pool);

        Log.d("advd", ""+pool);

        callGetAttractors.enqueue(new Callback<List<Attractors>>() {
            @Override
            public void onResponse(Call<List<Attractors>> call, Response<List<Attractors>> response) {

                int i = 0;
                int count = 0;
                int amount = 0;

                for(Attractors attractors: response.body()){
                    count ++;
                }

                Place places[] =new Place[count];


                for(Attractors attractors: response.body()){

                    double x = attractors.getAttractors().getCenter().getLatlng().getPoint().getLatitude();
                    double y = attractors.getAttractors().getCenter().getLatlng().getPoint().getLongitude();
                    int type = attractors.getAttractors().getType();
                    double radiusm = attractors.getAttractors().getRadiusM();
                    double power = attractors.getAttractors().getPower();
                    double z_score = attractors.getAttractors().getZ_score();

                    Log.d("testingall", "value: " + x);
                    Log.d("testingall", "value: " + y);

                    places[i]=new Place(new LatLng(x, y), type, radiusm, power, z_score);

                    if(waterPointsEnabled){
                        LatLng center = new LatLng(x, y);
                        final PointF pixel = mapboxMap.getProjection().toScreenLocation(center);
                        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "water");
                        Log.d("water", "value: " + features);
                        if(!features.isEmpty()){
                            continue;
                        }
                    }
                    if(selected == "Attractor" && type == 1){
                        mDatabaseHelper = new DatabaseHelper(getActivity(), attractorTable);

                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(x, y))
                                .title("Attractor"));
                        amount++;
                        atts++;
                        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
                        singleLocation.setType((places[i].getType()));
                        singleLocation.setRadiusm((places[i].getRadiusm()));
                        singleLocation.setPower((places[i].getPower()));
                        singleLocation.setZ_score((places[i].getZ_score()));
                        singleLocation.setLocationCoordinates(places[i].getCoordinate());
                        singleLocation.setPsuedo(false);
                        //getRoutesToAllPoints(places[i].getCoordinate());

                        AddData(attractorTable, places[i].getType(), places[i].getPower(),  places[i].getCoordinate().getLatitude(), places[i].getCoordinate().getLongitude(),
                                places[i].getRadiusm(), places[i].getZ_score(), 0);

                        locationList.add(singleLocation);

                    }

                    if(selected == "Void" && type == 2){
                        mDatabaseHelper = new DatabaseHelper(getActivity(), voidTable);
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(x, y))
                                .title("Void"));
                        amount++;
                        voids++;
                        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
                        singleLocation.setType((places[i].getType()));
                        singleLocation.setRadiusm((places[i].getRadiusm()));
                        singleLocation.setPower((places[i].getPower()));
                        singleLocation.setZ_score((places[i].getZ_score()));
                        singleLocation.setLocationCoordinates(places[i].getCoordinate());
                        singleLocation.setPsuedo(false);
                        locationList.add(singleLocation);

                        AddData(voidTable, places[i].getType(), places[i].getPower(),  places[i].getCoordinate().getLatitude(), places[i].getCoordinate().getLongitude(),
                                places[i].getRadiusm(), places[i].getZ_score(), 0);
                    }

                    i++;
                }
                if(amount > 0){
                    initRecyclerView();
                    startButton.setVisibility(View.GONE);
                   // navigateButton.setVisibility(View.VISIBLE);
                    resetButton.setVisibility(View.VISIBLE);
                } else {
                    //Nothhing was found
                    onCreateDialog();
                }

                saveData();
                progressdialog.dismiss();


            }

            @Override
            public void onFailure(Call<List<Attractors>> call, Throwable t) {
                Log.d("Errorget", "Attrf" + t.getMessage());
                progressdialog.dismiss();
            }


        });
    }

    public void getPsuedo() {

        //Empty previous run
        locationList = new ArrayList<>();
        mapboxMap.clear();
        removeRecyclerView();

        //Start ProgressDialog
        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Looking for " + selected + "s " + "please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.randonauts.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                Type = response.body().getType();
                N = response.body().getN();
                spot = response.body().getSpot();
                hexsize = response.body().getHexsize();

                Call<List<Psuedo>> callGetPsuedo = randoWrapperApi.getPsuedo(N,
                        mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(), distance, 23, 4);

                callGetPsuedo.enqueue(new Callback<List<Psuedo>>() {
                    @Override
                    public void onResponse(Call<List<Psuedo>> call, Response<List<Psuedo>> response) {
                        int i = 0;
                        int count = 0;
                        int amount = 0;

                        for (Psuedo psuedos : response.body()) {
                            count++;
                        }

                        Place places[] = new Place[count];


                        for (Psuedo psuedos : response.body()) {

                            double x = psuedos.getLatitude();
                            double y = psuedos.getLongitude();
                            int type = psuedos.getType();
                            double radiusm = psuedos.getRadiusM();
                            double power = psuedos.getPower();
                            double z_score = psuedos.getZ_score();

                            places[i] = new Place(new LatLng(x, y), type, radiusm, power, z_score);

                            if (waterPointsEnabled) {
                                LatLng center = new LatLng(x, y);
                                final PointF pixel = mapboxMap.getProjection().toScreenLocation(center);
                                List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "water");
                                Log.d("water", "value: " + features);
                                if (!features.isEmpty()) {
                                    continue;
                                }
                            }

                            if(type == 1) {
                                mDatabaseHelper = new DatabaseHelper(getActivity(), attractorTable);
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(x, y))
                                        .title("Attractor"));
                                amount++;
                                psuedo++;
                                SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
                                singleLocation.setType((places[i].getType()));
                                singleLocation.setRadiusm((places[i].getRadiusm()));
                                singleLocation.setPower((places[i].getPower()));
                                singleLocation.setZ_score((places[i].getZ_score()));
                                singleLocation.setLocationCoordinates(places[i].getCoordinate());
                                singleLocation.setPsuedo(true);

                                locationList.add(singleLocation);

                                AddData(attractorTable, places[i].getType(), places[i].getPower(),  places[i].getCoordinate().getLatitude(), places[i].getCoordinate().getLongitude(),
                                        places[i].getRadiusm(), places[i].getZ_score(), 1);
                                attractorsToJSONArray(places[i].getType(), places[i].getRadiusm(),
                                        places[i].getPower(), places[i].getZ_score(), places[i].getCoordinate());
                            }

                            if(type == 2) {
                                mDatabaseHelper = new DatabaseHelper(getActivity(), voidTable);
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(x, y))
                                        .title("Void"));
                                amount++;
                                psuedo++;
                                SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
                                singleLocation.setType((places[i].getType()));
                                singleLocation.setRadiusm((places[i].getRadiusm()));
                                singleLocation.setPower((places[i].getPower()));
                                singleLocation.setZ_score((places[i].getZ_score()));
                                singleLocation.setLocationCoordinates(places[i].getCoordinate());
                                singleLocation.setPsuedo(true);

                                locationList.add(singleLocation);

                                AddData(voidTable, places[i].getType(), places[i].getPower(),  places[i].getCoordinate().getLatitude(), places[i].getCoordinate().getLongitude(),
                                        places[i].getRadiusm(), places[i].getZ_score(), 1);
                                attractorsToJSONArray(places[i].getType(), places[i].getRadiusm(),
                                        places[i].getPower(), places[i].getZ_score(), places[i].getCoordinate());
                            }


                            i++;

                        }

                        if(amount > 0){
                            initRecyclerView();
                            startButton.setVisibility(View.GONE);
                         //   navigateButton.setVisibility(View.VISIBLE);
                            resetButton.setVisibility(View.VISIBLE);
                        } else {
                            //Nothhing was found
                            onCreateDialog();
                        }
                        saveData();
                        writeJsonFile();
                        progressdialog.dismiss();


                    }

                    @Override
                    public void onFailure(Call<List<Psuedo>> call, Throwable t) {
                        progressdialog.dismiss();
                    }
                });


            }

            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {

            }

        });
    }

    public void resetRandonaut() {
        //Empty previous run
        locationList = new ArrayList<>();
        mapboxMap.clear();
        removeRecyclerView();
        attractorsArray = new JSONArray();
        attractorObj = new JSONObject();

    }


    //Disk reading/writing/creating

    public void attractorsToJSONArray(int type, double radiusM, double power, double z_score, LatLng coordinates){

        try {
            String attractor_type;
            if (type == 1) {
                attractor_type = "Attractor";

            } else {
                attractor_type = "Void";
            }
            attractorObj = new JSONObject();
            attractorObj.put("id", psuedo);
            attractorObj.put("type", attractor_type);
            attractorObj.put("power", power);
            attractorObj.put("x", coordinates.getLatitude());
            attractorObj.put("y", coordinates.getLongitude());
            attractorObj.put("radiusm", radiusM);
            attractorObj.put("z_score", z_score);
            attractorObj.put("pseudo", true);


            attractorsArray.put(attractorObj);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("wrote", "" + e);
            e.printStackTrace();
        }

    }

    public void writeJsonFile(){

        FileReader fileReader = null;
        FileWriter fileWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try{
        boolean isFilePresent = isFilePresent(getActivity(), "storage.json");
        if(isFilePresent) {
            Log.d("wrote", "FileisPresent" );
            String jsonString = read(getActivity(), "storage.json");
            //do the json parsing here and do the rest of functionality of app
            Log.d("wrote", "" + jsonString );
            Object json = new JSONTokener(jsonString).nextValue();

            if (json instanceof JSONObject){
                Log.d("wrote", "o" );
                JSONObject jsonObject = new JSONObject(jsonString);
               // jsonObject = (attractorObj);
                JSONArray jsonArray = new JSONArray();
                attractorsArray.put(jsonObject);
                File file = new File(getActivity().getFilesDir(), "storage.json");
                try {

                    fileWriter = new FileWriter(file.getAbsoluteFile());
                    bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(attractorsArray.toString());
                    bufferedWriter.close();
                    Log.d("wrote", "" + jsonObject );
                } catch (IOException e){

                    e.printStackTrace();

                }

            }

            else if (json instanceof JSONArray){
                Log.d("wrote", "d" );
                //JSONArray jsonArray = new JSONArray(jsonString);

             //   JSONArray jsonArray = new JSONArray(jsonString);
             //   jsonArray.put(attractorsArray);

                JSONArray sourceArray = new JSONArray(jsonString);

                String s3 = attractorsArray.toString();
                JSONArray destinationArray = new JSONArray(s3);


               for (int i = 0; i < sourceArray.length(); i++) {
                   JSONObject jsonObject = sourceArray.getJSONObject(i);
                   destinationArray.put(jsonObject);
                }

                File file = new File(getActivity().getFilesDir(), "storage.json");
                try {

                    fileWriter = new FileWriter(file.getAbsoluteFile());
                    bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(destinationArray.toString());
                    bufferedWriter.close();
                    Log.d("wrote", "success" + destinationArray );
                } catch (IOException e){

                    e.printStackTrace();

                }

            }

        } else {
            Log.d("wrote", "Fileisnotpresent" );
            boolean isFileCreated = create(getActivity(), "storage.json", "{}");
            if (isFileCreated) {
                Log.d("wrote", "willcreatenewfile" );
                //proceed with storing the first todo  or show ui
                File file = new File(getActivity().getFilesDir(), "storage.json");
                try {
                    file.createNewFile();
                    fileWriter = new FileWriter(file.getAbsoluteFile());
                    bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(attractorsArray.toString());
                    bufferedWriter.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
                String response = null;
            } else {
                //show error or try again.
            }
        }
    } catch (JSONException e) {
        // TODO Auto-generated catch block
        Log.d("wrote", "error" + e);
        e.printStackTrace();
    }
    } //Fix this


    public void AddData (String table, int type, double power, double x, double y, double radiusm, double z_score, double pseudo) {
        boolean insertData = mDatabaseHelper.addData(table, type, power, x, y, radiusm, z_score, pseudo);

        if (insertData){
            Toast.makeText(getContext(), "succ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "nosucc", Toast.LENGTH_LONG).show();
        }
    }



    private String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private boolean create(Context context, String fileName, String jsonString){
        String FILENAME = "storage.json";
        Log.d("wrote", "testwrote" );
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

    }

    public boolean isFilePresent(Context context, String fileName) {
        Log.d("wrote", "isFilePresent" );
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }


    SendMessage SM;

    interface SendMessage {
        void rng();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    //Dialogs

    public void setReportAlertDialog(){
        reportDialog = new Dialog(getActivity());
        reportDialog.setContentView(R.layout.dialog_report);
        reportDialog.setTitle("Report");


        reportDialog.show();
    }

    public void setRngDialog(){
        reportDialog = new Dialog(getActivity());
        reportDialog.setContentView(R.layout.dialog_loading);
        reportDialog.setTitle("Loading RNG");




        // Create the observer which updates the UI.

        //bitTextView = (TextView) reportDialog.findViewById(R.id.bitTextView);

        //rng.getLiveBoolean().observe(this,  nameObserver);;
        //rng.getLiveByte().observe(reportDialog.getContext()) {
         //   byteTextView.text = it.toString()
        //}

        reportDialog.show();
    }

    final Observer<Boolean> nameObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(@Nullable final Boolean newName) {
            // Update the UI, in this case, a TextView.
            Toast.makeText(getContext(), "update", Toast.LENGTH_SHORT).show();
            bitTextView.setText(newName.toString());
        }
    };

    public void onCreateDialog() {

        new AlertDialog.Builder(getContext())
                .setTitle("No " + selected + "s found")
                .setMessage("There were no " + selected + "'s found. Please try again!")

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

    public void setPreferencesAlertDialog(){
        preferencesDialog = new Dialog(getActivity());
        preferencesDialog.setContentView(R.layout.dialog_preferences);
        preferencesDialog.setTitle("Generate");

        distance = 3000;

        //Buttons
        Button start = (Button) preferencesDialog.findViewById(R.id.preferencesDialogStartButton);
        Button cancel = (Button) preferencesDialog.findViewById(R.id.preferencesDialogCancelButton);

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

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (PsuedoToggleButton.isChecked()) {
                    preferencesDialog.cancel();
                    getPsuedo();
                //    setRngPreferencesDialog(PsuedoToggleButton);

                } else {
                    //getAttractors();
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
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance = progress*100;
                textViewProgress.setText("" + progress*100);
                if(progress == 0){
                    distance = 1*100;
                    textViewProgress.setText("" + 1*100);
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

    public void setRngPreferencesDialog(){
        preferencesDialog = new Dialog(getActivity());
        preferencesDialog.setContentView(R.layout.dialog_rngpreferences);
        preferencesDialog.setTitle("Select RNG");

        //Buttons
        Button start = (Button) preferencesDialog.findViewById(R.id.RNGpreferencesDialogStartButton);
        Button cancel = (Button) preferencesDialog.findViewById(R.id.RNGpreferencesDialogCancelButton);

        //Toggle Buttons
        QuantumToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.AnuToggleButton);
        PoolToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.PoolToggleButton);
        GCPToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.gcpToggleButton);
        CameraToggleButton = (ToggleButton) preferencesDialog.findViewById(R.id.cmrngToggleButton);

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
                if (QuantumToggleButton.isChecked()) {
                    preferencesDialog.cancel();
                    getANUQuantumEntropy();
                } else if (PoolToggleButton.isChecked()) {
                    preferencesDialog.cancel();
                    poolQuantumEntropy();
                } else if (CameraToggleButton.isChecked()) {
                    preferencesDialog.cancel();
                    setQuantumEntropy();
                } else if (GCPToggleButton.isChecked()) {
                    preferencesDialog.cancel();
                    getGCPEntropy();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                preferencesDialog.cancel();

            }

        });


    }

    public void getANUQuantumEntropy(){

        //Start ProgressDialog
        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Getting quantum entropy. please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://devapi.randonauts.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                Type = response.body().getType();
                N = response.body().getN();
                spot = response.body().getSpot();
                hexsize = response.body().getHexsize();

                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, false);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        GID = response.body().getGid();
                        Log.d("Errorget", ""+GID);
                        entropy = entropy + hexsize;
                        saveData();
                        progressdialog.dismiss();
                        //getAttractors(false);
                        getAttractors(false);
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        Log.d("Errorget", t.getMessage());
                        progressdialog.dismiss();
                    }
                });
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                Toast.makeText(getContext(), "ex", Toast.LENGTH_SHORT).show();
                progressdialog.dismiss();
            }
        });



    }

    public void getGCPEntropy(){

        //Start ProgressDialog
        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Getting GCP entropy. please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://devapi.randonauts.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                Type = response.body().getType();
                N = response.body().getN();
                spot = response.body().getSpot();
                hexsize = response.body().getHexsize();

                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, true);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        GID = response.body().getGid();
                        Log.d("Errorget", ""+GID);
                        entropy = entropy + hexsize;
                        saveData();
                        progressdialog.dismiss();
                        //getAttractors(false);
                        getAttractors(false);
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        Log.d("Errorget", t.getMessage());
                        progressdialog.dismiss();
                    }
                });
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                Toast.makeText(getContext(), "ex", Toast.LENGTH_SHORT).show();
                progressdialog.dismiss();
            }
        });



    }

    public void setQuantumEntropy(){
        SM.rng(); //Starts camRNG instance fragment

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.117:3000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

        callGetSizes.enqueue(new Callback<Sizes>() {
            @Override
            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
                Type = response.body().getType();
                N = response.body().getN();
                spot = response.body().getSpot();
                hexsize = response.body().getHexsize();

                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, false);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        GID = response.body().getGid();
                        entropy = entropy + hexsize;
                        saveData();
                        getAttractors(false);
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        Log.d("Errorget", t.getMessage());
                    }
                });
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {
                Toast.makeText(getContext(), "ex", Toast.LENGTH_SHORT).show();
            }
        });



    }

    //POOLS ARE NOT SELECTED RANDOMLY, INTEGER IS THE SAME ON STARTUP!!!
    public void poolQuantumEntropy(){
        //Start ProgressDialog
        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Getting pool quantum entropy. please wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://devapi.randonauts.com/")
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
                Log.d("advd", ""+count);
                Log.d("advd", ""+randomNum);
                for(Pools pools: response.body()){
                    if(current == randomNum){

                        GID = pools.getPool().substring(0, (pools.getPool().length() -5));
                        Log.d("advd", GID);

                        saveData();
                        progressdialog.dismiss();
                        getAttractors(true);
                        //Size is not yet implemented, so use entire pool.
                    }
                    current++;
                }
            }

            @Override
            public void onFailure(Call<List<Pools>> call, Throwable t) {
                Log.d("Errorget", t.getMessage());
                progressdialog.dismiss();
            }
        });
    }

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
                    selected = "Psuedo";
                    VoidToggleButton.setChecked(false);
                    AttractorToggleButton.setChecked(false);
                    AnomalyToggleButton.setChecked(false);
                }
                if (buttonView == AnomalyToggleButton) {
                    selected = "Anomaly";
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

    //Shared preferences

    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(STATS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("ATTRACTORS", atts);
        editor.putLong("VOID", voids);
        editor.putLong("PSEUDO", psuedo);
        editor.putLong("ENTROPY", entropy);

        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(STATS, Context.MODE_PRIVATE);
        atts = sharedPreferences.getLong("ATTRACTORS", 0);
        voids = sharedPreferences.getLong("VOID", 0);
        entropy = sharedPreferences.getLong("ENTROPY", 0);
        psuedo = sharedPreferences.getLong("PSEUDO", 0);
    }

    //Recyclerview

    private void initRecyclerView() {

        RecyclerView recyclerView = v.findViewById(R.id.rv_on_top_of_map);
        recyclerView.setOnFlingListener(null);
        LocationRecyclerViewAdapter locationAdapter =
                new LocationRecyclerViewAdapter(this, createRecyclerViewLocations(), mapboxMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(locationAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

    }

    private void removeRecyclerView() {

        RecyclerView recyclerView = v.findViewById(R.id.rv_on_top_of_map);
        recyclerView.setAdapter(null);

    }

    private List<SingleRecyclerViewLocation> createRecyclerViewLocations() {

        return locationList;
    }

    /**
     * POJO model class for a single location in the recyclerview
     */
    class SingleRecyclerViewLocation {

        private int type;

        private double radiusm;
        private double  power;
        private double z_score;
        private boolean isPsuedo;

        private LatLng locationCoordinates;

        public int getType() {
            return type;
        }

        public void setType(int name) {
            this.type = name;
        }

        public boolean isPsuedo() {
            return isPsuedo;
        }

        public void setPsuedo(boolean psuedo) {
            isPsuedo = psuedo;
        }

        public double getRadiusm() {
            return radiusm;
        }

        public void setRadiusm(double radiusm) {
            this.radiusm = radiusm;
        }

        public double getPower() {
            return power;
        }

        public void setPower(double power) {
            this.power = power;
        }

        public double getZ_score() {
            return z_score;
        }

        public void setZ_score(double z_score) {
            this.z_score = z_score;
        }

        public LatLng getLocationCoordinates() {
            return locationCoordinates;
        }

        public void setLocationCoordinates(LatLng locationCoordinates) {
            this.locationCoordinates = locationCoordinates;
        }

    }

    static class LocationRecyclerViewAdapter extends
            RecyclerView.Adapter<LocationRecyclerViewAdapter.MyViewHolder> {

        private List<SingleRecyclerViewLocation> locationList;
        private MapboxMap map;
        private WeakReference<RandonautFragment> weakReference;

        public LocationRecyclerViewAdapter(RandonautFragment activity,
                                           List<SingleRecyclerViewLocation> locationList,
                                           MapboxMap mapBoxMap) {
            this.locationList = locationList;
            this.map = mapBoxMap;
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_on_top_of_map_card, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            SingleRecyclerViewLocation singleRecyclerViewLocation = locationList.get(position);
            String type = "Attractor";
            if(singleRecyclerViewLocation.getType() == 2){
                type = "Void";
            }
            if(singleRecyclerViewLocation.isPsuedo()){
                type = "Pseudo Attractor";
                if(singleRecyclerViewLocation.getType() == 2){
                    type = "Pseudo Void";
                }
            }

            String radiusm = "Radius: " +  (int) singleRecyclerViewLocation.getRadiusm();
            String power = "Power: " + String.format("%.2f", singleRecyclerViewLocation.getPower());
            String z_score = "Z Score: " + String.format("%.2f", singleRecyclerViewLocation.getZ_score());

            holder.type.setText(type);
            holder.radiusm.setText(radiusm);
            holder.power.setText(power);
            holder.z_score.setText(z_score);


            holder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    LatLng selectedLocationLatLng = locationList.get(position).getLocationCoordinates();
                    //  weakReference.get()
                    //        .drawNavigationPolylineRoute(weakReference.get().directionsRouteList.get(position));
                    CameraPosition newCameraPosition = new CameraPosition.Builder()
                            .target(selectedLocationLatLng)
                            .build();
                    map.easeCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
                }
            });
        }

        @Override
        public int getItemCount() {
            return locationList.size();
        }

        static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView type;
            TextView radiusm;
            TextView power;
            TextView z_score;
            CardView singleCard;
            ItemClickListener clickListener;

            MyViewHolder(View view) {
                super(view);
                type = view.findViewById(R.id.type);
                radiusm = view.findViewById(R.id.radiusm);
                power = view.findViewById(R.id.power);
                z_score = view.findViewById(R.id.z_score);

                singleCard = view.findViewById(R.id.single_location_cardview);
                singleCard.setOnClickListener(this);
            }

            public void setClickListener(ItemClickListener itemClickListener) {
                this.clickListener = itemClickListener;
            }

            @Override
            public void onClick(View view) {
                clickListener.onClick(view, getLayoutPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }





    //Permissions for location
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), "Granted", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getContext(), "Not Granted", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

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

            // locationComponent.getLastKnownLocation();
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(locationComponent.getLastKnownLocation()))
                    .zoom(13)
                    .build();

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }


    //Mapbox Route generation (works but not yet used)

    /**
     * Loop through the possible destination list of LatLng locations and get
     * the route for each destination.
     */
    private void getRoutesToAllPoints(LatLng test) {

        getRoute(Point.fromLngLat(test.getLongitude(), test.getLatitude()));

    }

    /**
     * Make a call to the Mapbox Directions API to get the route from the person location icon
     * to the marker's location and then add the route to the route list.
     *
     * @param destination the marker associated with the recyclerview card that was tapped on.
     */
    @SuppressWarnings({"MissingPermission"})
    private void getRoute(Point destination) {
        directionsOriginPoint = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(),
                mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
        MapboxDirections client = MapboxDirections.builder()
                .origin(directionsOriginPoint)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken("pk.eyJ1IjoiZGF2aWRmYWxjb24iLCJhIjoiY2szbjRzZmd2MTcwNDNkcXhnbTFzbHR0cCJ9.ZgbfsJXtrCFgI0rRJkwUyg")
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Log.d(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.d(TAG, "No routes found");
                    return;
                }
                // Add the route to the list.
                directionsRouteList.add(response.body().routes().get(0));
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.d(TAG, "Error: " + throwable.getMessage());
                if (!throwable.getMessage().equals("Coordinate is invalid: 0,0")) {
                    Toast.makeText(getContext(),
                            "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Update the GeoJSON data for the direction route LineLayer.
     *
     * @param route The route to be drawn in the map's LineLayer that was set up above.
     */
    private void drawNavigationPolylineRoute(final DirectionsRoute route) {

        if (mapboxMap != null) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    List<Feature> directionsRouteFeatureList = new ArrayList<>();
                    LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
                    List<Point> lineStringCoordinates = lineString.coordinates();
                    for (int i = 0; i < lineStringCoordinates.size(); i++) {
                        directionsRouteFeatureList.add(Feature.fromGeometry(
                                LineString.fromLngLats(lineStringCoordinates)));
                    }
                    dashedLineDirectionsFeatureCollection =
                            FeatureCollection.fromFeatures(directionsRouteFeatureList);
                    GeoJsonSource source = style.getSourceAs(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);
                    if (source != null) {
                        source.setGeoJson(dashedLineDirectionsFeatureCollection);
                    }
                }
            });
        }
    }





    //Mapbox functions

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
