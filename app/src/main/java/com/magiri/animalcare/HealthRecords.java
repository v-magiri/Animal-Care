package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.AnimalHealthRecordAdapter;
import com.magiri.animalcare.Adapters.TreatmentAdapter;
import com.magiri.animalcare.Model.Breeding;
import com.magiri.animalcare.Model.DiseaseTreatment;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class HealthRecords extends AppCompatActivity {
    private static final String TAG = "HealthRecords";
    List<DiseaseTreatment> treatmentList;
    RecyclerView healthRecyclerView;
    AnimalHealthRecordAdapter treatmentAdapter;
    DatabaseReference mRef;
    String FarmerID;
    private MaterialToolbar materialToolbar;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_records);
        materialToolbar=findViewById(R.id.healthMaterialToolBar);
        setSupportActionBar(materialToolbar);
        healthRecyclerView=findViewById(R.id.healthRecordRecyclerView);
        healthRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        healthRecyclerView.setHasFixedSize(true);
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        treatmentList=new ArrayList<>();
        mRef= FirebaseDatabase.getInstance().getReference("Health_Record").child(FarmerID);

        progressDialog.setMessage("Loading Health Records");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        getTreatmentRecord();
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getTreatmentRecord() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                treatmentList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    DiseaseTreatment diseaseTreatmentRecord=dataSnapshot.getValue(DiseaseTreatment.class);
                    treatmentList.add(diseaseTreatmentRecord);
                    treatmentAdapter=new AnimalHealthRecordAdapter(HealthRecords.this,treatmentList);
                    healthRecyclerView.setAdapter(treatmentAdapter);
                }
                progressDialog.dismiss();
                treatmentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.vet_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.app_bar_search);

        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Record by Name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                treatmentAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}