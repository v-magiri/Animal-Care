package com.magiri.animalcare;

import static com.magiri.animalcare.darajaApi.Constants.VISIT_COST_PER_10_KILOMETRE;
import static com.magiri.animalcare.darajaApi.Constants.VISIT_COST_PER_5_KILOMETRE;
import static com.magiri.animalcare.darajaApi.Constants.VISIT_COST_PER_ABOVE_10_KILOMETRE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.essam.simpleplacepicker.MapActivity;
import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.VisitRequest;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.UI.DropDown;
import com.magiri.animalcare.darajaApi.RestClient;
import com.magiri.animalcare.darajaApi.STKResponse;
import com.magiri.animalcare.darajaApi.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RequestVisitation extends AppCompatActivity {
    private static final String TAG = "RequestVisitation";
    private EditText locationDropDown, servicesDropDown;
    private Button submitBtn;
    private TextView locationTxt,VetNameTxt;
    private static String Country,Language,Api_Key;
    private static String Address;
    private CircleImageView profilePic;
    private double Latitude=0,Longitude=0;
    private static final String []mSupportedAreas={""};
    private LinearLayout locationLayout;
    private DatabaseReference mRef,ref;
    private ProgressDialog progressDialog,mProgressDialog;
    String[] locationOptions,serviceOptions;
    FusedLocationProviderClient fusedLocationProviderClient;
    String VETID,visitAddress,serviceType;
    TextInputLayout NameInputLayout;
    int visitationFee;
    double distanceBtw;
    AlertDialog paymentDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_visitation);

        locationDropDown=findViewById(R.id.locationDropDown);
        servicesDropDown=findViewById(R.id.servicesDropDown);
        submitBtn=findViewById(R.id.SubmitBtn);
        locationTxt=findViewById(R.id.locationTxt);
        VetNameTxt=findViewById(R.id.VetNameTxt);
        profilePic=findViewById(R.id.vetProfilePicCircleImageView);
        locationOptions=getResources().getStringArray(R.array.visitation_location_options);
        serviceOptions=getResources().getStringArray(R.array.service_options);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(RequestVisitation.this);
        locationLayout=findViewById(R.id.locationLayout);

        Bundle bundle=getIntent().getExtras();
        VetNameTxt.setText(bundle.getString("VetName","FooBar"));
        VETID=bundle.getString("VetID",null);
        if(bundle.getString("VetProfilePicUrl")!=null){
            Glide.with(getApplicationContext()).load(bundle.get("VetProfilePicUrl")).into(profilePic);
        }else{
            Glide.with(getApplicationContext()).load(R.drawable.ic_vet).into(profilePic);
        }

        Country="Kenya";
        Language="English";
        Api_Key=getResources().getString(R.string.maps_ApiKey);
        mRef= FirebaseDatabase.getInstance().getReference("VisitRequest");
        ref=FirebaseDatabase.getInstance().getReference("Veterinarian");

        progressDialog=new ProgressDialog(this);
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Saving Visit Request");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        locationDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(RequestVisitation.this);
                alertDialog.setTitle("Choose Visitation Location");
                int checkedItem=1;
                alertDialog.setSingleChoiceItems(locationOptions, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                getFarmerLocation();
                                dialog.dismiss();
                                break;
                            case 1:
                                progressDialog.show();
                                dialog.dismiss();
                                SelectLocation();
//                                if(Latitude!=null && Longitude!=null){
//                                  payVisit(vetID);
//                                }else{
//                                    Toast.makeText(getApplicationContext(),"Something wrong happened",Toast.LENGTH_SHORT).show();
//
                                break;
                        }
                        locationDropDown.setText(locationOptions[which]);
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        });
        servicesDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(RequestVisitation.this);
                alertDialog.setTitle("Choose Service You Need");
                int checkedItem=1;
                alertDialog.setSingleChoiceItems(serviceOptions, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                            case 1:
                                dialog.dismiss();
                                serviceType=serviceOptions[which];
                                break;
                            //                                servicesLayout.setVisibility(View.VISIBLE);
                        }
                        servicesDropDown.setText(serviceOptions[which]);
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        });

    }

    private void validateFields() {
        if(TextUtils.isEmpty(visitAddress)){
            NameInputLayout.setError("Please Choose Location");
            Toast.makeText(getApplicationContext(), "Please Choose Location", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(serviceType)){
            NameInputLayout.setError("Please Choose Location");
            Toast.makeText(getApplicationContext(), "Please Choose Location", Toast.LENGTH_SHORT).show();
            return;
        }else if(Latitude==0 && Longitude==0){
            Toast.makeText(getApplicationContext(), "Something wrong Happened Please Try again Later", Toast.LENGTH_SHORT).show();
            return;
        }else {
            mProgressDialog.show();
            SaveVisitRequest(visitAddress,Longitude,Latitude,serviceType);
        }

    }

    private void SaveVisitRequest(String visitAddress, double longitude, double latitude, String serviceType) {
        String clientID= Prevalent.currentOnlineFarmer.getFarmerID();
//        ref=mRef.child(RegistrationNumber);
        String visitID=mRef.push().getKey();
        if(VETID!=null){
//            String visitationFee=CalculateVisitationFee(latitude,longitude,VETID);
//            CalculateVisitationFee(latitude,longitude,VETID);
            ref.child(VETID).child("Location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double vetLatitude= Double.parseDouble(snapshot.child("Latitude").getValue(String.class));
                    double vetLongitude=Double.parseDouble(snapshot.child("Longitude").getValue(String.class));
                    Location vetLocation=new Location("VetLocation");
                    vetLocation.setLatitude(vetLatitude);
                    vetLocation.setLongitude(vetLongitude);
                    Location farmerLocation=new Location("FarmerLocation");
                    farmerLocation.setLatitude(latitude);
                    farmerLocation.setLongitude(longitude);
                    distanceBtw=(vetLocation.distanceTo(farmerLocation))/1000;
                    System.out.println("The Distance Between Them is "+distanceBtw);
                    if(distanceBtw>0 && distanceBtw<=5){
                        visitationFee= (int) (distanceBtw * VISIT_COST_PER_5_KILOMETRE);
                    }else if(distanceBtw>5 && distanceBtw<=10){
                        visitationFee= (int) (distanceBtw * VISIT_COST_PER_10_KILOMETRE);
                    }else{
                        visitationFee= (int) (distanceBtw * VISIT_COST_PER_ABOVE_10_KILOMETRE);
                    }
                    VisitRequest request=new VisitRequest(VETID,visitAddress,latitude,longitude,visitationFee,clientID,"Pending",visitID,false,serviceType);
                    mRef.child(visitID).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
//                                if(serviceType.equals("Artificial Insemination")){
//                                    Toast.makeText(RequestVisitation.this, "Request Successfully Added", Toast.LENGTH_SHORT).show();
////                                    PromptPayment(visitID,visitationFee,VETID);
//                                }else{
//                                    Intent intent=new Intent(RequestVisitation.this,Diagnose.class);
//                                    Bundle bundle=new Bundle();
//                                    bundle.putString("VISITID",visitID);
//                                    bundle.putInt("FEE",visitationFee);
//                                    intent.putExtras(intent);
//                                    startActivity(intent);
//                                    finish();
//                                }
                                PromptPayment(visitID,visitationFee,VETID,serviceType);
                                mProgressDialog.dismiss();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Timber.tag(TAG).d("onCancelled: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),"Something wrong Happened.Please try Again Later",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"Something wrong Happened",Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }

    private void PromptPayment(String visitID, int visitationFee, String vetid,String serviceType) {
        AlertDialog.Builder paymentDailogBuilder=new AlertDialog.Builder(RequestVisitation.this);
        LayoutInflater layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.checkout_layout,null);
        paymentDailogBuilder.setView(view);
        paymentDailogBuilder.setCancelable(false);
        TextInputEditText phoneNumberTxt=view.findViewById(R.id.phoneNumberTxt);
        ImageView closeImageView=view.findViewById(R.id.close_Dialog);
        Button payBtn=view.findViewById(R.id.payBtn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhoneNumber=phoneNumberTxt.getText().toString();
                if(PhoneNumber.equals("")){
                    phoneNumberTxt.setError("Invalid Phone Number");
                    return;
                }else {
                    Retrofit.Builder builder=new Retrofit.Builder()
                            .baseUrl("https://ani-care.herokuapp.com/")
                            .addConverterFactory(GsonConverterFactory.create());

                    Retrofit retrofit=builder.build();
                    RestClient restClient=retrofit.create(RestClient.class);
                    Call<STKResponse> call=restClient.pushStk(
                            visitationFee,
                            Utils.refactorPhoneNumber(PhoneNumber),
                            vetid,
                            visitID
                    );
                    call.enqueue(new Callback<STKResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<STKResponse> call, @NonNull Response<STKResponse> response) {
                            assert response.body() != null;
                            if(response.body().getResponseCode().equals("0")){
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Visit Request Recorded",Toast.LENGTH_SHORT).show();
                                Timber.tag(TAG).i("Mpesa Worked: ");
                                paymentDailog.dismiss();
                                if(serviceType.equals("Disease Treatment")){
                                    Intent intent=new Intent(RequestVisitation.this,Diagnose.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putString("VISITID",visitID);
                                    intent.putExtras(intent);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    finish();
                                }

                            }else{
                                Toast.makeText(getApplicationContext(),"Something Wrong Happened Please Try Again",Toast.LENGTH_SHORT).show();
                                Timber.tag(TAG).i("Mpesa Failed: ");
                                paymentDailog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<STKResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"Something Wrong Happened Please Try Again",Toast.LENGTH_SHORT).show();
                            Timber.tag(TAG).d("onFailure: Something wrong Happened");
                            paymentDailog.dismiss();
                        }
                    });
                }
//                progressDialog.show();
            }
        });
        paymentDailog=paymentDailogBuilder.create();
        paymentDailog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        paymentDailog.show();
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentDailog.dismiss();
            }
        });
    }

//    private void CalculateVisitationFee(double latitude, double longitude, String vetid) {
//        ref.child(vetid).child("Location").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                double vetLatitude= Double.parseDouble(snapshot.child("Latitude").getValue(String.class));
//                double vetLongitude=Double.parseDouble(snapshot.child("Longitude").getValue(String.class));
//                Location vetLocation=new Location("VetLocation");
//                vetLocation.setLatitude(vetLatitude);
//                vetLocation.setLongitude(vetLongitude);
//                Location farmerLocation=new Location("FarmerLocation");
//                farmerLocation.setLatitude(latitude);
//                farmerLocation.setLongitude(longitude);
//                distanceBtw=(vetLocation.distanceTo(farmerLocation))/1000;
//                System.out.println("The Distance Between Them is "+distanceBtw);
//                if(distanceBtw>0 && distanceBtw<=5){
//                    visitationFee= (int) (distanceBtw * VISIT_COST_PER_5_KILOMETRE);
//                }else if(distanceBtw>5 && distanceBtw<=10){
//                    visitationFee= (int) (distanceBtw * VISIT_COST_PER_10_KILOMETRE);
//                }else{
//                    visitationFee= (int) (distanceBtw * VISIT_COST_PER_ABOVE_10_KILOMETRE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Timber.tag(TAG).d("onCancelled: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),"Something wrong Happened.Please try Again Later",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void SelectLocation() {
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SimplePlacePicker.API_KEY,Api_Key);
        bundle.putString(SimplePlacePicker.COUNTRY,Country);
        bundle.putString(SimplePlacePicker.LANGUAGE,Language);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,mSupportedAreas);
        intent.putExtras(bundle);
        startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
        progressDialog.dismiss();
    }

    @SuppressLint("MissingPermission")
    private void getFarmerLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Latitude= location.getLatitude();
                    Longitude= location.getLongitude();
                    try{
                        Geocoder geocoder=new Geocoder(RequestVisitation.this, Locale.getDefault());
                        List<Address> addresses=geocoder.getFromLocation(Latitude,Longitude,1);
                        if(addresses!=null&& addresses.size()>0){
                            locationLayout.setVisibility(View.VISIBLE);
                            locationTxt.setText(addresses.get(0).getAddressLine(0));
                            visitAddress=addresses.get(0).getAddressLine(0);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void makeVisitationPayment(int visitationFee,String RegistrationNumber,String phoneNumber) {
        visitationFee=1;
        final DatabaseReference ref;
        String clientID= Prevalent.currentOnlineFarmer.getFarmerID();
//        ref=mRef.child(RegistrationNumber);
        String visitID=mRef.push().getKey();
//        VisitRequest visitRequest=new VisitRequest(RegistrationNumber,Address,Latitude,Longitude,visitationFee,clientID,"Pending",visitID,false);
        assert visitID != null;
//        mRef.child(visitID).setValue(visitRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(getApplicationContext(),"Request Recorded",Toast.LENGTH_SHORT).show();
//                }else{
//                    Log.d(TAG, "onComplete: Something Happened");
//                }
//                progressDialog.dismiss();
////                bottomSheetDialog.dismiss();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: "+e.getMessage());
//                Toast.makeText(getApplicationContext(),"Failed to Record your visit Request",Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
////                bottomSheetDialog.dismiss();
//            }
//        });
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://ani-care.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();
        RestClient restClient=retrofit.create(RestClient.class);
        Call<STKResponse> call=restClient.pushStk(
                visitationFee,
                Utils.refactorPhoneNumber(phoneNumber),
                RegistrationNumber,
                visitID
        );
        call.enqueue(new Callback<STKResponse>() {
            @Override
            public void onResponse(Call<STKResponse> call, Response<STKResponse> response) {
                if(response.body().getResponseCode().equals("0")){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Visit Request Recorded",Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).i("Mpesa Worked: ");

                }else{
                    Toast.makeText(getApplicationContext(),"Something Wrong Happened Please Try Again",Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).i("Mpesa Failed: ");
                }
            }

            @Override
            public void onFailure(Call<STKResponse> call, Throwable t) {
                Timber.tag(TAG).d("onFailure: Something wrong Happened");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            String locationAddress=data.getStringExtra(SimplePlacePicker.SELECTED_ADDRESS);
            Latitude=data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA,-1);
            Longitude=data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA,-1);
            locationLayout.setVisibility(View.VISIBLE);
            locationTxt.setText(locationAddress);
            visitAddress=locationAddress;
        }
    }

}