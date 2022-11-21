package com.magiri.animalcare;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Herd extends AppCompatActivity {
    private MaterialToolbar herdMaterialToolBar;
    private LinearLayout breedFilter,statusFilter,ageFilter;
    private FloatingActionButton addAnimalFAB;
    private RecyclerView animalRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herd);

        herdMaterialToolBar=findViewById(R.id.herdMaterialToolBar);
        breedFilter=findViewById(R.id.breedFilter);
        statusFilter=findViewById(R.id.statusFilter);
        ageFilter=findViewById(R.id.ageFilter);
        addAnimalFAB=findViewById(R.id.addAnimalFloatingBar);
        animalRecyclerView=findViewById(R.id.herdRecyclerView);


        herdMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.vet_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.app_bar_search);

        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Animal");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //todo update with the recyclerView Adapter
                return false;
            }
        });

        return true;
    }
}