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
import com.magiri.animalcare.Adapters.BreedingAdapter;
import com.magiri.animalcare.Model.Breeding;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class BreedingFragment extends Fragment {
    private static final String TAG = "BreedingFragment";
    RecyclerView breedingRecyclerView;
    private String AnimalID;
    private DatabaseReference mRef;
    BreedingAdapter breedingAdapter;
    List<Breeding> breedingList;
    String FarmerID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_breeding, container, false);
        breedingRecyclerView=view.findViewById(R.id.breedIngRecyclerView);
        breedingList=new ArrayList<>();
        breedingAdapter=new BreedingAdapter(getActivity(),breedingList);
        breedingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        breedingRecyclerView.setHasFixedSize(true);
        breedingRecyclerView.setAdapter(breedingAdapter);
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        mRef= FirebaseDatabase.getInstance().getReference("Breeding").child(FarmerID);
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }
        getAnimalBreedingRecord();
        return view;
    }

    private void getAnimalBreedingRecord() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Breeding breedingRecord=dataSnapshot.getValue(Breeding.class);
                    if(breedingRecord.getAnimalID().equals(AnimalID)){
                        breedingList.add(breedingRecord);
                    }
                }
                breedingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}