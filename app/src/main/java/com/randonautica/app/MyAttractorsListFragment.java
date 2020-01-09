package com.randonautica.app;

import android.app.Dialog;
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

public class MyAttractorsListFragment extends Fragment {


    //Store attractors
    JSONObject attractorObj = new JSONObject();
    JSONArray attractorsArray = new JSONArray();

    ArrayList<Attractor> attractorArray = new ArrayList<Attractor>();

    Dialog reportDialog;

    private ListView mListView;

    DatabaseHelper mDatabaseHelper;

    String attractorTable = "Attractors";

    private View view;

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_attractors, container, false);
        getActivity().setTitle("My Attractors");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabaseHelper = new DatabaseHelper(getActivity(), attractorTable);
        mListView = (ListView) view.findViewById(R.id.attractorsList);
        populateListView();
    }

    private void populateListView() {
        Log.d("test", "populateListView: Displaying data in the ListView.");
        attractorArray = new ArrayList<Attractor>();
        Cursor data = mDatabaseHelper.getData(attractorTable);

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

        AttractorListAdapter adapter = new AttractorListAdapter(getContext(), R.layout.my_attractors_list_item_test, attractorArray);
        mListView.setAdapter(adapter);


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
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            String type = "Attractor " +  getItem(position).getId();

            String power = "Power: " + df2.format(Double.valueOf(getItem(position).getPower()));
            String radiusm = "Radius: " + df2.format(Double.valueOf(getItem(position).getRadiusm()));
            String z_score = "Z_Score: " + df2.format(Double.valueOf(getItem(position).getZ_score()));
            String pseudo;
            if(Double.valueOf(getItem(position).getPseudo()) == 1){
                pseudo = "Pseudo: " + "Yes";
            } else {
                pseudo = "Pseudo: " + "No";
            }



            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);

            TextView textViewType = (TextView) convertView.findViewById(R.id.textViewType);
            TextView textViewPower = (TextView) convertView.findViewById(R.id.textViewPower);
            TextView textViewRadius = (TextView) convertView.findViewById(R.id.textViewRadius);
            TextView textViewZ_score = (TextView) convertView.findViewById(R.id.textViewZ_score);
            TextView textViewPsuedo = (TextView) convertView.findViewById(R.id.textViewPsuedo);

            textViewType.setText(type);
            textViewPower.setText(power);
            textViewRadius.setText(radiusm);
            textViewZ_score.setText(z_score);
            textViewPsuedo.setText(pseudo);

            Button button = (Button) convertView.findViewById(R.id.reportButtonInList);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setReportAlertDialog();

                }
            });

            Button showButton = (Button) convertView.findViewById(R.id.showButtonInList);
            showButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setShowAlertDialog(Integer.parseInt(getItem(position).getId()),
                            Double.valueOf(getItem(position).getPower()),
                            Double.valueOf(getItem(position).getX()),
                            Double.valueOf(getItem(position).getY()),
                            Double.valueOf(getItem(position).getRadiusm()),
                            Double.valueOf(getItem(position).getZ_score()),
                            Double.valueOf(getItem(position).getPseudo()));

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

    public void setShowAlertDialog(final int type, final double power, final double x, final double y, final double radiusm, final double z_score, final double pseudo){
        reportDialog = new Dialog(getActivity());
        reportDialog.setContentView(R.layout.dialog_showonmap);
        reportDialog.setTitle("Show");

        TextView textViewShowOnMap = (TextView) reportDialog.findViewById(R.id.textViewShowOnMap);
        textViewShowOnMap.setText(
                "This will remove the old attractors from the map, do you wish to proceed?");

        Button okButton = (Button) reportDialog.findViewById(R.id.preferencesDialogStartButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SM.sendData(type, power, x, y, radiusm, z_score, pseudo);
                reportDialog.dismiss();

            }
        });

        reportDialog.show();
    }

    SendMessage SM;

    interface SendMessage {
        void sendData(int type, double power, double x, double y, double radiusm, double z_score, double pseudo);
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



