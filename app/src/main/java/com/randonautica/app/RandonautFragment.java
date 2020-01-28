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
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.randonautica.app.Classes.Attractors;
import com.randonautica.app.Classes.DatabaseHelper;
import com.randonautica.app.Classes.Entropy;
import com.randonautica.app.Classes.Pools;
import com.randonautica.app.Classes.Psuedo;
import com.randonautica.app.Classes.RandoWrapperApi;
import com.randonautica.app.Classes.ReportQuestions;
import com.randonautica.app.Classes.Sizes;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
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
import org.json.JSONObject;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
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
    //Load keys
    static {
        System.loadLibrary("keys");
    }
    //Native Modules
    protected native String getApiKey();
    protected native String getBaseApi();

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
    private Button reportButton;
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

    //Message to mainactivity
    SendMessage SM;

    /** create view */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (v == null) {
            createMapInstance();
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


    protected void createMapInstance(){

        Mapbox.getInstance(getContext(), new String(Base64.decode(getApiKey(),Base64.DEFAULT)));

    }

    /** after view is created - set waterpoints and buttons */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MySettingsFragment.SHARED_PREFS, Context.MODE_PRIVATE);
        waterPointsEnabled = sharedPreferences.getBoolean("enableWaterPoints", false);

            startButton = (Button) view.findViewById(R.id.startButton);
            resetButton = (Button) view.findViewById(R.id.resetRandonaut);
            reportButton = (Button) view.findViewById(R.id.reportButton);

            startButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onClick(View v) {
                    setPreferencesAlertDialog();
                    if(mapboxMap.getLocationComponent().isLocationComponentActivated() == false){
                        enableLocationComponent(mapboxMap.getStyle());
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
                }
            });



    }

    /** set map view */
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


    /** set attractor from my attractors */
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
        if(pseudo == 1){
            singleLocation.setPsuedo(true);
        } else {
            singleLocation.setPsuedo(false);
        }

        //Set circle of radius
        mapboxMap.addPolygon(generatePerimeter(
                new LatLng(x, y),
                (radiusm/1000),
                64));

        locationList.add(singleLocation);
        initRecyclerView();

        startButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.VISIBLE);
    }

    /** all the generate attractor functions */

    public void getAttractors(boolean pool, boolean gcp){

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

        Call<List<Attractors>> callGetAttractors = randoWrapperApi.getAttractorsTest(GID,
                mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(), distance, pool, gcp);

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

                    //First Part
                    String GID = attractors.getAttractors().getGID();
                    String TID = attractors.getAttractors().getTID();
                    String LID = attractors.getAttractors().getLID();
                    int type = attractors.getAttractors().getType();
                    double x_ = attractors.getAttractors().getX();
                    double y_ = attractors.getAttractors().getY();


                    //Second part
                    double x = attractors.getAttractors().getCenter().getLatlng().getPoint().getLatitude(); //Used in map
                    double y = attractors.getAttractors().getCenter().getLatlng().getPoint().getLongitude(); //Used in map

                    double distance = attractors.getAttractors().getCenter().getLatlng().getBearing().getDistance();
                    double initialBearing = attractors.getAttractors().getCenter().getLatlng().getBearing().getInitialBearing();
                    double finalBearing = attractors.getAttractors().getCenter().getLatlng().getBearing().getFinalBearing();

                    //Third part
                    int side = attractors.getAttractors().getSide();
                    double distanceErr = attractors.getAttractors().getDistanceErr();
                    double radiusM = attractors.getAttractors().getRadiusM();
                    int n = attractors.getAttractors().getN();
                    double mean = attractors.getAttractors().getMean();
                    int rarity = attractors.getAttractors().getRarity();
                    double power_old = attractors.getAttractors().getPower_old();
                    double power = attractors.getAttractors().getPower();
                    double z_score = attractors.getAttractors().getZ_score();
                    double probability_single = attractors.getAttractors().getProbability_single();
                    double integral_score = attractors.getAttractors().getIntegral_score();
                    double significance = attractors.getAttractors().getSignificance();
                    double probability = attractors.getAttractors().getProbability();
                    int FILTERING_SIGNIFICANCE = attractors.getAttractors().getFILTERING_SIGNIFICANCE();

                    places[i]=new Place(new LatLng(x, y), GID,  TID,  LID,  x_,  y_,  distance,  initialBearing,  finalBearing, side,  distanceErr,  radiusM, n,  mean, rarity,  power_old,  probability_single,  integral_score,  significance,  probability, FILTERING_SIGNIFICANCE, type, radiusM,  power,  z_score);

                    if(waterPointsEnabled){
                        LatLng center = new LatLng(x, y);
                        final PointF pixel = mapboxMap.getProjection().toScreenLocation(center);
                        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "water");
                        if(!features.isEmpty()){
                            continue;
                        }
                    }

                    if(selected == "Attractor" && type == 1){
                        //Make databaseHelper
                        mDatabaseHelper = new DatabaseHelper(getActivity(), attractorTable);

                        //Generate Marker
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(x, y))
                                .title("Attractor"));

                        //Generate Circle
                        mapboxMap.addPolygon(generatePerimeter(
                                new LatLng(x, y),
                                (radiusM/1000),
                                64));

                        amount++;
                        atts++;
                        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
                        singleLocation.setType((places[i].getType()));
                        singleLocation.setRadiusm((places[i].getRadiusM()));
                        singleLocation.setPower((places[i].getPower()));
                        singleLocation.setZ_score((places[i].getZ_score()));
                        singleLocation.setLocationCoordinates(places[i].getCoordinate());
                        singleLocation.setPsuedo(false);
                        //getRoutesToAllPoints(places[i].getCoordinate());

                        AddData(attractorTable,
                                places[i].getCoordinate().getLatitude(),
                                places[i].getCoordinate().getLongitude(),
                                places[i].getGID(),
                                places[i].getTID(),
                                places[i].getLID(),

                                places[i].getX(),
                                places[i].getY(),
                                places[i].getDistance(),
                                places[i].getInitialBearing(),
                                places[i].getFinalBearing(),
                                places[i].getSide(),
                                places[i].getDistanceErr(),
                                places[i].getRadiusM(),
                                places[i].getN(),
                                places[i].getMean(),
                                places[i].getRarity(),
                                places[i].getPower_old(),
                                places[i].getProbability_single(),
                                places[i].getIntegral_score(),
                                places[i].getSignificance(),
                                places[i].getProbability(),
                                places[i].getFILTERING_SIGNIFICANCE(),
                                places[i].getType(),
                                places[i].getRadiusM(),
                                places[i].getPower(),
                                places[i].getZ_score(),
                                0, 0);

                        locationList.add(singleLocation);

                    }

                    if(selected == "Void" && type == 2){
                        mDatabaseHelper = new DatabaseHelper(getActivity(), voidTable);

                        //Generate Marker
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(x, y))
                                .title("Void"));

                        //Generate Circle
                        mapboxMap.addPolygon(generatePerimeter(
                                new LatLng(x, y),
                                (places[i].getRadiusM()/1000),
                                64));

                        amount++;
                        voids++;
                        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
                        singleLocation.setType((places[i].getType()));
                        singleLocation.setRadiusm((places[i].getRadiusM()));
                        singleLocation.setPower((places[i].getPower()));
                        singleLocation.setZ_score((places[i].getZ_score()));
                        singleLocation.setLocationCoordinates(places[i].getCoordinate());
                        singleLocation.setPsuedo(false);

                        AddData(voidTable,
                                places[i].getCoordinate().getLatitude(),
                                places[i].getCoordinate().getLongitude(),
                                places[i].getGID(),
                                places[i].getTID(),
                                places[i].getLID(),

                                places[i].getX(),
                                places[i].getY(),
                                places[i].getDistance(),
                                places[i].getInitialBearing(),
                                places[i].getFinalBearing(),
                                places[i].getSide(),
                                places[i].getDistanceErr(),
                                places[i].getRadiusM(),
                                places[i].getN(),
                                places[i].getMean(),
                                places[i].getRarity(),
                                places[i].getPower_old(),
                                places[i].getProbability_single(),
                                places[i].getIntegral_score(),
                                places[i].getSignificance(),
                                places[i].getProbability(),
                                places[i].getFILTERING_SIGNIFICANCE(),
                                places[i].getType(),
                                places[i].getRadiusM(),
                                places[i].getPower(),
                                places[i].getZ_score(),
                                0, 0);

                        locationList.add(singleLocation);

                    }

                    i++;
                }

                //Check for anomaly
                if(selected == "Anomalie"){
                    for (int c = count - 1; c > 0; c--) { //Start bubblesort
                        for (int j = 0; j < c; j++) {
                            if (places[j + 1] == null) {
                                continue;
                            }
                            if (places[j] == null ||places[j + 1].compareTo(places[j]) < 0) {
                                Place temp = places[j + 1];
                                places[j + 1] = places[j];
                                places[j] = temp;
                            }
                        }
                    } //End bubblesort
                    for(i = 0; i < count; i++){

                        //Make databaseHelper
                        mDatabaseHelper = new DatabaseHelper(getActivity(), anomalyTable);

                        //Generate Marker
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(places[i].getCoordinate().getLatitude(),  places[i].getCoordinate().getLongitude()))
                                .title("Attractor"));

                        //Generate Circle
                        mapboxMap.addPolygon(generatePerimeter(
                                new LatLng( places[i].getCoordinate().getLatitude(),  places[i].getCoordinate().getLongitude()),
                                (places[i].getRadiusM()/1000),
                                64));

                        amount++;
                        anomalies++;
                        SingleRecyclerViewLocation singleLocation = new SingleRecyclerViewLocation();
                        singleLocation.setType((places[i].getType()));
                        singleLocation.setRadiusm((places[i].getRadiusM()));
                        singleLocation.setPower((places[i].getPower()));
                        singleLocation.setZ_score((places[i].getZ_score()));
                        singleLocation.setLocationCoordinates(places[i].getCoordinate());
                        singleLocation.setPsuedo(false);

                        AddData(anomalyTable,
                                places[i].getCoordinate().getLatitude(),
                                places[i].getCoordinate().getLongitude(),
                                places[i].getGID(),
                                places[i].getTID(),
                                places[i].getLID(),

                                places[i].getX(),
                                places[i].getY(),
                                places[i].getDistance(),
                                places[i].getInitialBearing(),
                                places[i].getFinalBearing(),
                                places[i].getSide(),
                                places[i].getDistanceErr(),
                                places[i].getRadiusM(),
                                places[i].getN(),
                                places[i].getMean(),
                                places[i].getRarity(),
                                places[i].getPower_old(),
                                places[i].getProbability_single(),
                                places[i].getIntegral_score(),
                                places[i].getSignificance(),
                                places[i].getProbability(),
                                places[i].getFILTERING_SIGNIFICANCE(),
                                places[i].getType(),
                                places[i].getRadiusM(),
                                places[i].getPower(),
                                places[i].getZ_score(),
                                0, 0);

                        locationList.add(singleLocation);

                        i ++;
                    }



                } //End anomaly

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
                .baseUrl(new String(Base64.decode(getBaseApi(),Base64.DEFAULT)))
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
                int seed = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    seed = ThreadLocalRandom.current().nextInt(0, 2147483647);
                } else {
                    seed = 23;
                }
                Call<List<Psuedo>> callGetPsuedo = randoWrapperApi.getPsuedo(N,
                        mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(), distance, seed, 4);

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

                            //First Part
                            String GID = psuedos.getGID();
                            String TID = psuedos.getTID();
                            String LID = psuedos.getLID();
                            int type = psuedos.getType();
                            double x_ = psuedos.getX();
                            double y_ = psuedos.getY();


                            //Second part
                            double x = psuedos.getLatitude(); //Used in map
                            double y = psuedos.getLongitude(); //Used in map

                            double distance = psuedos.getDistance();
                            double initialBearing = psuedos.getInitialBearing();
                            double finalBearing = psuedos.getFinalBearing();

                            //Third part
                            int side = psuedos.getSide();
                            double distanceErr = psuedos.getDistanceErr();
                            double radiusM = psuedos.getRadiusM();
                            int n = psuedos.getN();
                            double mean = psuedos.getMean();
                            int rarity = psuedos.getRarity();
                            double power_old = psuedos.getPower_old();
                            double power = psuedos.getPower();
                            double z_score = psuedos.getZ_score();
                            double probability_single = psuedos.getProbability_single();
                            double integral_score = psuedos.getIntegral_score();
                            double significance = psuedos.getSignificance();
                            double probability = psuedos.getProbability();
                            int FILTERING_SIGNIFICANCE = psuedos.getFILTERING_SIGNIFICANCE();

                            places[i]=new Place(new LatLng(x, y), GID,  TID,  LID,  x_,  y_,  distance,  initialBearing,  finalBearing, side,  distanceErr,  radiusM, n,  mean, rarity,  power_old,  probability_single,  integral_score,  significance,  probability, FILTERING_SIGNIFICANCE, type, radiusM,  power,  z_score);


                            if (waterPointsEnabled) {
                                LatLng center = new LatLng(x, y);
                                final PointF pixel = mapboxMap.getProjection().toScreenLocation(center);
                                List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "water");
                                if (!features.isEmpty()) {
                                    continue;
                                }
                            }

                            if(type == 1) {
                                mDatabaseHelper = new DatabaseHelper(getActivity(), attractorTable);
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(x, y))
                                        .title("Attractor"));

                                //Generate Circle
                                mapboxMap.addPolygon(generatePerimeter(
                                        new LatLng(x, y),
                                        (radiusM/1000),
                                        64));

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

                                AddData(voidTable,
                                        places[i].getCoordinate().getLatitude(),
                                        places[i].getCoordinate().getLongitude(),
                                        places[i].getGID(),
                                        places[i].getTID(),
                                        places[i].getLID(),

                                        places[i].getX(),
                                        places[i].getY(),
                                        places[i].getDistance(),
                                        places[i].getInitialBearing(),
                                        places[i].getFinalBearing(),
                                        places[i].getSide(),
                                        places[i].getDistanceErr(),
                                        places[i].getRadiusM(),
                                        places[i].getN(),
                                        places[i].getMean(),
                                        places[i].getRarity(),
                                        places[i].getPower_old(),
                                        places[i].getProbability_single(),
                                        places[i].getIntegral_score(),
                                        places[i].getSignificance(),
                                        places[i].getProbability(),
                                        places[i].getFILTERING_SIGNIFICANCE(),
                                        places[i].getType(),
                                        places[i].getRadiusM(),
                                        places[i].getPower(),
                                        places[i].getZ_score(),
                                        1, 0);
                            }

                            if(type == 2) {
                                mDatabaseHelper = new DatabaseHelper(getActivity(), voidTable);
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(x, y))
                                        .title("Void"));

                                //Generate Circle
                                mapboxMap.addPolygon(generatePerimeter(
                                        new LatLng(x, y),
                                        (places[i].getRadiusM()/1000),
                                        64));

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

                                AddData(voidTable,
                                        places[i].getCoordinate().getLatitude(),
                                        places[i].getCoordinate().getLongitude(),
                                        places[i].getGID(),
                                        places[i].getTID(),
                                        places[i].getLID(),

                                        places[i].getX(),
                                        places[i].getY(),
                                        places[i].getDistance(),
                                        places[i].getInitialBearing(),
                                        places[i].getFinalBearing(),
                                        places[i].getSide(),
                                        places[i].getDistanceErr(),
                                        places[i].getRadiusM(),
                                        places[i].getN(),
                                        places[i].getMean(),
                                        places[i].getRarity(),
                                        places[i].getPower_old(),
                                        places[i].getProbability_single(),
                                        places[i].getIntegral_score(),
                                        places[i].getSignificance(),
                                        places[i].getProbability(),
                                        places[i].getFILTERING_SIGNIFICANCE(),
                                        places[i].getType(),
                                        places[i].getRadiusM(),
                                        places[i].getPower(),
                                        places[i].getZ_score(),
                                        1, 0);

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
    } //PSEUDOOOOOOOOOOOO

    public void resetRandonaut() {
        //Empty previous run
        locationList = new ArrayList<>();
        mapboxMap.clear();
        removeRecyclerView();
        attractorsArray = new JSONArray();
        attractorObj = new JSONObject();
    }

    //Disk reading/writing/creating
    public void AddData (String table,double x, double y, String GID, String TID, String LID, double x_, double y_, double distance, double initialBearing, double finalBearing, int side, double distanceErr, double radiusM, int n, double mean, int rarity, double power_old, double probability_single, double integral_score, double significance, double probability, int FILTERING_SIGNIFICANCE, int type, double radiusm, double power, double z_score,double pseudo, int report) {
        boolean insertData = mDatabaseHelper.addData(table, x,  y,  GID,  TID,  LID,  x_,  y_,  distance,  initialBearing,  finalBearing,  side,  distanceErr,  radiusM,  n,  mean,  rarity,  power_old,  probability_single,  integral_score,  significance,  probability,  FILTERING_SIGNIFICANCE,  type,  radiusm,  power,  z_score, pseudo,  report);

        if (insertData){

        } else {

        }
    }


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
                .baseUrl(new String(Base64.decode(getBaseApi(),Base64.DEFAULT)))
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
                        progressdialog.dismiss();
                        getAttractors(false, false);
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        progressdialog.dismiss();
                    }
                });
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {

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
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
              //  .baseUrl(new String(Base64.decode(getBaseApi(),Base64.DEFAULT)))
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

                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, true);

                callGetEntropy.enqueue(new Callback<Entropy>() {
                    @Override
                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
                        GID = response.body().getGid();
                        entropy = entropy + hexsize;
                        saveData();
                        progressdialog.dismiss();
                        //getAttractors(false);
                        getAttractors(false, true);
                    }
                    @Override
                    public void onFailure(Call<Entropy> call, Throwable t) {
                        progressdialog.dismiss();
                    }
                });
            }
            @Override
            public void onFailure(Call<Sizes> call, Throwable t) {

                progressdialog.dismiss();
            }
        });



    }

    public void setQuantumEntropy(){
        SM.rng(); //Starts camRNG instance fragment
//
//        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.1.117:3000/")
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        randoWrapperApi = retrofit.create(RandoWrapperApi.class);
//
//        Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);
//
//        callGetSizes.enqueue(new Callback<Sizes>() {
//            @Override
//            public void onResponse(Call<Sizes> call, Response<Sizes> response) {
//                Type = response.body().getType();
//                N = response.body().getN();
//                spot = response.body().getSpot();
//                hexsize = response.body().getHexsize();
//
//                Call<Entropy> callGetEntropy = randoWrapperApi.getEntropy(hexsize, false, false);
//
//                callGetEntropy.enqueue(new Callback<Entropy>() {
//                    @Override
//                    public void onResponse(Call<Entropy> call, Response<Entropy> response) {
//                        GID = response.body().getGid();
//                        entropy = entropy + hexsize;
//                        saveData();
//                        getAttractors(false, false);
//                    }
//                    @Override
//                    public void onFailure(Call<Entropy> call, Throwable t) {
//
//                    }
//                });
//            }
//            @Override
//            public void onFailure(Call<Sizes> call, Throwable t) {
//
//            }
//        });



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
                .baseUrl(new String(Base64.decode(getBaseApi(),Base64.DEFAULT)))
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


                        saveData();
                        progressdialog.dismiss();
                        getAttractors(true, false);
                        //Size is not yet implemented, so use entire pool.
                    }
                    current++;
                }
            }

            @Override
            public void onFailure(Call<List<Pools>> call, Throwable t) {

                progressdialog.dismiss();
            }
        });
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
                    selected = "Psuedo";
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
     * Recyclerview containing all the attractors/voids
     */

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
        private Button reportButtton = reportButton;

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

        public Button getReportButtton() {
            return reportButtton;
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

        public void setReportAlertDialog() {
            Dialog reportDialog;
            reportDialog = new Dialog(getApplicationContext());
            JSONObject obj = new JSONObject();

            // reportDialog.setContentView(R.layout.dialog_report);
            //reportDialog.setTitle("Report");
            reportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            reportDialog.setContentView(R.layout.dialog_questionreport);

            final ReportQuestions rReportQuestions = new ReportQuestions();

            final TextView rQuestionView;
            final TextView qeustionViewScore;
            Button yesAnwserButton;
            Button noAnwserButton;

            final int[] currentQeustion = {0};
            int maxQeustions = 5;

            yesAnwserButton = (Button) reportDialog.findViewById(R.id.yesAnwserButton);
            noAnwserButton = (Button) reportDialog.findViewById(R.id.noAnwserButton);
            rQuestionView = (TextView) reportDialog.findViewById(R.id.rQuestionView);
            qeustionViewScore = (TextView) reportDialog.findViewById(R.id.qeustionView1);

            qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");

            //Button listener for yes
            yesAnwserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentQeustion[0]++;
                    rQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                    if(currentQeustion[0] == 3){

                        //    reportDialogWindow(currentQeustion, position, showButton);
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");

                    }
                    qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                }

            });

            //Button listener for no
            noAnwserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentQeustion[0]++;
                    rQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                    if(currentQeustion[0] == 3){
                        //   reportDialog.setContentView(R.layout.dialog_qeustionreportwindow);
                        //    reportDialogWindow(currentQeustion, position, showButton);
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");

                    }
                    qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                }

            });


            reportDialog.show();
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
            final Button reportButton = singleRecyclerViewLocation.getReportButtton();

            holder.type.setText(type);
            holder.radiusm.setText(radiusm);
            holder.power.setText(power);
            holder.z_score.setText(z_score);

                singleRecyclerViewLocation.getReportButtton();

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
                   // reportButton.setVisibility(view.VISIBLE);  //Not yet working, report function while clicking an attractor.
                }


            });

            reportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



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
//                reportButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        setReportAlertDialog(1);
//
//                    }
//                });


            }















        }
    }



    public interface ItemClickListener {
        void onClick(View view, int position);

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

            if(locationComponent.getLastKnownLocation() != null){
                // locationComponent.getLastKnownLocation();
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(locationComponent.getLastKnownLocation()))
                        .zoom(13)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000);

            }




        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }


    /**
     * Send message to the mainactivity, used for camrng
     * */

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


    /**
     * Draw circle on the mapview
     */

    private PolygonOptions generatePerimeter(LatLng centerCoordinates, double radiusInKilometers, int numberOfSides) {
        List<LatLng> positions = new ArrayList<>();
        double distanceX = radiusInKilometers / (111.319 * Math.cos(centerCoordinates.getLatitude() * Math.PI / 180));
        double distanceY = radiusInKilometers / 110.574;

        double slice = (2 * Math.PI) / numberOfSides;

        double theta;
        double x;
        double y;
        LatLng position;
        for (int i = 0; i < numberOfSides; ++i) {
            theta = i * slice;
            x = distanceX * Math.cos(theta);
            y = distanceY * Math.sin(theta);

            position = new LatLng(centerCoordinates.getLatitude() + y,
                    centerCoordinates.getLongitude() + x);
            positions.add(position);
        }
        return new PolygonOptions()
                .addAll(positions)
                .fillColor(Color.BLUE)
                .alpha(0.4f);
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
