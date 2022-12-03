package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.VisitationAdapter;
import com.magiri.animalcare.Model.VisitRequest;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;
import java.util.List;

public class FarmerVisitRequests extends AppCompatActivity {
    private static final String TAG = "FarmerVisitRequests";
    private MaterialToolbar materialToolBar;
    private RecyclerView requestRecyclerView;
    List<VisitRequest> visitRequestList;
    VisitationAdapter visitationAdapter;
    private DatabaseReference mRef;
    String FarmerID;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_visit_requests);
        mRef= FirebaseDatabase.getInstance().getReference("VisitRequest");
        visitRequestList=new ArrayList<>();
        materialToolBar=findViewById(R.id.materialToolBar);
        requestRecyclerView=findViewById(R.id.requestRecyclerView);
        requestRecyclerView.setHasFixedSize(true);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading Visit Requests");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        materialToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getVisitRequests();
    }

    private void getVisitRequests() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                visitRequestList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    VisitRequest visitRequest=dataSnapshot.getValue(VisitRequest.class);
                    if(visitRequest.getFarmerID().equals(FarmerID)){
                        visitRequestList.add(visitRequest);
                        visitationAdapter=new VisitationAdapter(FarmerVisitRequests.this,visitRequestList);
                        requestRecyclerView.setAdapter(visitationAdapter);
                    }
                }
                progressDialog.dismiss();
                visitationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}