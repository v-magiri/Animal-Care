package com.magiri.animalcare.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.ViewVet;
import com.magiri.animalcare.darajaApi.RestClient;
import com.magiri.animalcare.darajaApi.STKResponse;
import com.magiri.animalcare.darajaApi.Utils;

import java.io.IOError;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
    double Latitude,Longitude;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private static DatabaseReference mRef,ref;
    ProgressDialog progressDialog;
    List<Veterinarian> vetList;
    String[] locationOptions;

    public VetsAroundAdapter(Context context, List<Veterinarian> veterinarianList) {
        this.context = context;
        this.veterinarianList = veterinarianList;
        mRef= FirebaseDatabase.getInstance().getReference("Ratings");
        ref=FirebaseDatabase.getInstance().getReference("Veterinarian");
        progressDialog=new ProgressDialog(context);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context);
        getFarmerLocation();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");
        vetList=new ArrayList<>(veterinarianList);
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
        holder.NameTextView.setText(vet.getName());
        String profilePicUrl=vet.getProfilePicUrl();
        if(TextUtils.isEmpty(profilePicUrl)){
            Glide.with(context).load(R.drawable.ic_vet).into(holder.vetProfilePic);
        }else{
            Glide.with(context).load(profilePicUrl).into(holder.vetProfilePic);
        }
        try{
            mRef.child(vet.getRegistrationNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int reviews= (int) snapshot.getChildrenCount();
                    holder.reviewsText.setText(reviews+" Reviews");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: "+error.getMessage());
                }
            });
        }catch (IOError e){
            System.out.println("Not yet Rated");
        }
        holder.skillsTextView.setText(vet.getSkill());
        holder.vetCardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vetIntent=new Intent(context,ViewVet.class);
                vetIntent.putExtra("VETID",vet.getRegistrationNumber());
                context.startActivity(vetIntent);

            }
        });
        setVetDetails(vet.getRegistrationNumber(),holder.distanceBtnTxt);
    }

    private void setVetDetails(String registrationNumber, TextView distanceBtnTxt) {
        ref.child(registrationNumber).child("Location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double vetLatitude= Double.parseDouble(snapshot.child("Latitude").getValue(String.class));
                double vetLongitude=Double.parseDouble(snapshot.child("Longitude").getValue(String.class));
                if(Latitude!=0 && Longitude!=0){
                    Location vetLocation=new Location("VetLocation");
                    vetLocation.setLatitude(vetLatitude);
                    vetLocation.setLongitude(vetLongitude);
                    Location farmerLocation=new Location("FarmerLocation");
                    farmerLocation.setLatitude(Latitude);
                    farmerLocation.setLongitude(Longitude);
                    double distanceBtw=(vetLocation.distanceTo(farmerLocation))/1000;
                    distanceBtnTxt.setText(new DecimalFormat("#.##").format(distanceBtw)+" km");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getFarmerLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Latitude= location.getLatitude();
                    Longitude= location.getLongitude();
                }
            }
        });
    }

    private void makeVisitationPayment(int visitationFee,String RegistrationNumber,String phoneNumber) {
        visitationFee=1;
        final DatabaseReference ref;
        String clientID= Prevalent.currentOnlineFarmer.getFarmerID();
        String visitID=mRef.push().getKey();
        assert visitID != null;
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
        private final TextView NameTextView,reviewsText,distanceBtnTxt,skillsTextView;
        private final Button consultBtn,requestButton;
        private final CardView vetCardItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           vetProfilePic=itemView.findViewById(R.id.vetProfilePic);
           forwardLayout=itemView.findViewById(R.id.forwardRelativeLayout);
           NameTextView=itemView.findViewById(R.id.vetNameTextView);
           reviewsText=itemView.findViewById(R.id.numberOfRating);
            distanceBtnTxt=itemView.findViewById(R.id.distanceBtnTxt);
           consultBtn=itemView.findViewById(R.id.consultBtn);
           requestButton=itemView.findViewById(R.id.visitBtn);
           vetCardItem=itemView.findViewById(R.id.vetItemLayout);
            skillsTextView=itemView.findViewById(R.id.skillTxt);
        }
    }
}