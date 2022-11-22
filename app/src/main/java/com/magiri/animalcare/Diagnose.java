package com.magiri.animalcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;

public class Diagnose extends AppCompatActivity {
    MaterialToolbar diagnoseMaterialToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose);
        diagnoseMaterialToolBar=findViewById(R.id.diagnoseMaterialToolBar);
    }
}