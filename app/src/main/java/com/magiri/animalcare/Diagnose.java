package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magiri.animalcare.Model.DiseaseDiagnosis;
import com.magiri.animalcare.darajaApi.RestClient;
import com.magiri.animalcare.darajaApi.STKResponse;
import com.magiri.animalcare.darajaApi.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Diagnose extends AppCompatActivity {
    private static final String TAG = "Diagnose";
    MaterialToolbar diagnoseMaterialToolBar;
    private Button diagnoseBtn;
    List<String> symptomList;
    String[] generalSymptoms,gitSystemSymptoms,respiratorySymptoms,nerveousSymptoms,udderSymptoms,skinSymptoms,circulatorySymptoms;
    List<CheckBox> checkBoxList;
    LinearLayout generalLayout,gitLayout,circulatoryLayout,nerveousLayout,udderLayout,respiratoryLayout,skinLayout;
    CheckBox[] checkBoxes,skinCheckBoxes,gitCheckBoxes,respiratoryCheckBoxes,circulatoryCheckBoxes,nerveousCheckBoxes,udderCheckBoxes;
    ProgressDialog progressDialog;
    String VisitID;
    private DatabaseReference mRef;
    AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose);
        diagnoseMaterialToolBar=findViewById(R.id.diagnoseMaterialToolBar);
        generalLayout=findViewById(R.id.generalSymptomLayout);
        gitLayout=findViewById(R.id.gitSystemLayout);
        respiratoryLayout=findViewById(R.id.respiratoryLayout);
        circulatoryLayout=findViewById(R.id.circulatoryLayout);
        nerveousLayout=findViewById(R.id.nerveousLayout);
        udderLayout=findViewById(R.id.udderLayout);
        skinLayout=findViewById(R.id.skinLayout);
        checkBoxList=new ArrayList<>();
        symptomList=new ArrayList<>();
        progressDialog=new ProgressDialog(Diagnose.this);
        progressDialog.setTitle("Diagnose Disease");
        progressDialog.setMessage("Diagnosing Disease From Symptoms");
        progressDialog.setCanceledOnTouchOutside(false);
        diagnoseBtn=findViewById(R.id.diagnoseBtn);

        Bundle bundle=getIntent().getExtras();
        VisitID=bundle.getString("VISITID");
        generalSymptoms=getResources().getStringArray(R.array.general_symptom);
        gitSystemSymptoms=getResources().getStringArray(R.array.git_symptoms);
        circulatorySymptoms=getResources().getStringArray(R.array.circulatory_symptoms);
        udderSymptoms=getResources().getStringArray(R.array.udder_symptoms);
        respiratorySymptoms=getResources().getStringArray(R.array.respiratory_symptoms);
        nerveousSymptoms=getResources().getStringArray(R.array.nerveous_symptoms);
        skinSymptoms=getResources().getStringArray(R.array.skin_symptoms);
        mRef= FirebaseDatabase.getInstance().getReference("VisitRequest");

        //dynamically add general symptoms
        checkBoxes=new CheckBox[generalSymptoms.length];
        for(int n=0;n<generalSymptoms.length;n++){
            String symptom=generalSymptoms[n];
            checkBoxes[n]=new CheckBox(getBaseContext());
            generalLayout.addView(checkBoxes[n]);
            checkBoxes[n].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkBoxes[n].setText(symptom);
            checkBoxes[n].setId(n);
            checkBoxes[n].setTextSize(18);
            checkBoxes[n].setPadding(15,15,8,15);
            checkBoxes[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox) v;
                    int checkboxID=checkBox.getId();
                    if(checkBox.isChecked()){
                        symptomList.add(generalSymptoms[checkboxID]);
                    }else{
                        symptomList.remove(generalSymptoms[checkboxID]);

                    }
                }
            });
        }

        //dynamically add udder symptoms
        udderCheckBoxes=new CheckBox[udderSymptoms.length];
        for(int n=0;n<udderSymptoms.length;n++){
            String symptom=udderSymptoms[n];
            udderCheckBoxes[n]=new CheckBox(getBaseContext());
            udderLayout.addView(udderCheckBoxes[n]);
            udderCheckBoxes[n].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            udderCheckBoxes[n].setText(symptom);
            udderCheckBoxes[n].setId(n);
            udderCheckBoxes[n].setTextSize(18);
            udderCheckBoxes[n].setPadding(15,15,8,15);
            udderCheckBoxes[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox) v;
                    int checkboxID=checkBox.getId();
                    if(checkBox.isChecked()){
                        symptomList.add(udderSymptoms[checkboxID]);
                    }else{
                        symptomList.remove(udderSymptoms[checkboxID]);

                    }
                }
            });
        }

        //dynamically add respiratory Symptoms
        respiratoryCheckBoxes=new CheckBox[respiratorySymptoms.length];
        for(int n=0;n<respiratorySymptoms.length;n++){
            String symptom=respiratorySymptoms[n];
            respiratoryCheckBoxes[n]=new CheckBox(getBaseContext());
            respiratoryLayout.addView(respiratoryCheckBoxes[n]);
            respiratoryCheckBoxes[n].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            respiratoryCheckBoxes[n].setText(symptom);
            respiratoryCheckBoxes[n].setId(n);
            respiratoryCheckBoxes[n].setTextSize(18);
            respiratoryCheckBoxes[n].setPadding(15,15,8,15);
            respiratoryCheckBoxes[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox) v;
                    int checkboxID=checkBox.getId();
                    if(checkBox.isChecked()){
                        symptomList.add(respiratorySymptoms[checkboxID]);
                    }else{
                        symptomList.remove(respiratorySymptoms[checkboxID]);

                    }
                }
            });
        }

        //dynamically add circulatory Symptoms
        circulatoryCheckBoxes=new CheckBox[circulatorySymptoms.length];
        for(int n=0;n<circulatorySymptoms.length;n++){
            String symptom=circulatorySymptoms[n];
            circulatoryCheckBoxes[n]=new CheckBox(getBaseContext());
            circulatoryLayout.addView(circulatoryCheckBoxes[n]);
            circulatoryCheckBoxes[n].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circulatoryCheckBoxes[n].setText(symptom);
            circulatoryCheckBoxes[n].setId(n);
            circulatoryCheckBoxes[n].setTextSize(18);
            circulatoryCheckBoxes[n].setPadding(15,15,8,15);
            circulatoryCheckBoxes[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox) v;
                    int checkboxID=checkBox.getId();
                    if(checkBox.isChecked()){
                        symptomList.add(circulatorySymptoms[checkboxID]);
                    }else{
                        symptomList.remove(circulatorySymptoms[checkboxID]);

                    }
                }
            });
        }

        //dynamically add nerveous Symptoms
        nerveousCheckBoxes=new CheckBox[nerveousSymptoms.length];
        for(int n=0;n<nerveousSymptoms.length;n++){
            String symptom=nerveousSymptoms[n];
            nerveousCheckBoxes[n]=new CheckBox(getBaseContext());
            nerveousLayout.addView(nerveousCheckBoxes[n]);
            nerveousCheckBoxes[n].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            nerveousCheckBoxes[n].setText(symptom);
            nerveousCheckBoxes[n].setId(n);
            nerveousCheckBoxes[n].setTextSize(18);
            nerveousCheckBoxes[n].setPadding(15,15,8,15);
            nerveousCheckBoxes[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox) v;
                    int checkboxID=checkBox.getId();
                    if(checkBox.isChecked()){
                        symptomList.add(nerveousSymptoms[checkboxID]);
                    }else{
                        symptomList.remove(nerveousSymptoms[checkboxID]);

                    }
                }
            });
        }

        //dynamically add skin Symptoms
        skinCheckBoxes=new CheckBox[skinSymptoms.length];
        for(int n=0;n<skinSymptoms.length;n++){
            String symptom=skinSymptoms[n];
            skinCheckBoxes[n]=new CheckBox(getBaseContext());
            skinLayout.addView(skinCheckBoxes[n]);
            skinCheckBoxes[n].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            skinCheckBoxes[n].setText(symptom);
            skinCheckBoxes[n].setId(n);
            skinCheckBoxes[n].setTextSize(18);
            skinCheckBoxes[n].setPadding(15,15,8,15);
            skinCheckBoxes[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox) v;
                    int checkboxID=checkBox.getId();
                    if(checkBox.isChecked()){
                        symptomList.add(skinSymptoms[checkboxID]);
                    }else{
                        symptomList.remove(skinSymptoms[checkboxID]);
                    }
                }
            });
        }

        //dynamically add git Symptoms
        gitCheckBoxes=new CheckBox[gitSystemSymptoms.length];
        for(int n=0;n<gitSystemSymptoms.length;n++){
            String symptom=gitSystemSymptoms[n];
            gitCheckBoxes[n]=new CheckBox(getBaseContext());
            gitLayout.addView(gitCheckBoxes[n]);
            gitCheckBoxes[n].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            gitCheckBoxes[n].setText(symptom);
            gitCheckBoxes[n].setId(n);
            gitCheckBoxes[n].setTextSize(18);
            gitCheckBoxes[n].setPadding(15,15,8,15);
            gitCheckBoxes[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox=(CheckBox) v;
                    int checkboxID=checkBox.getId();
                    if(checkBox.isChecked()){
                        symptomList.add(gitSystemSymptoms[checkboxID]);
                    }else{
                        symptomList.remove(gitSystemSymptoms[checkboxID]);

                    }
                }
            });
        }

        diagnoseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(symptomList.size()>0){
                    progressDialog.show();
                    diagnoseDiseaseFromSymptoms(symptomList);
                }
                else{
                    Toast.makeText(Diagnose.this, "Please Check Symptom occurring on your Livestock", Toast.LENGTH_SHORT).show();
                }
            }
        });
        diagnoseMaterialToolBar.setNavigationOnClickListener(v -> finish());
    }

    public void diagnoseDiseaseFromSymptoms(List<String> checkedSymptoms) {
        //make a post request to diagnose disease through naivebayes classifier
        String Symptoms="";
        for(String s:checkedSymptoms){
            Symptoms+=s+"n";
        }
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://19a4-41-89-227-170.eu.ngrok.io")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();
        RestClient restClient=retrofit.create(RestClient.class);
        Call<DiseaseDiagnosis> call=restClient.diagnoseDisease(Symptoms);

        call.enqueue(new Callback<DiseaseDiagnosis>() {
            @Override
            public void onResponse(@NonNull Call<DiseaseDiagnosis> call, @NonNull Response<DiseaseDiagnosis> response) {
                assert response.body() != null;
                String disease= String.valueOf(response.body().getDisease());
                int prob=response.body().getCertaintyFactor();
                HashMap<String,Object> map=new HashMap<>();
                map.put("Disease",disease);
                map.put("Certainty_Factor",prob);
                Log.i(TAG, "onResponse: "+disease);
                showAlertDialog(disease,prob);
                if(VisitID!=null){
                    mRef.child(VisitID).child("Diagnosis").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG, "onComplete: Saved Diagnosis ");
                            }
                        }
                    });
                }
                Toast.makeText(getApplicationContext(), "Disease Diagnosed "+disease, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<DiseaseDiagnosis> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(),"Could Not Diagnose Disease",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void showAlertDialog(String disease, int prob) {
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(Diagnose.this);
        LayoutInflater layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.diagnosis_alert_dialog,null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        TextView diseaseTextView=view.findViewById(R.id.diseaseTxt);
        TextView factorTextVIew=view.findViewById(R.id.certaintyTxt);
        Button successBtn=view.findViewById(R.id.okeyBtn);
        factorTextVIew.setText(String.valueOf(prob));
        diseaseTextView.setText(disease);
        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                finish();
            }
        });
        alert=alertDialogBuilder.create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.show();
    }


}