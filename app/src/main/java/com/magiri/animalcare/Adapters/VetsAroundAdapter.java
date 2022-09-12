package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.magiri.animalcare.FarmerVet_Chat;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.R;
import com.magiri.animalcare.ViewVet;

import java.util.List;

public class VetsAroundAdapter extends RecyclerView.Adapter<VetsAroundAdapter.MyViewHolder> {
    Context context;
    List<Veterinarian> veterinarianList;

    public VetsAroundAdapter(Context context, List<Veterinarian> veterinarianList) {
        this.context = context;
        this.veterinarianList = veterinarianList;
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
                //request for vet visit
            }
        });
    }

    @Override
    public int getItemCount() {
        return veterinarianList.size();
    }
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