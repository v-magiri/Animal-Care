package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Farmer;
import com.magiri.animalcare.R;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder> {
    private static final String TAG = "RatingAdapter";
    Context context;
    List<HashMap<String,Object>> ratingList;
    private DatabaseReference mRef;

    public RatingAdapter(Context context, List<HashMap<String, Object>> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
        mRef= FirebaseDatabase.getInstance().getReference("Farmers");
    }

    @NonNull
    @Override
    public RatingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAdapter.MyViewHolder holder, int position) {
        HashMap<String,Object> map=ratingList.get(position);
        holder.farmerRating.setRating(Float.valueOf(map.get("Rating").toString()));
        try {
            String Comment=map.get("Comment").toString();
            if(TextUtils.isEmpty(Comment)){
                holder.reviewLayout.setVisibility(View.GONE);
            }else{
                holder.commentTxt.setText(Comment);
            }
        }catch (IOError error){
            System.out.println("Something wrong Happened");
        }
        setFarmerDetails(map.get("FarmerID").toString(),holder.farmerProfilePic,holder.FarmerNameTxt);
    }

    private void setFarmerDetails(String farmerID, CircleImageView farmerProfilePic, TextView farmerNameTxt) {
        mRef.child(farmerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Farmer farmer=snapshot.getValue(Farmer.class);
                String farmerProfilePicUrl=farmer.getProfilePicUrl();
                if(TextUtils.isEmpty(farmerProfilePicUrl)){
                    Glide.with(context).load(R.drawable.ic_farmer).into(farmerProfilePic);
                }else{
                    Glide.with(context).load(farmerProfilePicUrl).into(farmerProfilePic);
                }
                farmerNameTxt.setText(farmer.getFarmerName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        RatingBar farmerRating;
        TextView commentTxt;
        TextView FarmerNameTxt;
        CircleImageView farmerProfilePic;
        private RelativeLayout reviewLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            farmerProfilePic=itemView.findViewById(R.id.clientProfilePicImageView);
            commentTxt=itemView.findViewById(R.id.commentTextView);
            FarmerNameTxt=itemView.findViewById(R.id.clientNameTextView);
            farmerRating=itemView.findViewById(R.id.farmerRating);
            reviewLayout=itemView.findViewById(R.id.rootLayout);
        }
    }
}
