package com.magiri.animalcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class Diagnose extends AppCompatActivity {
    MaterialToolbar diagnoseMaterialToolBar;
    private Button diagnoseBtn;
    List symptomList;
    String[] generalSymptoms,gitSystemSymptoms,respiratorySymptoms,nerveousSymptoms,udderSymptoms,skinSymptoms,circulatorySymptoms;
    List<CheckBox> checkBoxList;
    LinearLayout generalLayout,gitLayout,circulatoryLayout,nerveousLayout,udderLayout,respiratoryLayout,skinLayout;
    CheckBox[] checkBoxes,skinCheckBoxes,gitCheckBoxes,respiratoryCheckBoxes,circulatoryCheckBoxes,nerveousCheckBoxes,udderCheckBoxes;
    CheckBox consciousness,dullnessCheckBox,salivationCheckBox,FrothingCheckBox,skinNodulesCheckBox,staggeringCheckBox,mobilityCheckBox,bodyStiffensCheckBox;
    CheckBox productionCheckBox,swollenNodesCheckBox,lamenessCheckBox,muscleWeaknessCheckBox,abortionCheckBox,vomitingCheckBox;
    CheckBox appetite,secretion,Diarrhea,weight,weakness,cough,discharge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose);
        diagnoseMaterialToolBar=findViewById(R.id.diagnoseMaterialToolBar);
        diagnoseBtn=findViewById(R.id.diagnoseBtn);
        generalLayout=findViewById(R.id.generalSymptomLayout);
        gitLayout=findViewById(R.id.gitSystemLayout);
        respiratoryLayout=findViewById(R.id.respiratoryLayout);
        circulatoryLayout=findViewById(R.id.circulatoryLayout);
        nerveousLayout=findViewById(R.id.nerveousLayout);
        udderLayout=findViewById(R.id.udderLayout);
        skinLayout=findViewById(R.id.skinLayout);
        symptomList=new ArrayList();
        checkBoxList=new ArrayList<>();

        generalSymptoms=getResources().getStringArray(R.array.general_symptom);
        gitSystemSymptoms=getResources().getStringArray(R.array.git_symptoms);
        circulatorySymptoms=getResources().getStringArray(R.array.circulatory_symptoms);
        udderSymptoms=getResources().getStringArray(R.array.udder_symptoms);
        respiratorySymptoms=getResources().getStringArray(R.array.respiratory_symptoms);
        nerveousSymptoms=getResources().getStringArray(R.array.nerveous_symptoms);
        skinSymptoms=getResources().getStringArray(R.array.skin_symptoms);

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
                List checkedSymptoms=symptomList;
                diagnoseDiseaseFromSymptoms(checkedSymptoms);
            }
        });
    }

    private void diagnoseDiseaseFromSymptoms(List checkedSymptoms) {
        //make a post request to diagnose disease through naivebayes classifier

    }


}