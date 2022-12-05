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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.magiri.animalcare.Model.Animal;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.Model.VisitRequest;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.UI.DropDown;
import com.magiri.animalcare.darajaApi.RestClient;
import com.magiri.animalcare.darajaApi.STKResponse;
import com.magiri.animalcare.darajaApi.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
//    private EditText locationDropDown, servicesDropDown;
    private Button submitBtn;
    private TextView locationTxt,VetNameTxt;
    private static String Country,Language,Api_Key;
    private static String Address;
    private CircleImageView profilePic;
    private double Latitude=0,Longitude=0;
    private static final String []mSupportedAreas={""};
    private LinearLayout locationLayout;
    private DatabaseReference mRef,ref,databaseReference;
    private ProgressDialog progressDialog,mProgressDialog,mapProgressDialog;
    String[] locationOptions,serviceOptions;
    FusedLocationProviderClient fusedLocationProviderClient;
    String VETID,visitAddress,serviceType;
    TextInputLayout NameInputLayout;
    int visitationFee;
    double distanceBtw;
    AlertDialog paymentDailog;
    AutoCompleteTextView animalSelectionTxt,locationDropDown,servicesDropDown;
    String FarmerID,SelectedAnimal;
    ArrayList<String> listAnimal;
    ProgressDialog animalProgressDialog;
    ArrayAdapter arrayAdapter,locationOptionsAdapter,servicesOptionsAdapter;
    LinearLayout animalLayout;

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
        locationOptionsAdapter=new ArrayAdapter(getApplicationContext(),R.layout.item,locationOptions);
        servicesOptionsAdapter=new ArrayAdapter(getApplicationContext(),R.layout.item,serviceOptions);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(RequestVisitation.this);
        locationLayout=findViewById(R.id.locationLayout);
        animalSelectionTxt=findViewById(R.id.animalChoiceTxt);
        FarmerID=Prevalent.currentOnlineFarmer.getFarmerID();
        animalLayout=findViewById(R.id.animalChoiceLayout);
        locationDropDown.setAdapter(locationOptionsAdapter);
        servicesDropDown.setAdapter(servicesOptionsAdapter);

        mRef= FirebaseDatabase.getInstance().getReference("VisitRequest");
        ref=FirebaseDatabase.getInstance().getReference("Veterinarian");
        databaseReference=FirebaseDatabase.getInstance().getReference("Animals").child(FarmerID);
        listAnimal=new ArrayList<>();
        animalProgressDialog=new ProgressDialog(RequestVisitation.this);
        animalProgressDialog.setMessage("Loading Animal Names");
        animalProgressDialog.setCanceledOnTouchOutside(false);
        animalProgressDialog.show();
        mapProgressDialog=new ProgressDialog(this);
        mapProgressDialog.setMessage("Opening Map");
        mapProgressDialog.setCanceledOnTouchOutside(false);
        loadMyHerd();
        animalSelectionTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedAnimal=parent.getItemAtPosition(position).toString();
            }
        });
        servicesDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                serviceType=parent.getItemAtPosition(position).toString();
            }
        });
        locationDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String locationOptionSelected=parent.getItemAtPosition(position).toString();
                if(locationOptionSelected.equals("Current Location")){
                    getFarmerLocation();
                }else{
                    mapProgressDialog.show();
                    SelectLocation();
                }
            }
        });

        Bundle bundle=getIntent().getExtras();
//        VetNameTxt.setText(bundle.getString("VetName","FooBar"));

        VETID=bundle.getString("VetID",null);
        ref.child(VETID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veterinarian veterinarian=snapshot.getValue(Veterinarian.class);
                VetNameTxt.setText(veterinarian.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
        if(bundle.getString("VetProfilePicUrl")!=null){
            Glide.with(getApplicationContext()).load(bundle.get("VetProfilePicUrl")).into(profilePic);
        }else{
            Glide.with(getApplicationContext()).load(R.drawable.ic_vet).into(profilePic);
        }

        Country="Kenya";
        Language="English";
        Api_Key=getResources().getString(R.string.maps_ApiKey);

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

    }

    private void loadMyHerd() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot3:snapshot.getChildren()){
                    Animal myAnimal=dataSnapshot3.getValue(Animal.class);
                    listAnimal.add(myAnimal.getAnimalName());
                }
                animalProgressDialog.dismiss();
                if(listAnimal.size()==0){
                    animalLayout.setVisibility(View.GONE);
                    Toast.makeText(RequestVisitation.this,"Please register Animals",Toast.LENGTH_SHORT).show();
                }else{
                    arrayAdapter=new ArrayAdapter(getApplicationContext(),R.layout.item,listAnimal);
                    animalSelectionTxt.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
                animalProgressDialog.dismiss();
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
        }else if(TextUtils.isEmpty(SelectedAnimal)){
            Toast.makeText(getApplicationContext(), "Something wrong Happened Please Try again Later", Toast.LENGTH_SHORT).show();
        }
        else {
            mProgressDialog.show();
            SaveVisitRequest(visitAddress,Longitude,Latitude,serviceType,SelectedAnimal);
        }

    }

    private void SaveVisitRequest(String visitAddress, double longitude, double latitude, String serviceType,String animalID) {
        String clientID= Prevalent.currentOnlineFarmer.getFarmerID();
        String visitID=mRef.push().getKey();
        if(VETID!=null){
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
                    System.out.println("The Visitation Fee is: "+visitationFee);
                    VisitRequest request=new VisitRequest(VETID,visitAddress,latitude,longitude,visitationFee,clientID,"Pending",visitID,false,serviceType,animalID);
                    PromptPayment(visitID,visitationFee,VETID,serviceType,request);
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

    private void PromptPayment(String visitID, int visitationFee, String vetid,String serviceType,VisitRequest request) {
//        visitationFee=1;
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
                    progressDialog.show();
                    Retrofit.Builder builder=new Retrofit.Builder()
                            .baseUrl("https://4b43-41-89-227-170.eu.ngrok.io/")
                            .addConverterFactory(GsonConverterFactory.create());

                    Retrofit retrofit=builder.build();
                    RestClient restClient=retrofit.create(RestClient.class);
                    Call<STKResponse> call=restClient.pushStk(
                            1,
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
                                mRef.child(visitID).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            paymentDailog.dismiss();
                                            if(serviceType.equals("Disease Treatment")){
                                                Intent intent=new Intent(RequestVisitation.this,Diagnose.class);
                                                Bundle bundle=new Bundle();
                                                bundle.putString("VISITID",visitID);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                finish();
                                            }

                                        }
                                    }
                                });
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Something Wrong Happened Please Try Again",Toast.LENGTH_SHORT).show();
                                Timber.tag(TAG).i("Mpesa Failed: ");
                                paymentDailog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<STKResponse> call, Throwable t) {
                            progressDialog.dismiss();
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
        mapProgressDialog.dismiss();
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