package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.VetsAroundAdapter;
import com.magiri.animalcare.Model.Veterinarian;

import java.util.ArrayList;
import java.util.List;

public class Vets_Around extends AppCompatActivity {
    private static final String TAG = "Vets Around";
    private RecyclerView vetsAroundRecyclerView;
    List<Veterinarian> veterinarianList;
    VetsAroundAdapter vetsAroundAdapter;
    private DatabaseReference mRef;
    private static final int MAP_REQUEST_CODE=123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vets_around);

        vetsAroundRecyclerView=findViewById(R.id.vetsAroundRecyclerView);
        veterinarianList=new ArrayList<>();
        vetsAroundAdapter=new VetsAroundAdapter(this,veterinarianList);

        vetsAroundRecyclerView.setHasFixedSize(true);
        vetsAroundRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        vetsAroundRecyclerView.setAdapter(vetsAroundAdapter);
        mRef= FirebaseDatabase.getInstance().getReference();

        fetchVets();

    }

    private void fetchVets() {
        final DatabaseReference ref;
        ref=mRef.child("Veterinarian");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Veterinarian vet=dataSnapshot.getValue(Veterinarian.class);
                    veterinarianList.add(vet);
                }
                vetsAroundAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            String locationAddress=data.getStringExtra(SimplePlacePicker.SELECTED_ADDRESS);
            String latitude=String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA,-1));
            String longitude=String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA,-1));
            vetsAroundAdapter.UpdateLocation(locationAddress,latitude,longitude);
        }
    }



}