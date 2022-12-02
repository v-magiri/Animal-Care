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
import com.magiri.animalcare.Adapters.ProductionAdapter;
import com.magiri.animalcare.Model.MilkRecord;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class ProductionRecord extends AppCompatActivity {

    private MaterialToolbar materialToolbar;
    private List<MilkRecord> milkRecordList;
    private RecyclerView milkRecyclerView;
    private String FarmerID;
    private DatabaseReference ref;
    ProductionAdapter productionAdapter;
    private static final String TAG = "ProductionRecord";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_record);
        materialToolbar=findViewById(R.id.productionMaterialToolBar);
        setSupportActionBar(materialToolbar);
        milkRecordList=new ArrayList<>();
        productionAdapter=new ProductionAdapter(ProductionRecord.this,milkRecordList);
        milkRecyclerView=findViewById(R.id.productionRecordRecyclerView);

        milkRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        milkRecyclerView.setHasFixedSize(true);
        milkRecyclerView.setAdapter(productionAdapter);
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        ref= FirebaseDatabase.getInstance().getReference("MilkProduction").child(FarmerID);
        getMilkProduction();
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getMilkProduction() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MilkRecord milkRecord=dataSnapshot.getValue(MilkRecord.class);
                    milkRecordList.add(milkRecord);
                }
                productionAdapter.notifyDataSetChanged();
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
        searchView.setQueryHint("Search Production By Animal");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productionAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}