package com.magiri.animalcare.Adapters;

import static com.magiri.animalcare.darajaApi.Constants.BUSINESS_SHORT_CODE;
import static com.magiri.animalcare.darajaApi.Constants.CALLBACKURL;
import static com.magiri.animalcare.darajaApi.Constants.PARTYB;
import static com.magiri.animalcare.darajaApi.Constants.PASSKEY;
import static com.magiri.animalcare.darajaApi.Constants.TRANSACTION_TYPE;
import static com.magiri.animalcare.darajaApi.Constants.VISIT_COST_PER_10_KILOMETRE;
import static com.magiri.animalcare.darajaApi.Constants.VISIT_COST_PER_5_KILOMETRE;
import static com.magiri.animalcare.darajaApi.Constants.VISIT_COST_PER_ABOVE_10_KILOMETRE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.essam.simpleplacepicker.MapActivity;
import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.FarmerVet_Chat;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.Model.VisitRequest;
import com.magiri.animalcare.R;
import com.magiri.animalcare.RequestVisitation;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.ViewVet;
import com.magiri.animalcare.darajaApi.AccessToken;
import com.magiri.animalcare.darajaApi.DarajaApiClient;
import com.magiri.animalcare.darajaApi.RestClient;
import com.magiri.animalcare.darajaApi.STKPUSH;
import com.magiri.animalcare.darajaApi.STKResponse;
import com.magiri.animalcare.darajaApi.Utils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class VetsAroundAdapter extends RecyclerView.Adapter<VetsAroundAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "VetsAroundAdapter";
    Context context;
    List<Veterinarian> veterinarianList;
    private static String Country,Language,Api_Key;
    private static String Address,Latitude,Longitude;
    private static final String []mSupportedAreas={""};
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private static Double farmerLatitude;
    private static Double farmerLongitude;
    Activity activity;
    TextView locationTextView;
    ImageView locationPickerImageView;
    private static DatabaseReference mRef,ref;
    ProgressDialog progressDialog;
    BottomSheetDialog bottomSheetDialog;
//    AlertDialog paymentDailog;
    List<Veterinarian> vetList;
    private final DarajaApiClient darajaApiClient;
    double distanceBtw;
    String[] locationOptions;

    public VetsAroundAdapter(Context context, List<Veterinarian> veterinarianList) {
        this.context = context;
        this.veterinarianList = veterinarianList;
        Country="Kenya";
        Language="English";
        Api_Key=context.getResources().getString(R.string.maps_ApiKey);
        activity= (Activity) context;
        mRef= FirebaseDatabase.getInstance().getReference("VisitRequest");
        ref=FirebaseDatabase.getInstance().getReference("Veterinarian");
        progressDialog=new ProgressDialog(context);
//        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");
        vetList=new ArrayList<>(veterinarianList);
        darajaApiClient=new DarajaApiClient();
        darajaApiClient.setGetAccessToken(true);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context);
        locationOptions=context.getResources().getStringArray(R.array.visitation_location_options);
    }

    @NonNull
    @Override
    public VetsAroundAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.vet_around_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VetsAroundAdapter.MyViewHolder holder, int position) {
        Veterinarian vet=veterinarianList.get(position);
        String vetID=vet.getRegistrationNumber();
        holder.NameTextView.setText(vet.getName());
        holder.visitationTextView.setText(vet.getVisitationFee());
        holder.forwardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect for user to see vet details
                Intent intent=new Intent(context, ViewVet.class);
                intent.putExtra("Vet_RegNum",vet.getRegistrationNumber());
                context.startActivity(intent);
            }
        });
        holder.consultBtn.setOnClickListener(v -> {
            Intent intent=new Intent(context, FarmerVet_Chat.class);
            intent.putExtra("Vet_REGNUM",vet.getRegistrationNumber());
            intent.putExtra("Vet_Name",vet.getName());
            context.startActivity(intent);
        });

        holder.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent=new Intent(context, RequestVisitation.class);
                Bundle bundle=new Bundle();
                bundle.putString("VetID",vetID);
                bundle.putString("VetName",vet.getName());
                bundle.putString("VetProfilePicUrl",vet.getProfilePicUrl());
                newIntent.putExtras(bundle);
                context.startActivity(newIntent);
//                AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
//                alertDialog.setTitle("Choose Visitation Location");
//                int checkedItem=1;
//                alertDialog.setSingleChoiceItems(locationOptions, checkedItem, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case 0:
//                                getFarmerLocation();
//                                dialog.dismiss();
//                                if(Latitude!=null && Longitude!=null){
//                                    payVisit(vetID);
//                                }else{
//                                    Toast.makeText(context,"Something wrong happened",Toast.LENGTH_SHORT).show();
//                                }
//                                break;
//                            case 1:
//                                SelectLocation(context);
//                                dialog.dismiss();
//                                if(Latitude!=null && Longitude!=null){
//                                    payVisit(vetID);
//                                }else{
//                                    Toast.makeText(context,"Something wrong happened",Toast.LENGTH_SHORT).show();
//                                }
//                                break;
//                        }
//                    }
//                });
//                AlertDialog alert = alertDialog.create();
//                alert.setCanceledOnTouchOutside(false);
//                alert.show();
//                if(Latitude!=null && Longitude!=null){
//                    ref.child(vetID).child("Location").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            double vetLatitude= Double.parseDouble(snapshot.child("Latitude").getValue(String.class));
//                            double vetLongitude=Double.parseDouble(snapshot.child("Longitude").getValue(String.class));
//                            Location vetLocation=new Location("VetLocation");
//                            vetLocation.setLatitude(vetLatitude);
//                            vetLocation.setLongitude(vetLongitude);
//                            Location farmerLocation=new Location("FarmerLocation");
//                            farmerLocation.setLatitude(Double.parseDouble(Latitude));
//                            farmerLocation.setLongitude(Double.parseDouble(Longitude));
//                            distanceBtw=(vetLocation.distanceTo(farmerLocation))/1000;
//                            int visitationFee;
//                            if(distanceBtw>0 && distanceBtw<=5){
//                                visitationFee= (int) (distanceBtw * VISIT_COST_PER_5_KILOMETRE);
//                            }else if(distanceBtw>5 && distanceBtw<=10){
//                                visitationFee= (int) (distanceBtw * VISIT_COST_PER_10_KILOMETRE);
//                            }else{
//                                visitationFee= (int) (distanceBtw * VISIT_COST_PER_ABOVE_10_KILOMETRE);
//                            }
//                            Log.i(TAG, "Visitation Fee: "+visitationFee+" Distance between"+distanceBtw);
//                            payVisit(vet.getRegistrationNumber(), String.valueOf(visitationFee));
////                            bottomSheetDialog.dismiss();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.d(TAG, "onCancelled: "+error.getMessage());
////                            bottomSheetDialog.dismiss();
//                            Toast.makeText(context,"Something wrong Happened.Please try Again Later",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getFarmerLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Latitude= String.valueOf(location.getLatitude());
                    Longitude= String.valueOf(location.getLongitude());
                }
            }
        });
    }
//    private void pickAddress(String name) {
//        bottomSheetDialog=new BottomSheetDialog(context);
//        bottomSheetDialog.setContentView(R.layout.request_visit_bottom_sheet);
//        TextView vetNameTextView=bottomSheetDialog.findViewById(R.id.vetNameTextView);
//        locationTextView=bottomSheetDialog.findViewById(R.id.locationTxt);
//        Button cancelBtn=bottomSheetDialog.findViewById(R.id.cancelBtn);
//        Button OkBtn=bottomSheetDialog.findViewById(R.id.okStatusBtn);
//        locationPickerImageView=bottomSheetDialog.findViewById(R.id.pickLocationImageView);
//
//        vetNameTextView.setText(name);
//        locationPickerImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //open location Place Picker
//                SelectLocation(context);
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetDialog.dismiss();
//            }
//        });
//        OkBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(TextUtils.isEmpty(Address) || TextUtils.isEmpty(Latitude) || TextUtils.isEmpty(Longitude)){
//                    bottomSheetDialog.dismiss();
//                    Toast.makeText(context,"Please Select Your Location",Toast.LENGTH_SHORT).show();
//                    return;
//                }else{
//                    ref.child(VetID).child("Location").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            double vetLatitude= Double.parseDouble(snapshot.child("Latitude").getValue(String.class));
//                            double vetLongitude=Double.parseDouble(snapshot.child("Longitude").getValue(String.class));
//                            Location vetLocation=new Location("VetLocation");
//                            vetLocation.setLatitude(vetLatitude);
//                            vetLocation.setLongitude(vetLongitude);
//                            Location farmerLocation=new Location("FarmerLocation");
//                            farmerLocation.setLatitude(Double.parseDouble(Latitude));
//                            farmerLocation.setLongitude(Double.parseDouble(Longitude));
//                            distanceBtw=(vetLocation.distanceTo(farmerLocation))* 0.001;
//                            int visitationFee;
//                            if(distanceBtw>0 && distanceBtw<=5){
//                                visitationFee= (int) (distanceBtw * VISIT_COST_PER_5_KILOMETRE);
//                            }else if(distanceBtw>5 && distanceBtw<=10){
//                                visitationFee= (int) (distanceBtw * VISIT_COST_PER_10_KILOMETRE);
//                            }else{
//                                visitationFee= (int) (distanceBtw * VISIT_COST_PER_ABOVE_10_KILOMETRE);
//                            }
//                            Log.i(TAG, "Visitation Fee: "+visitationFee+" Distance between"+distanceBtw);
//                            payVisit(vet.getRegistrationNumber(), String.valueOf(visitationFee));
//                            bottomSheetDialog.dismiss();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.d(TAG, "onCancelled: "+error.getMessage());
//                            bottomSheetDialog.dismiss();
//                            Toast.makeText(context,"Something wrong Happened.Please try Again Later",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//            }
//        });
//        bottomSheetDialog.show();
//    }

//    private void SaveRequest(String RegistrationNumber,String Fee) {
//        final DatabaseReference ref;
//        String clientID= Prevalent.currentOnlineFarmer.getFarmerID();
////        ref=mRef.child(RegistrationNumber);
//        String visitID=mRef.push().getKey();
//        VisitRequest visitRequest=new VisitRequest(RegistrationNumber,Address,Latitude,Longitude,Fee,clientID,"Pending",visitID,false);
//        assert visitID != null;
//        mRef.child(visitID).setValue(visitRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(context,"Request Recorded",Toast.LENGTH_SHORT).show();
//                }else{
//                    Log.d(TAG, "onComplete: Something Happened");
//                }
//                progressDialog.dismiss();
//                bottomSheetDialog.dismiss();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: "+e.getMessage());
//                Toast.makeText(context,"Failed to Record your visit Request",Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//                bottomSheetDialog.dismiss();
//            }
//        });
//    }

    private void payVisit(String RegistrationNumber) {
        AlertDialog.Builder paymentDailogBuilder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    if(Latitude!=null && Longitude!=null){
                        ref.child(RegistrationNumber).child("Location").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double vetLatitude= Double.parseDouble(snapshot.child("Latitude").getValue(String.class));
                                double vetLongitude=Double.parseDouble(snapshot.child("Longitude").getValue(String.class));
                                Location vetLocation=new Location("VetLocation");
                                vetLocation.setLatitude(vetLatitude);
                                vetLocation.setLongitude(vetLongitude);
                                Location farmerLocation=new Location("FarmerLocation");
                                farmerLocation.setLatitude(Double.parseDouble(Latitude));
                                farmerLocation.setLongitude(Double.parseDouble(Longitude));
                                distanceBtw=(vetLocation.distanceTo(farmerLocation))/1000;
                                int visitationFee;
                                if(distanceBtw>0 && distanceBtw<=5){
                                    visitationFee= (int) (distanceBtw * VISIT_COST_PER_5_KILOMETRE);
                                }else if(distanceBtw>5 && distanceBtw<=10){
                                    visitationFee= (int) (distanceBtw * VISIT_COST_PER_10_KILOMETRE);
                                }else{
                                    visitationFee= (int) (distanceBtw * VISIT_COST_PER_ABOVE_10_KILOMETRE);
                                }
                                Log.i(TAG, "Visitation Fee: "+visitationFee+" Distance between"+distanceBtw);
                                makeVisitationPayment(visitationFee,RegistrationNumber,PhoneNumber);
//                            bottomSheetDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, "onCancelled: "+error.getMessage());
//                            bottomSheetDialog.dismiss();
                                Toast.makeText(context,"Something wrong Happened.Please try Again Later",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                //                getAccessToken();

//                makeVisitationPayment(Fee,RegistrationNumber,PhoneNumber);
//                Retrofit.Builder retrofit=new Retrofit.Builder().addConverterFactory();
                progressDialog.show();
            }
        });
        AlertDialog paymentDailog=paymentDailogBuilder.create();
        paymentDailog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        paymentDailog.show();
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentDailog.dismiss();
            }
        });
//        SaveRequest(RegistrationNumber,Fee);
    }
//    public void getAccessToken(){
//        darajaApiClient.setGetAccessToken(true);
//        darajaApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
//            @Override
//            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
//                if(response.isSuccessful()){
//                    darajaApiClient.setAuthToken(response.body().accessToken);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AccessToken> call, Throwable t) {
//                Log.e(TAG, "onFailure: "+t.toString());
//            }
//        });
//    }

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
//                    Toast.makeText(context,"Request Recorded",Toast.LENGTH_SHORT).show();
//                }else{
//                    Log.d(TAG, "onComplete: Something Happened");
//                }
//                progressDialog.dismiss();
//                bottomSheetDialog.dismiss();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: "+e.getMessage());
//                Toast.makeText(context,"Failed to Record your visit Request",Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//                bottomSheetDialog.dismiss();
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
                    Toast.makeText(context,"Visit Request Recorded",Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).i("Mpesa Worked: ");

                }else{
                    Toast.makeText(context,"Something Wrong Happened Please Try Again",Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).i("Mpesa Failed: ");
                }
            }

            @Override
            public void onFailure(Call<STKResponse> call, Throwable t) {
                Timber.tag(TAG).d("onFailure: Something wrong Happened");
            }
        });
//        String timestamp= Utils.getTimeStamp();
//        STKPUSH stkpush=new STKPUSH(
//                BUSINESS_SHORT_CODE,
//                Utils.STKPUSHPassword(BUSINESS_SHORT_CODE,PASSKEY,timestamp),
//                timestamp,
//                TRANSACTION_TYPE,
//                String.valueOf(visitationFee),
//                Utils.refactorPhoneNumber(phoneNumber),
//                PARTYB,
//                Utils.refactorPhoneNumber(phoneNumber),
//                CALLBACKURL,
//                RegistrationNumber,
//                "Visitation Fee"
//        );
//        darajaApiClient.setGetAccessToken(false);
//        darajaApiClient.mpesaService().sendPush(stkpush).enqueue(new Callback<STKPUSH>() {
//            @Override
//            public void onResponse(Call<STKPUSH> call, Response<STKPUSH> response) {
//                try{
//                    if(response.isSuccessful()){
//                        progressDialog.dismiss();
//                        Log.d(TAG, "The Response is: "+response.body());
//                    }else{
//                        progressDialog.dismiss();
//                        Timber.d("Response %s, ",response.errorBody().string());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<STKPUSH> call, Throwable t) {
//                progressDialog.dismiss();
//                Log.e(TAG, "onFailure: "+t.toString());
//            }
//        });
    }

    private void SelectLocation(Context context) {
        Intent intent = new Intent(context, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(SimplePlacePicker.API_KEY,Api_Key);
        bundle.putString(SimplePlacePicker.COUNTRY,Country);
        bundle.putString(SimplePlacePicker.LANGUAGE,Language);
        bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,mSupportedAreas);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
    }
    public void UpdateLocation(String address,String latitude,String longitude){
        Address=address;
        Latitude=latitude;
        Longitude=longitude;
//        locationTextView.setText(Address);
//        locationPickerImageView.setVisibility(View.GONE);
//        locationTextView.setVisibility(View.VISIBLE);
    }
    @Override
    public int getItemCount() {
        return veterinarianList.size();
    }

    @Override
    public Filter getFilter() {
        return vetFilter;
    }
    private final Filter vetFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Veterinarian> filterVets=new ArrayList<>();
            if(constraint == null || constraint.length()==0 ){
                filterVets.addAll(vetList);
            }else{
                String searchText=constraint.toString().toLowerCase();
                for(Veterinarian vet:vetList){
                    if(vet.getName().toLowerCase().contains(searchText)){
                        filterVets.add(vet);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filterVets;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            veterinarianList.clear();
            veterinarianList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ImageView vetProfilePic;
        private final RelativeLayout forwardLayout;
        private final TextView NameTextView,visitationTextView;
        private final Button consultBtn,requestButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           vetProfilePic=itemView.findViewById(R.id.vetProfilePic);
           forwardLayout=itemView.findViewById(R.id.forwardRelativeLayout);
           NameTextView=itemView.findViewById(R.id.vetNameTextView);
           visitationTextView=itemView.findViewById(R.id.vetVisitationFee);
           consultBtn=itemView.findViewById(R.id.consultBtn);
           requestButton=itemView.findViewById(R.id.visitBtn);
        }
    }
}