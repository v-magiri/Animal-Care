package com.magiri.animalcare.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magiri.animalcare.FarmerVet_Chat;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.Model.VisitRequest;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.ViewVet;

import java.util.ArrayList;
import java.util.List;

public class VetsAroundAdapter extends RecyclerView.Adapter<VetsAroundAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "VetsAroundAdapter";
    Context context;
    List<Veterinarian> veterinarianList;
    private static String Country,Language,Api_Key;
    private static String Address,Latitude,Longitude;
    private static String []mSupportedAreas={""};
    Activity activity;
    TextView locationTextView;
    ImageView locationPickerImageView;
    private static DatabaseReference mRef;
    ProgressDialog progressDialog;
    BottomSheetDialog bottomSheetDialog;
    List<Veterinarian> vetList;
//    private

    public VetsAroundAdapter(Context context, List<Veterinarian> veterinarianList) {
        this.context = context;
        this.veterinarianList = veterinarianList;
        Country="Kenya";
        Language="English";
        Api_Key=context.getResources().getString(R.string.maps_ApiKey);
        activity= (Activity) context;
        mRef= FirebaseDatabase.getInstance().getReference("VisitRequest");
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Saving Request in the System");
        vetList=new ArrayList<>(veterinarianList);
    }

    @NonNull
    @Override
    public VetsAroundAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.vet_around_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VetsAroundAdapter.MyViewHolder holder, int position) {
        Veterinarian vet=veterinarianList.get(position);
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
        holder.consultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FarmerVet_Chat.class);
                intent.putExtra("Vet_REGNUM",vet.getRegistrationNumber());
                intent.putExtra("Vet_Name",vet.getName());
                context.startActivity(intent);
            }
        });

        holder.requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog=new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.request_visit_bottom_sheet);
                TextView vetNameTextView=bottomSheetDialog.findViewById(R.id.vetNameTextView);
                locationTextView=bottomSheetDialog.findViewById(R.id.locationTxt);
                Button cancelBtn=bottomSheetDialog.findViewById(R.id.cancelBtn);
                Button OkBtn=bottomSheetDialog.findViewById(R.id.okStatusBtn);
                locationPickerImageView=bottomSheetDialog.findViewById(R.id.pickLocationImageView);

                vetNameTextView.setText(vet.getName());
                locationPickerImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open location Place Picker
                        SelectLocation(context);
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
                        makeVisitiationPayment(vet.getVisitationFee());
                        validatedSelectedLocation(vet.getRegistrationNumber(),vet.getVisitationFee());
//                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();
            }
        });
    }

    private void makeVisitiationPayment(String visitationFee) {

    }

    private void SaveRequest(String RegistrationNumber,String Fee) {
        final DatabaseReference ref;
        String clientID=Prevalent.currentOnlineFarmer.getFarmerID();
//        ref=mRef.child(RegistrationNumber);
        String visitID=mRef.push().getKey();
        VisitRequest visitRequest=new VisitRequest(RegistrationNumber,Address,Latitude,Longitude,Fee,clientID,"Pending",visitID,false);
        assert visitID != null;
        mRef.child(visitID).setValue(visitRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"Request Recorded",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context,"Failed to Record your visit Request",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void validatedSelectedLocation(String RegistrationNumber,String Fee) {
        if(TextUtils.isEmpty(Address) || TextUtils.isEmpty(Latitude) || TextUtils.isEmpty(Longitude)){
            Toast.makeText(context,"Please Select Your Location",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        SaveRequest(RegistrationNumber,Fee);
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
        locationTextView.setText(address);
        locationPickerImageView.setVisibility(View.GONE);
        locationTextView.setVisibility(View.VISIBLE);
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

    protected class MyViewHolder extends RecyclerView.ViewHolder{
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