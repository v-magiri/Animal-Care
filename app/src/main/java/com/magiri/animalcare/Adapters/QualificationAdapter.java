package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.magiri.animalcare.R;

import java.util.List;

public class QualificationAdapter extends RecyclerView.Adapter<QualificationAdapter.MyViewHolder> {
    Context context;
    private String[] qualificationList;

    public QualificationAdapter(Context context, String[] qualificationList) {
        this.context = context;
        this.qualificationList = qualificationList;
    }

    @NonNull
    @Override
    public QualificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull QualificationAdapter.MyViewHolder holder, int position) {
        holder.skillTxt.setText(qualificationList[position]);
    }

    @Override
    public int getItemCount() {
        return qualificationList.length;
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView skillTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            skillTxt=itemView.findViewById(R.id.skillTextView);
        }
    }

}
