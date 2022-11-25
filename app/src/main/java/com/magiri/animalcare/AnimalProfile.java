package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Animal;
import com.magiri.animalcare.Session.Prevalent;

public class AnimalProfile extends AppCompatActivity {
    private static final String TAG = "AnimalProfile";
    FrameLayout frameLayout;
    TabLayout tabLayout;
    ImageView animalImageView;
    String AnimalID;
    DatabaseReference mRef;
    String FarmerID;
    private MaterialToolbar materialToolbar;
    private TextView animalNameTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_profile);
        frameLayout=findViewById(R.id.simpleFrameLayout);
        tabLayout=findViewById(R.id.simpleTabLayout);
        animalImageView=findViewById(R.id.animalImageView);
        materialToolbar=findViewById(R.id.animalProfileMaterialToolBar);
        animalNameTxt=findViewById(R.id.animalNameTextView);

        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        mRef= FirebaseDatabase.getInstance().getReference("Animals").child(FarmerID);
        Bundle bundle=getIntent().getExtras();
        AnimalID=bundle.getString("AnimalID",null);
        animalNameTxt.setText(AnimalID);
        fetchAnimalDetails(AnimalID);

        TabLayout.Tab profile=tabLayout.newTab();
        profile.setText("Profile");
        tabLayout.addTab(profile);

        TabLayout.Tab productionTab=tabLayout.newTab();
        productionTab.setText("Milk");
        tabLayout.addTab(productionTab);

        TabLayout.Tab healthTab=tabLayout.newTab();
        healthTab.setText("Health");
        tabLayout.addTab(healthTab);

        TabLayout.Tab breedingTab=tabLayout.newTab();
        breedingTab.setText("Breeding");
        tabLayout.addTab(breedingTab);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment=null;
                switch(tab.getPosition()){
                    case 0:
                        fragment=new Animal_Profile();
                        break;
                    case 1:
                        fragment=new Production();
                        break;
                    case 2:
                        fragment=new Health();
                        break;
                    case 3:
                        fragment=new Breeding();
                        break;
                }
                FragmentManager fm=getSupportFragmentManager();
                FragmentTransaction transaction=fm.beginTransaction();
                transaction.replace(R.id.simpleFrameLayout,fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchAnimalDetails(String animalID) {
        mRef.child(animalID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Animal animal=snapshot.getValue(Animal.class);
                String animalImageUrl=animal.getAnimalImageUrl();
                Glide.with(getApplicationContext()).load(animalImageUrl).into(animalImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}