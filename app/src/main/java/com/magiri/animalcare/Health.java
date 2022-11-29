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
import com.magiri.animalcare.Adapters.TreatmentAdapter;
import com.magiri.animalcare.Model.DiseaseTreatment;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class Health extends Fragment {
    private static final String TAG = "Health";
    RecyclerView healthRecyclerView;
    private String AnimalID;
    private DatabaseReference mRef;
    List<DiseaseTreatment> diseaseTreatmentList;
    TreatmentAdapter treatmentAdapter;
    String FarmerID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_health, container, false);
        healthRecyclerView=view.findViewById(R.id.healthRecyclerView);
        healthRecyclerView.setHasFixedSize(true);
        diseaseTreatmentList=new ArrayList<>();
        healthRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        treatmentAdapter=new TreatmentAdapter(getActivity(),diseaseTreatmentList);
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        mRef= FirebaseDatabase.getInstance().getReference("Health_Record").child(FarmerID);
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }
        getAnimalHealthRecord();
        return view;
    }

    private void getAnimalHealthRecord() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    DiseaseTreatment diseaseTreatment=dataSnapshot.getValue(DiseaseTreatment.class);
                    assert diseaseTreatment != null;
                    if(diseaseTreatment.getAnimalID().equals(AnimalID)){
                        diseaseTreatmentList.add(diseaseTreatment);
                    }
                }
                treatmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}