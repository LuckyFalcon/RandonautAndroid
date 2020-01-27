package com.randonautica.app;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.randonautica.app.Classes.Attractor;
import com.randonautica.app.Classes.DatabaseHelper;
import com.randonautica.app.Classes.ReportQuestions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyVoidsListFragment extends Fragment {

    ArrayList<Attractor> attractorArray = new ArrayList<Attractor>();
    Dialog reportDialog;
    private ListView mListView;
    DatabaseHelper mDatabaseHelper;
    String voidTable = "Voids";
    private View view;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    MyAttractorsListFragment.SendMessage SM;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_voids, container, false);
        getActivity().setTitle("My Attractors");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabaseHelper = new DatabaseHelper(getActivity(), voidTable);
        mListView = (ListView) view.findViewById(R.id.voidsList);
        populateListView();

    }

    private void populateListView() {
        attractorArray = new ArrayList<Attractor>();
        Cursor data = mDatabaseHelper.getData(voidTable);

        while(data.moveToNext()){
            Attractor resultRow = new Attractor();

            resultRow.id = data.getString(0);
            resultRow.GID = data.getString(1);
            resultRow.TID = data.getString(2);
            resultRow.LID = data.getString(3);
            resultRow.x_ = data.getString(4);
            resultRow.y_ = data.getString(5);
            resultRow.distance = data.getString(6);
            resultRow.initialBearing = data.getString(7);
            resultRow.finalBearing = data.getString(8);
            resultRow.side = data.getString(9);
            resultRow.distanceErr = data.getString(10);
            resultRow.radiusM = data.getString(11);
            resultRow.N = data.getString(12);
            resultRow.mean = data.getString(13);
            resultRow.rarity = data.getString(14);
            resultRow.power_old = data.getString(15);
            resultRow.probability_single = data.getString(16);
            resultRow.integral_score = data.getString(17);
            resultRow.significance = data.getString(18);
            resultRow.probability = data.getString(19);
            resultRow.FILTERING_SIGNIFICANCE = data.getString(20);
            resultRow.type = data.getString(21);
            resultRow.radiusm = data.getString(22);
            resultRow.power = data.getString(23);
            resultRow.z_score = data.getString(24);
            resultRow.latitude = data.getString(25);
            resultRow.longitude = data.getString(26);
            resultRow.pseudo = data.getString(27);
            resultRow.report = data.getString(28);

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

            String type = "Void " +  getItem(position).getId();

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

            final Button button = (Button) convertView.findViewById(R.id.reportButtonInList);
            if(Double.valueOf(getItem(position).getReport()) == 1){
                button.setText("Reported");
                button.setPressed(true);
                button.setEnabled(false);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setReportAlertDialog(position, button);

                }
            });

            Button showButton = (Button) convertView.findViewById(R.id.showButtonInList);
            showButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setShowAlertDialog(Integer.parseInt(getItem(position).getId()),
                            Double.valueOf(getItem(position).getPower()),
                            Double.valueOf(getItem(position).getLatitude()),
                            Double.valueOf(getItem(position).getLongitude()),
                            Double.valueOf(getItem(position).getRadiusm()),
                            Double.valueOf(getItem(position).getZ_score()),
                            Double.valueOf(getItem(position).getPseudo()));

                }
            });

            return convertView;
        }

        public void setReportAlertDialog(final int position, final Button showButton) {
            reportDialog = new Dialog(getActivity());
            final ArrayList<String> ar = new ArrayList<String>();
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
                    rQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));

                    if(currentQeustion[0] == 3){
                        ar.add("1");
                        reportDialogWindow(currentQeustion, position, showButton, ar);
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");

                    } else {
                        currentQeustion[0]++;
                        ar.add("1");
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                    }
                }});

            //Button listener for no
            noAnwserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    rQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                    if(currentQeustion[0] == 3){
                        ar.add("0");
                        reportDialog.setContentView(R.layout.dialog_qeustionreportwindow);
                        reportDialogWindow(currentQeustion, position, showButton, ar);
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                    } else {
                        currentQeustion[0]++;
                        ar.add("0");
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                    }
                }});


            reportDialog.show();
        }

        public void reportDialogWindow(final int[] currentQeustion, final int position, final Button showButton, final ArrayList<String> ar){
            Log.d("Test", ""+ar);
            reportDialog.setContentView(R.layout.dialog_qeustionreportwindow);
            final ReportQuestions rReportQuestions = new ReportQuestions();
            final TextView wQuestionView;

            final Button windowButton1 = (Button) reportDialog.findViewById(R.id.windowButton1);
            final Button windowButton2 = (Button) reportDialog.findViewById(R.id.windowButton2);
            final Button windowButton3 = (Button) reportDialog.findViewById(R.id.windowButton3);
            final Button windowButton4 = (Button) reportDialog.findViewById(R.id.windowButton4);
            final Button windowButton5 = (Button) reportDialog.findViewById(R.id.windowButton5);
            final Button windowButton6 = (Button) reportDialog.findViewById(R.id.windowButton6);
            final TextView qeustionViewScore;
            wQuestionView = (TextView) reportDialog.findViewById(R.id.wQuestionView);
            qeustionViewScore = (TextView) reportDialog.findViewById(R.id.qeustionView2);


            qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
            //Button listener for windowButton1
            windowButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentQeustion[0]++;

                    if(currentQeustion[0] == 8){
                        ar.add(windowButton1.getText().toString());
                        reportDialogInput(position, showButton, ar);
                    } else {
                        ar.add(windowButton1.getText().toString());
                        wQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                        updateAnwserButtons(windowButton1, windowButton2, windowButton3, windowButton4, windowButton5, windowButton6, currentQeustion, rReportQuestions);
                        qeustionViewScore.setText("Question " + (currentQeustion[0] + 1) + "/8");
                        Log.d("Test", ""+ar);
                    }
                }
            });
            //Button listener for windowButton1
            windowButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentQeustion[0]++;
                    if(currentQeustion[0] == 8){
                        ar.add(windowButton2.getText().toString());
                        reportDialogInput(position, showButton, ar);
                    } else {
                        ar.add(windowButton2.getText().toString());
                        wQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                        updateAnwserButtons(windowButton1, windowButton2, windowButton3, windowButton4, windowButton5, windowButton6, currentQeustion, rReportQuestions);
                        qeustionViewScore.setText("Question " + (currentQeustion[0] + 1) + "/8");
                    }
                }
            });
            //Button listener for windowButton1
            windowButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentQeustion[0]++;
                    if(currentQeustion[0] == 8){
                        ar.add(windowButton3.getText().toString());
                        reportDialogInput(position, showButton, ar);
                    } else {
                        ar.add(windowButton3.getText().toString());
                        wQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                        updateAnwserButtons(windowButton1, windowButton2, windowButton3, windowButton4, windowButton5, windowButton6, currentQeustion,rReportQuestions);
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                    }
                }

            });
            //Button listener for windowButton1
            windowButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentQeustion[0]++;
                    if(currentQeustion[0] == 8){
                        ar.add(windowButton4.getText().toString());
                        reportDialogInput(position, showButton, ar);
                    } else {
                        ar.add(windowButton4.getText().toString());
                        wQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                        updateAnwserButtons(windowButton1, windowButton2, windowButton3, windowButton4, windowButton5, windowButton6, currentQeustion,rReportQuestions);
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                    }
                }
            });
            //Button listener for windowButton1
            windowButton5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentQeustion[0]++;
                    if(currentQeustion[0] == 8){
                        ar.add(windowButton5.getText().toString());
                        reportDialogInput(position, showButton, ar);
                    } else {
                        ar.add(windowButton5.getText().toString());
                        wQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                        updateAnwserButtons(windowButton1, windowButton2, windowButton3, windowButton4, windowButton5, windowButton6, currentQeustion,rReportQuestions);
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                    }
                }

            });
            //Button listener for windowButton1
            windowButton6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentQeustion[0]++;
                    if(currentQeustion[0] == 8){
                        ar.add(rReportQuestions.getbutton2Anwser(5));
                        reportDialogInput(position, showButton, ar);
                    } else {
                        ar.add(windowButton6.getText().toString());
                        wQuestionView.setText(rReportQuestions.getQuestion(currentQeustion[0]));
                        updateAnwserButtons(windowButton1, windowButton2, windowButton3, windowButton4, windowButton5, windowButton6, currentQeustion,rReportQuestions);
                        qeustionViewScore.setText("Question "+(currentQeustion[0]+1)+"/8");
                    }
                }

            });

        }

        public void reportDialogInput(final int position, final Button showButton, final ArrayList<String> ar){
            reportDialog.setContentView(R.layout.dialog_textinput);
            final JSONObject obj = new JSONObject();
            Log.d("Test", ""+ar);
            final ReportQuestions rReportQuestions = new ReportQuestions();
            final EditText userInput = (EditText) reportDialog.findViewById(R.id.editText2);
            final Button sendReportButton = (Button) reportDialog.findViewById(R.id.sendReportButton);
            final Button cancelReportButton = (Button) reportDialog.findViewById(R.id.cancelReportButton);

            //Button listener for windowButton1
            sendReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ar.add(userInput.getText().toString());
                    try {
                        //General
                        obj.put("user_id", "132");
                        obj.put("platform", "8");
                        obj.put("datetime", "datetime");

                        //Question anwsers
                        obj.put("visited", ar.get(0));
                        obj.put("point_type", "Attractor");
                        obj.put("intent_set", ar.get(1));
                        obj.put("artifact_collected", ar.get(2));
                        obj.put("fucking_amazing", ar.get(3));
                        obj.put("rating_meaningfulness", ar.get(4));
                        obj.put("rating_emotional", ar.get(5));
                        obj.put("rating_importance", ar.get(6));
                        obj.put("rating_strangeness", ar.get(7));
                        obj.put("rating_synchroncity", ar.get(8));
                        obj.put("text", ar.get(9));

                        //Attractor
                        obj.put("short_hash_id", "132");
                        obj.put("gid", getItem(position).getGID());
                        obj.put("tid", getItem(position).getTID());
                        obj.put("lid", getItem(position).getLID());
                        obj.put("type", getItem(position).getType());
                        obj.put("x", getItem(position).getX_());
                        obj.put("y", getItem(position).getY_());
                        obj.put("latitude", getItem(position).getLatitude());
                        obj.put("longitude", getItem(position).getLongitude());
                        obj.put("distance", getItem(position).getDistance());
                        obj.put("initial_bearing", getItem(position).getInitialBearing());
                        obj.put("final_bearing", getItem(position).getFinalBearing());
                        obj.put("side", getItem(position).getSide());
                        obj.put("distance_err", getItem(position).getDistanceErr());
                        obj.put("radiusM", getItem(position).getRadiusM());
                        obj.put("n", getItem(position).getN());
                        obj.put("mean", getItem(position).getMean());
                        obj.put("rarity", getItem(position).getRarity());
                        obj.put("power_old", getItem(position).getPower_old());
                        obj.put("power", getItem(position).getPower());
                        obj.put("z_score", getItem(position).getZ_score());
                        obj.put("probability_single", getItem(position).getProbability_single());
                        obj.put("integral_score", getItem(position).getIntegral_score());
                        obj.put("significance", getItem(position).getSignificance());
                        obj.put("probability", getItem(position).getProbability());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mDatabaseHelper.setReport("Attractors", Integer.valueOf(getItem(position).getId()));
                    //post req here

                    Log.d("Test", ""+ar);
                    showButton.setVisibility(view.GONE);
                    reportDialog.cancel();
                }
            });

            //Button listener for windowButton1
            cancelReportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reportDialog.cancel();

                }

            });


        }


    }

    public void updateAnwserButtons(Button windowButton1, Button windowButton2, Button windowButton3,
                                    Button windowButton4, Button windowButton5, Button windowButton6,
                                    final int[] currentQeustion, final ReportQuestions rReportQuestions ){
        if(currentQeustion[0] == 4){
            windowButton1.setText(rReportQuestions.getbutton2Anwser(0));
            windowButton2.setText(rReportQuestions.getbutton2Anwser(1));
            windowButton3.setText(rReportQuestions.getbutton2Anwser(2));
            windowButton4.setText(rReportQuestions.getbutton2Anwser(3));
            windowButton5.setText(rReportQuestions.getbutton2Anwser(4));
            windowButton6.setText(rReportQuestions.getbutton2Anwser(5));
        }

        if(currentQeustion[0] == 5){
            windowButton1.setText(rReportQuestions.getbutton3Anwser(0));
            windowButton2.setText(rReportQuestions.getbutton3Anwser(1));
            windowButton3.setText(rReportQuestions.getbutton3Anwser(2));
            windowButton4.setText(rReportQuestions.getbutton3Anwser(3));
        }
        if(currentQeustion[0] == 6){
            windowButton1.setText(rReportQuestions.getbutton4Anwser(0));
            windowButton2.setText(rReportQuestions.getbutton4Anwser(1));
            windowButton3.setText(rReportQuestions.getbutton4Anwser(2));
            windowButton4.setText(rReportQuestions.getbutton4Anwser(3));
        }
        if(currentQeustion[0] == 7){
            windowButton1.setText(rReportQuestions.getbutton5Anwser(0));
            windowButton2.setText(rReportQuestions.getbutton5Anwser(1));
            windowButton3.setText(rReportQuestions.getbutton5Anwser(2));
            windowButton4.setText(rReportQuestions.getbutton5Anwser(3));
            windowButton5.setText(rReportQuestions.getbutton5Anwser(4));

        }




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

    interface SendMessage {
        void sendData(int type, double power, double x, double y, double radiusm, double z_score, double pseudo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (MyAttractorsListFragment.SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

}





