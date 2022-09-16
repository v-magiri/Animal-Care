package com.magiri.animalcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

public class FarmerRecords extends AppCompatActivity {
    private MaterialToolbar recordMaterialToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_records);
        recordMaterialToolBar=findViewById(R.id.recordMaterialToolBar);

        recordMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}