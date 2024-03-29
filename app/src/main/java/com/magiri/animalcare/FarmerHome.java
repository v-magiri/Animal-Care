package com.magiri.animalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.Session.Session;

public class FarmerHome extends AppCompatActivity {
    private LinearLayout searchCard,herdLayout,forumCard,recordLayout;
    private RelativeLayout profileCard;
    private MaterialToolbar materialToolbar;
    private NavigationView navigationView;;
    private DrawerLayout drawerLayout;;
    private ImageView closeDrawerImageView,clientProfilePic,headerProfilePic;
    private TextView nameTxt,headerFarmerNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_home);

        //binding views
        profileCard=findViewById(R.id.farmerProfileCard);
        searchCard=findViewById(R.id.searchVetCard);
        herdLayout=findViewById(R.id.herdCard);
        recordLayout=findViewById(R.id.recordsCard);
        forumCard=findViewById(R.id.forumCard);
        materialToolbar=findViewById(R.id.animalCareMaterialToolbar);
        navigationView=findViewById(R.id.navigationView);
        drawerLayout=findViewById(R.id.drawerLayout);
        nameTxt=findViewById(R.id.clientNameTxt);
        clientProfilePic=findViewById(R.id.farmerProfilePic);

        View header=navigationView.getHeaderView(0);
        headerFarmerNameTxt=header.findViewById(R.id.FarmerNameTxt);
        headerProfilePic=header.findViewById(R.id.profilePic);

        nameTxt.setText(Session.getInstance(this).getFarmerName());
        headerFarmerNameTxt.setText(Session.getInstance(this).getFarmerName());

        closeDrawerImageView=header.findViewById(R.id.imgClose);


        forumCard.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),Farmer_Forum.class));
//            finish();
        });
        searchCard.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Home.class));
//            finish();
        });
        herdLayout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),Herd.class));
//            finish();
        });
        profileCard.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),Farmer_Account.class));
//            finish();
        });
        recordLayout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),FarmerRecords.class));
//            finish();
        });

        //open the navigation Drawer
        materialToolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));


        closeDrawerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNavigationDrawer();
            }
        });

        //on item on navigation View Selected
        navigationView.setNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.home:
                    closeNavigationDrawer();
                    break;
                case R.id.account:
                    startActivity(new Intent(FarmerHome.this,Farmer_Account.class));
                    closeNavigationDrawer();
//                    finish();
                    break;
                case R.id.forumChat:
                    startActivity(new Intent(FarmerHome.this,Farmer_Forum.class));
                    closeNavigationDrawer();
//                    finish();
                    break;
                case R.id.record:
                    startActivity(new Intent(FarmerHome.this,FarmerRecords.class));
                    closeNavigationDrawer();
//                    finish();
                    break;
                case R.id.logout:
                    SignoutUser();
                    closeNavigationDrawer();
                    break;
            }
            return false;
        });
    }

    private void SignoutUser() {
        //redirect the user to loginActivity
        startActivity(new Intent(FarmerHome.this,FarmerLogin.class));
        Session.getInstance(FarmerHome.this).logout();
        Prevalent.currentOnlineFarmer=null;
        finish();
    }
    private void closeNavigationDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    //todo Update home UI to look more appealing as https://dribbble.com/shots/9455575-Job-Search-Mobile-App

}