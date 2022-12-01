package com.magiri.animalcare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.MilkRecordAdapter;
import com.magiri.animalcare.Model.MilkRecord;
import com.magiri.animalcare.Session.Prevalent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Production extends Fragment {
    private static final String TAG = "MilkProduction";
    RecyclerView productionRecyclerView;
    private DatabaseReference mRef;
    private String AnimalID,FarmerID;
    List<MilkRecord> milkRecordList;
    private MilkRecordAdapter milkRecordAdapter;
    private RadioGroup milkFilterRadioGroup;
    private RadioButton todayRadioBtn,monthRadioBtn,allRadioBtn;
    private TextView totalProductionTxt;
    SimpleDateFormat sdf;
    String str;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_production, container, false);
        todayRadioBtn=view.findViewById(R.id.currentDayRadioBtn);
        monthRadioBtn=view.findViewById(R.id.currentMonthRadioBtn);
        allRadioBtn=view.findViewById(R.id.totalRadioBtn);
        milkFilterRadioGroup=view.findViewById(R.id.milkRecordFilter);
        totalProductionTxt=view.findViewById(R.id.totalMilkTxt);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar obj = Calendar.getInstance();
        str = sdf.format(obj.getTime());
//        productionRecyclerView=view.findViewById(R.id.productionRecyclerView);
//        productionRecyclerView.setHasFixedSize(true);
//        productionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        mRef= FirebaseDatabase.getInstance().getReference("MilkProduction").child(FarmerID);
        milkRecordList=new ArrayList<>();
        milkRecordAdapter=new MilkRecordAdapter(getActivity(),milkRecordList);
//        productionRecyclerView.setAdapter(milkRecordAdapter);
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }
        milkFilterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.currentDayRadioBtn:
                        getMilkProductionProduction("Today");
                        break;
                    case R.id.currentMonthRadioBtn:
                        getMilkProductionProduction("This Month");
                        break;
                    case R.id.totalRadioBtn:
                        getAnimalMilkProduction();
                        break;
                }
            }
        });
        getAnimalMilkProduction();
        return view;
    }

    private void getMilkProductionProduction(String constraint) {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalMilkProduction=0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MilkRecord milkRecord=dataSnapshot.getValue(MilkRecord.class);
                    if(milkRecord.getAnimalName().equals(AnimalID)){
//                        milkRecordList.add(milkRecord);
                        String milkingDate=milkRecord.getDate();
                        if(constraint.equals("Today")){
                            try {
                                Date date1=sdf.parse(milkingDate);
                                Date date2=sdf.parse(str);
                                int result=date2.compareTo(date1);
                                if(result==0){
                                    int currentDayProduction=milkRecord.getQuantity();
                                    totalMilkProduction+=currentDayProduction;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else if(constraint.equals("This Month")){
                            Calendar cal1=Calendar.getInstance();
                            Calendar cal2=Calendar.getInstance();
                            try {
                                Date date1=sdf.parse(milkingDate);
                                Date date2=sdf.parse(str);
                                cal1.setTime(date1);
                                cal2.setTime(date2);
                                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                                    if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                                        // the date falls in current month
                                        int currentDayProduction=milkRecord.getQuantity();
                                        totalMilkProduction+=currentDayProduction;
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                totalProductionTxt.setText(totalMilkProduction+" L");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void getAnimalMilkProduction() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalMilkProduction=0;
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MilkRecord milkRecord=dataSnapshot.getValue(MilkRecord.class);
                    if(milkRecord.getAnimalName().equals(AnimalID)){
//                        milkRecordList.add(milkRecord);
                        int currentDayProduction=milkRecord.getQuantity();
                        totalMilkProduction+=currentDayProduction;
                    }
                }
                totalProductionTxt.setText(totalMilkProduction+" L");
//                milkRecordAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }


}