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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.MilkRecordAdapter;
import com.magiri.animalcare.Model.MilkRecord;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class Production extends Fragment {
    private static final String TAG = "MilkProduction";
    RecyclerView productionRecyclerView;
    private DatabaseReference mRef;
    private String AnimalID,FarmerID;
    List<MilkRecord> milkRecordList;
    private MilkRecordAdapter milkRecordAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_production, container, false);
        productionRecyclerView=view.findViewById(R.id.productionRecyclerView);
        productionRecyclerView.setHasFixedSize(true);
        productionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        mRef= FirebaseDatabase.getInstance().getReference("MilkProduction").child(FarmerID);
        milkRecordList=new ArrayList<>();
        milkRecordAdapter=new MilkRecordAdapter(getActivity(),milkRecordList);
        productionRecyclerView.setAdapter(milkRecordAdapter);
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }
        getAnimalMilkProduction();
        return view;
    }

    private void getAnimalMilkProduction() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MilkRecord milkRecord=dataSnapshot.getValue(MilkRecord.class);
                    if(milkRecord.getAnimalName().equals(AnimalID)){
                        milkRecordList.add(milkRecord);
                    }
                }
                milkRecordAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}