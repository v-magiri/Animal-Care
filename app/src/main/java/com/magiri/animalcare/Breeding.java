package com.magiri.animalcare;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Breeding extends Fragment {
    RecyclerView breedingRecyclerView;
    private String AnimalID;
    private DatabaseReference mRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_breeding, container, false);
        breedingRecyclerView=view.findViewById(R.id.breedIngRecyclerView);
        mRef= FirebaseDatabase.getInstance().getReference("Breeding");
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }
        getAnimalBreedingRecord();
        return view;
    }

    private void getAnimalBreedingRecord() {
    }
}