package com.magiri.animalcare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.VetsAroundAdapter;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.Session.Session;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    private static final String TAG = "Vets Around";
    private RecyclerView vetsAroundRecyclerView;
    List<Veterinarian> veterinarianList;
    VetsAroundAdapter vetsAroundAdapter;
    private DatabaseReference mRef, ref;
    Double farmerLocationLatitude;
    Double farmerLocationLongitude;
    private DrawerLayout drawerLayout;
    private MaterialSearchBar materialSearchBar;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_PERMISSION_CODE=123;
    private final int BACKGROUND_LOCATION_PERMISSION_CODE=124;
    private MaterialToolbar materialToolbar;
    private NavigationView navigationView;
    private ImageView closeDrawerImageView,headerProfilePic;
    private TextView headerFarmerNameTxt;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vetsAroundRecyclerView = findViewById(R.id.vetsAroundRecyclerView);
//        materialSearchBar=findViewById(R.id.searchBar);
        drawerLayout=findViewById(R.id.drawerLayout);
        materialToolbar=findViewById(R.id.vetMaterialToolBar);
        setSupportActionBar(materialToolbar);
        veterinarianList = new ArrayList<>();
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);

        vetsAroundRecyclerView.setHasFixedSize(true);
        vetsAroundRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRef = FirebaseDatabase.getInstance().getReference("Veterinarian");
        ref = FirebaseDatabase.getInstance().getReference("Geofire");
        navigationView=findViewById(R.id.navigationView);
        View header=navigationView.getHeaderView(0);
        headerFarmerNameTxt=header.findViewById(R.id.FarmerNameTxt);
        headerProfilePic=header.findViewById(R.id.profilePic);
        headerFarmerNameTxt.setText(Session.getInstance(this).getFarmerName());
        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading Vets");
        progressDialog.show();

        closeDrawerImageView=header.findViewById(R.id.imgClose);

        materialToolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        closeDrawerImageView.setOnClickListener(v -> closeNavigationDrawer());
        navigationView.setNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.home:
                    closeNavigationDrawer();
                    break;
                case R.id.account:
                    startActivity(new Intent(Home.this,Farmer_Account.class));
                    closeNavigationDrawer();
//                    finish();
                    break;
                case R.id.forumChat:
                    startActivity(new Intent(Home.this,Farmer_Forum.class));
                    closeNavigationDrawer();
//                    finish();
                    break;
                case R.id.record:
                    startActivity(new Intent(Home.this,FarmerRecords.class));
                    closeNavigationDrawer();
//                    finish();
                    break;
                case R.id.request:
                    startActivity(new Intent(Home.this,FarmerVisitRequests.class));
                    closeNavigationDrawer();
                    break;
                case R.id.logout:
                    SignoutUser();
                    closeNavigationDrawer();
                    break;
            }
            return false;
        });
        fetchLocation();
    }

    @SuppressLint("MissingPermission")
    private  void fetchLocation() {
        checkLocationPermission();
        if(!locationEnabled()){
            new AlertDialog.Builder(this)
                    .setTitle("Location Needed")
                    .setMessage("Please Turn on Location Services on the application")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User declined for Background Location Permission.
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        farmerLocationLatitude=location.getLatitude();
                        farmerLocationLongitude=location.getLongitude();
                        fetchVets(farmerLocationLatitude,farmerLocationLongitude);
                    }else{
                        Toast.makeText(getApplicationContext(),"Something Wrong Happened.Try Again Later",Toast.LENGTH_SHORT).show();
                        SignoutUser();
                    }
                }
            });
        }
    }

    private void fetchVets(Double latitude, Double longitude) {
        if(latitude !=null && longitude != null){
            GeoFire geoFire= new GeoFire(ref);
            GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(farmerLocationLatitude,farmerLocationLongitude),20);
            veterinarianList.clear();
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    mRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Veterinarian vet=snapshot.getValue(Veterinarian.class);
                            veterinarianList.add(vet);
                            vetsAroundAdapter=new VetsAroundAdapter(Home.this,veterinarianList);
                            vetsAroundRecyclerView.setAdapter(vetsAroundAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
            progressDialog.dismiss();
        }else{
            Toast.makeText(getApplicationContext(),"Something wrong happened",Toast.LENGTH_LONG).show();
        }
    }

    private boolean locationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Background Location Permission is granted so do your work here
                } else {
                    // Ask for Background Location Permission
                    ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUND_LOCATION_PERMISSION_CODE);
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
//            askForLocationPermission();
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted location permission
                // Now check if android version >= 11, if >= 11 check for Background Location Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Background Location Permission is granted so do your work here
                        fetchLocation();
                    } else {
                        // Ask for Background Location Permission
                        askPermissionForBackgroundUsage();
                    }
                }
            } else {
                // User denied location permission
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted for Background Location Permission.
                fetchLocation();
            } else {
                // User declined for Background Location Permission.

            }
        }

    }

    private void SignoutUser() {
        //redirect the user to loginActivity
        startActivity(new Intent(Home.this,FarmerLogin.class));
        Session.getInstance(Home.this).logout();
        Prevalent.currentOnlineFarmer=null;
        finish();
    }

    private void askForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("Location Permission Needed!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Home.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Permission is denied by the user
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private void askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Home.this,
                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User declined for Background Location Permission.
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.vet_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.app_bar_search);

        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Vets");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                vetsAroundAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private void closeNavigationDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
//            String locationAddress=data.getStringExtra(SimplePlacePicker.SELECTED_ADDRESS);
//            String latitude=String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA,-1));
//            String longitude=String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA,-1));
//            vetsAroundAdapter.UpdateLocation(locationAddress,latitude,longitude);
//        }
//    }
}