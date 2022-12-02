package com.magiri.animalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

public class FarmerRecords extends AppCompatActivity {
    private MaterialToolbar recordMaterialToolBar;
    private CardView breedingCard,productionCard,healthCard,herdCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_records);
        recordMaterialToolBar=findViewById(R.id.recordMaterialToolBar);
        breedingCard=findViewById(R.id.breedingCard);
        productionCard=findViewById(R.id.productionCard);
        healthCard=findViewById(R.id.HealthCard);
        herdCard=findViewById(R.id.HerdCard);

        herdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //view and manage my herd
                startActivity(new Intent(getApplicationContext(),Herd.class));
            }
        });
        breedingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BreedingRecord.class));
            }
        });
        productionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProductionRecord.class));
            }
        });

        recordMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        healthCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HealthRecords.class));
            }
        });
    }
}