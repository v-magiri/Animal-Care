package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.magiri.animalcare.Adapters.AnimalBreedingAdapter;
import com.magiri.animalcare.Adapters.BreedingAdapter;
import com.magiri.animalcare.Model.Breeding;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class BreedingRecord extends AppCompatActivity {
    private static final String TAG = "BreedingRecord";
    RecyclerView breedRecyclerView;
    List<Breeding> breedingList;
    AnimalBreedingAdapter breedingAdapter;
    private MaterialToolbar materialToolbar;
    DatabaseReference mRef;
    String FarmerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breeding_record);
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        materialToolbar=findViewById(R.id.breedingMaterialToolBar);
        setSupportActionBar(materialToolbar);
        breedingList=new ArrayList<>();
        breedingAdapter=new AnimalBreedingAdapter(BreedingRecord.this,breedingList);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRef= FirebaseDatabase.getInstance().getReference("Breeding").child(FarmerID);
        breedRecyclerView=findViewById(R.id.breedingRecordRecyclerView);
        breedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        breedRecyclerView.setHasFixedSize(true);
        breedRecyclerView.setAdapter(breedingAdapter);

        getBreedingRecord();

    }

    private void getBreedingRecord() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Breeding breeding=dataSnapshot.getValue(Breeding.class);
                    breedingList.add(breeding);
                }
                breedingAdapter.notifyDataSetChanged();

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
        searchView.setQueryHint("Search Breeding by Name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                breedingAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}