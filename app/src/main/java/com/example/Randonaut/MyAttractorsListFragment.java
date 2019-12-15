package com.example.Randonaut;

import android.app.Dialog;
import android.content.Context;
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

import com.example.Randonaut.Classes.Attractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyAttractorsListFragment extends Fragment {

    //Store attractors
    JSONObject attractorObj = new JSONObject();
    JSONArray attractorsArray = new JSONArray();

    ArrayList<Attractor> attractorArray = new ArrayList<Attractor>();

    Dialog reportDialog;

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("My Attractors");
        return inflater.inflate(R.layout.fragment_list_attractors, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isFilePresent = isFilePresent(getActivity(), "storage.json");
        if(isFilePresent) {

            Log.d("wrote", "FileisPresent" );
            String jsonString = read(getActivity(), "storage.json");
            //do the json parsing here and do the rest of functionality of app
            Log.d("wrote", "" + jsonString );
            Object json = null;
            try {
                json = new JSONTokener(jsonString).nextValue();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (json instanceof JSONObject){
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);


                    Attractor resultRow = new Attractor();

                    resultRow.type = jsonObject.getString("type");
                    resultRow.id = jsonObject.getString("id");
                    resultRow.power = jsonObject.getString("power");
                    resultRow.x = jsonObject.getString("x");
                    resultRow.y = jsonObject.getString("y");
                    resultRow.radiusm = jsonObject.getString("radiusm");
                    resultRow.z_score = jsonObject.getString("z_score");
                    resultRow.pseudo = jsonObject.getString("pseudo");

                    attractorArray.add(resultRow);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListView myListView = (ListView) getActivity().findViewById(R.id.attractorsList);
                AttractorListAdapter adapter = new AttractorListAdapter(getContext(), R.layout.my_attractors_list_item_test, attractorArray);
                myListView.setAdapter(adapter);

            }

            else if (json instanceof JSONArray){
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(jsonString);
                    for(int i =0; i < jsonArray.length();i++){

                      JSONObject json_data = jsonArray.getJSONObject(i);
                        Attractor resultRow = new Attractor();

                        resultRow.type = json_data.getString("type");
                        resultRow.id = json_data.getString("id");
                        resultRow.power = json_data.getString("power");
                        resultRow.x = json_data.getString("x");
                        resultRow.y = json_data.getString("y");
                        resultRow.radiusm = json_data.getString("radiusm");
                        resultRow.z_score = json_data.getString("z_score");
                        resultRow.pseudo = json_data.getString("pseudo");

                        attractorArray.add(resultRow);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListView myListView = (ListView) getActivity().findViewById(R.id.attractorsList);
                AttractorListAdapter adapter = new AttractorListAdapter(getContext(), R.layout.my_attractors_list_item_test, attractorArray);
                myListView.setAdapter(adapter);


            }



        } else {
            Log.d("wrote", "Fileisnotpresent");
        }




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

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }


}




