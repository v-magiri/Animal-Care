package com.magiri.animalcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.QualificationAdapter;
import com.magiri.animalcare.Adapters.RatingAdapter;
import com.magiri.animalcare.Model.Veterinarian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewVet extends AppCompatActivity {
    private static final String TAG = "View Vet";
    private RecyclerView skillListView;
    private RelativeLayout forwardRelativeLayout;
    private ImageView profilePic;
    private TextView nameTextView,locationTextView,viewAllTxt;
    private Button consultBtn,requestBtn;
    String RegNum,visitationCharges;
    private RecyclerView reviewRecyclerView;
    String[] skillList;
    List<HashMap<String,Object>> ratingList;
    QualificationAdapter arrayAdapter;
    String VetName,Latitude,Longitude;
    ProgressDialog progressDialog;
    private DatabaseReference mRef;
    private MaterialToolbar materialToolbar;
    RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager horizontalLayout;
    LinearLayout reviewLayout;
    RatingAdapter ratingAdapter;
    private String vetLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vet);

        skillListView=findViewById(R.id.qualificationListView);
        forwardRelativeLayout=findViewById(R.id.forwardLayout);
        profilePic=findViewById(R.id.vetProfilePic);
        nameTextView=findViewById(R.id.vetNameTxt);
        locationTextView=findViewById(R.id.locationTxt);
        consultBtn=findViewById(R.id.consultBtn);
        requestBtn=findViewById(R.id.visitBtn);
        materialToolbar=findViewById(R.id.materialToolBar);
        reviewRecyclerView=findViewById(R.id.ratingRecyclerView);
        layoutManager=new LinearLayoutManager(getApplicationContext());
        reviewRecyclerView.setLayoutManager(layoutManager);
        horizontalLayout=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        reviewRecyclerView.setLayoutManager(horizontalLayout);
        viewAllTxt=findViewById(R.id.viewAllBtn);
        skillListView.setLayoutManager(new LinearLayoutManager(this));
        skillListView.setHasFixedSize(true);

        ratingList=new ArrayList<>();
        reviewLayout=findViewById(R.id.reviewLayout);

        viewAllTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to view all reviews
            }
        });

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading Vet Profile");
        progressDialog.setCanceledOnTouchOutside(false);
        mRef=FirebaseDatabase.getInstance().getReference("Ratings");
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RegNum=getIntent().getStringExtra("VETID");
        fetchData(RegNum);

        consultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start chat
                Intent intent=new Intent(getApplicationContext(), FarmerVet_Chat.class);
                intent.putExtra("Vet_REGNUM",RegNum);
                startActivity(intent);
            }
        });
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), RequestVisitation.class);
                intent.putExtra("VetID",RegNum);
                startActivity(intent);
                finish();
            }
        });

        forwardRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // let the user view the vet on  maps
                Uri mapUri= Uri.parse("google.navigation:q="+vetLocation);
                Intent mapIntent=new Intent(Intent.ACTION_VIEW,mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void fetchData(String regNum) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Veterinarian");
        ref.child(regNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veterinarian vet=snapshot.getValue(Veterinarian.class);
                visitationCharges=vet.getVisitationFee();
                String location=vet.getWard()+","+vet.getConstituency();
                locationTextView.setText(location);
                VetName=vet.getName();
                nameTextView.setText(VetName);
                String vetLatitude=snapshot.child("Location").child("Latitude").getValue(String.class);
                String vetLongitude=snapshot.child("Longitude").child("Longitude").getValue(String.class);
                vetLocation=vetLatitude+","+vetLongitude;
                if(!vet.getProfilePicUrl().equals("")){
                    Glide.with(ViewVet.this).load(vet.getProfilePicUrl()).into(profilePic);
                }else{
                    Glide.with(ViewVet.this).load(R.drawable.ic_vet).into(profilePic);
                }
                skillList= vet.getSkill().split(",");
                arrayAdapter=new QualificationAdapter(ViewVet.this, skillList);
                skillListView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                mRef.child(regNum).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            HashMap<String,Object> map=new HashMap<>();
                            String comment= (String) dataSnapshot.child("Comment").getValue();
                            if(!TextUtils.isEmpty(comment)){
                                map.put("Rating",dataSnapshot.child("Rating").getValue());
                                map.put("Comment",dataSnapshot.child("Comment").getValue());
                                map.put("FarmerID",dataSnapshot.child("FarmerID").getValue());
                                ratingList.add(map);
                            }
                        }
                        if(ratingList.size()==0){
                            reviewLayout.setVisibility(View.GONE);
                        }else{
                            ratingAdapter=new RatingAdapter(ViewVet.this,ratingList);
                            reviewRecyclerView.setAdapter(ratingAdapter);
                            ratingAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: "+error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong happened.Please contect support team",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}