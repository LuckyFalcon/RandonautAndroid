package com.randonautica.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.randonautica.app.Classes.Attractor;
import com.randonautica.app.Classes.DatabaseHelper;
import com.randonautica.app.Classes.RandoWrapperApi;
import com.randonautica.app.Classes.Sizes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyAnomalyListFragment extends Fragment {

    ProgressDialog progressdialog;

    //Store attractors
    JSONObject attractorObj = new JSONObject();
    JSONArray attractorsArray = new JSONArray();

    ArrayList<Attractor> attractorArray = new ArrayList<Attractor>();

    Dialog reportDialog;

    private ListView mListView;

    DatabaseHelper mDatabaseHelper;

    private RandoWrapperApi randoWrapperApi;

    String anomalyTable = "Anomalies";

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("My Attractors");

        return inflater.inflate(R.layout.fragment_list_anomalies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabaseHelper = new DatabaseHelper(getActivity(), anomalyTable);
        mListView = (ListView) getActivity().findViewById(R.id.attractorsList);
        populateListView();

    }

    private void populateListView() {
        Log.d("test", "populateListView: Displaying data in the ListView.");

        Cursor data = mDatabaseHelper.getData(anomalyTable);
        while(data.moveToNext()){
            Attractor resultRow = new Attractor();

            resultRow.id = data.getString(0);
            resultRow.type = data.getString(1);
            resultRow.power = data.getString(2);
            resultRow.x = data.getString(3);
            resultRow.y = data.getString(4);
            resultRow.radiusm = data.getString(5);
            resultRow.z_score = data.getString(6);
            resultRow.pseudo = data.getString(7);

            attractorArray.add(resultRow);

        }

        ListView myListView = (ListView) getActivity().findViewById(R.id.anomaliesList);
        AttractorListAdapter adapter = new AttractorListAdapter(getContext(), R.layout.my_attractors_list_item_test, attractorArray);
        myListView.setAdapter(adapter);


    }



    class AttractorListAdapter extends ArrayAdapter<Attractor>{

        private static final String TAG = "AttractorListAdapter";

        private Context mContext;
        int mResource;

        public AttractorListAdapter(@NonNull Context context, int resource, @NonNull List<Attractor> objects) {
            super(context, resource, objects);
            this.mContext = context;
            mResource = resource;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            String type;

            if(getItem(position).getPseudo() == "true"){
                type = "Pseudo " + getItem(position).getType() + " " +  getItem(position).getId();
            } else {
                type = getItem(position).getType() + " " +  getItem(position).getId();
            }

            String power = "Power: " + df2.format(Double.valueOf(getItem(position).getPower()));
            String radiusm = "Radius: " + df2.format(Double.valueOf(getItem(position).getRadiusm()));
            String z_score = "Z_Score: " + df2.format(Double.valueOf(getItem(position).getZ_score()));


            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);

            TextView textViewType = (TextView) convertView.findViewById(R.id.textViewType);
            TextView textViewPower = (TextView) convertView.findViewById(R.id.textViewPower);
            TextView textViewRadius = (TextView) convertView.findViewById(R.id.textViewRadius);
            TextView textViewZ_score = (TextView) convertView.findViewById(R.id.textViewZ_score);


            textViewType.setText(type);
            textViewPower.setText(power);
            textViewRadius.setText(radiusm);
            textViewZ_score.setText(z_score);

            Button button = (Button) convertView.findViewById(R.id.reportButtonInList);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setReportAlertDialog();

                }
            });
            return convertView;
        }
    }


    public void setReportAlertDialog(){
        reportDialog = new Dialog(getActivity());
        reportDialog.setContentView(R.layout.dialog_report);
        reportDialog.setTitle("Report");


        reportDialog.show();
    }

    public void sendReport() {

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
                .baseUrl("https://api.randonauts.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        randoWrapperApi = retrofit.create(RandoWrapperApi.class);

        //Call<Sizes> callGetSizes = randoWrapperApi.getSizes(distance);

    }

}




