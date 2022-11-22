package com.magiri.animalcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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
    String[] generalSymptoms;
    List<CheckBox> checkBoxList;
    LinearLayout generalLayout,gitLayout,circulatoryLayout,nerveousLayout,udderLayout,respiratoryLayout,skinLayout;
    CheckBox[] checkBoxes;
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
        gitLayout=findViewById(R.id.gitSystem);
        respiratoryLayout=findViewById(R.id.respiratoryLayout);
        circulatoryLayout=findViewById(R.id.circulatoryLayout);
        nerveousLayout=findViewById(R.id.nerveousLayout);
        udderLayout=findViewById(R.id.udderLayout);
        skinLayout=findViewById(R.id.skinLayout);
        symptomList=new ArrayList();
        checkBoxList=new ArrayList<>();

//        generalSymptoms=getResources().getStringArray(R.array.general_symptom);
//        checkBoxes=new CheckBox[generalSymptoms.length];
//        for(int n=0;n<generalSymptoms.length;n++){
//            String symptom=generalSymptoms[n];
//            checkBoxes[n]=new CheckBox(getBaseContext());
//            generalLayout.addView(checkBoxes[n]);
//            checkBoxes[n].setText(symptom);
//            checkBoxes[n].setId(n);
//            checkBoxes[n].setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    CheckBox checkBox=(CheckBox) v;
//                    int checkboxID=checkBox.getId();
//                    if(checkBox.isChecked()){
//                        symptomList.add(generalSymptoms[checkboxID]);
//                    }else{
//                        symptomList.remove(checkboxID);
//                    }
//                }
//            });
//        }


        diagnoseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCheckedSymptom();
            }
        });
    }

    private void getCheckedSymptom() {

    }

    public void onCheckClicked(View view){
        boolean checked=((CheckBox) view).isChecked();
        switch (view.getId()){

        }
    }

}