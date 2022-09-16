package com.magiri.animalcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

public class Herd extends AppCompatActivity {
    private MaterialToolbar herdMaterialToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herd);

        herdMaterialToolBar=findViewById(R.id.herdMaterialToolBar);

        herdMaterialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}