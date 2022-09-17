package com.magiri.animalcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.essam.simpleplacepicker.MapActivity;
import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.Model.VisitRequest;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewVet extends AppCompatActivity {
    private static final String TAG = "View Vet";
    private ListView skillListView;
    private RelativeLayout forwardRelativeLayout;
    private CircleImageView profilePic;
    private TextView nameTextView,locationTextView,visitationFeeTextView;
    private Button consultBtn,requestBtn;
    String RegNum,visitationCharges;
    String[] skillList;
    ArrayList<String> SkillList;
    ArrayAdapter<String> arrayAdapter;
    String VetName,Country,Language,Api_Key,Address,Latitude,Longitude;
    private static String []mSupportedAreas={""};
    ProgressDialog progressDialog;
    private DatabaseReference mRef;
    private BottomSheetDialog bottomSheetDialog;
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
        visitationFeeTextView=findViewById(R.id.visitationFeeTextView);
        consultBtn=findViewById(R.id.consultBtn);
        requestBtn=findViewById(R.id.visitBtn);

        Country="Kenya";
        Language="English";
        Api_Key=getResources().getString(R.string.maps_ApiKey);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait, while we save your Request");
        progressDialog.setCanceledOnTouchOutside(false);
        mRef=FirebaseDatabase.getInstance().getReference("VisitRequest");

        RegNum=getIntent().getStringExtra("Vet_RegNum");
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
                bottomSheetDialog=new BottomSheetDialog(ViewVet.this);
                bottomSheetDialog.setContentView(R.layout.request_visit_bottom_sheet);
                TextView vetNameTextView=bottomSheetDialog.findViewById(R.id.vetNameTextView);
                locationTextView=bottomSheetDialog.findViewById(R.id.locationTxt);
                Button cancelBtn=bottomSheetDialog.findViewById(R.id.cancelBtn);
                Button OkBtn=bottomSheetDialog.findViewById(R.id.okStatusBtn);
                ImageView locationPickerImageView=bottomSheetDialog.findViewById(R.id.pickLocationImageView);

                vetNameTextView.setText(VetName);
                locationPickerImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open location Place Picker
                        SelectLocation();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                OkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //save the request to database
                        validatedSelectedLocation(RegNum,visitationCharges);
//                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();

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
    private void SelectLocation() {
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SimplePlacePicker.API_KEY,Api_Key);
        bundle.putString(SimplePlacePicker.COUNTRY,Country);
        bundle.putString(SimplePlacePicker.LANGUAGE,Language);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,mSupportedAreas);
        intent.putExtras(bundle);
        startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
    }
    private void validatedSelectedLocation(String RegistrationNumber,String Fee) {
        if(TextUtils.isEmpty(Address) || TextUtils.isEmpty(Latitude) || TextUtils.isEmpty(Longitude)){
            Toast.makeText(getApplicationContext(),"Please Select Your Location",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        SaveRequest(RegistrationNumber,Fee);
    }

    private void SaveRequest(String registrationNumber, String fee) {
        final DatabaseReference ref;
        String clientID= Prevalent.currentOnlineFarmer.getFarmerID();
//        ref=mRef.child(RegistrationNumber);
        String visitID=mRef.push().getKey();
        VisitRequest visitRequest=new VisitRequest(registrationNumber,Address,Latitude,Longitude,fee,clientID,"Pending",visitID,false);
        assert visitID != null;
        mRef.child(visitID).setValue(visitRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Request Recorded",Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "onComplete: Something Happened");
                }
                progressDialog.dismiss();
                bottomSheetDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                Toast.makeText(getApplicationContext(),"Failed to Record your visit Request",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                bottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            String locationAddress=data.getStringExtra(SimplePlacePicker.SELECTED_ADDRESS);
            String latitude=String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA,-1));
            String longitude=String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA,-1));
        }
    }

    private void fetchData(String regNum) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Veterinarian");
        ref.child(regNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veterinarian vet=snapshot.getValue(Veterinarian.class);
                visitationCharges=vet.getVisitationFee();
                visitationFeeTextView.setText(visitationCharges);
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
                arrayAdapter=new ArrayAdapter(ViewVet.this, android.R.layout.simple_list_item_1,skillList);
                skillListView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong happened.Please contect support team",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}