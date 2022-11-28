package com.magiri.animalcare;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Health extends Fragment {
    RecyclerView healthRecyclerView;
    private String AnimalID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_health, container, false);
        healthRecyclerView=view.findViewById(R.id.healthRecyclerView);
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }
        getAnimalHealthRecord();
        return view;
    }

    private void getAnimalHealthRecord() {

    }
}