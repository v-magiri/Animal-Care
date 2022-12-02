package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.magiri.animalcare.Model.DiseaseTreatment;
import com.magiri.animalcare.R;

import java.util.List;

public class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.MyViewHolder>{
    Context context;
    List<DiseaseTreatment> diseaseTreatmentList;

    public TreatmentAdapter(Context context, List<DiseaseTreatment> diseaseTreatmentList) {
        this.context = context;
        this.diseaseTreatmentList = diseaseTreatmentList;
    }

    @NonNull
    @Override
    public TreatmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.health_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TreatmentAdapter.MyViewHolder holder, int position) {
        DiseaseTreatment diseaseTreatment=diseaseTreatmentList.get(position);
        holder.DateTxt.setText(diseaseTreatment.getTreatmentDate());
        holder.treatmentTxt.setText(diseaseTreatment.getTreatment());
        holder.costTxt.setText(diseaseTreatment.getCost());
        holder.DiagnosisTxt.setText(diseaseTreatment.getDiagnosis());

        holder.healthCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseTreatmentList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView DiagnosisTxt,treatmentTxt,costTxt,DateTxt;
        private CardView healthCard;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DiagnosisTxt=itemView.findViewById(R.id.diagnosisTxt);
            treatmentTxt=itemView.findViewById(R.id.treatmentTextView);
            costTxt=itemView.findViewById(R.id.treatmentCostTextView);
            DateTxt=itemView.findViewById(R.id.dateTextView);
            healthCard=itemView.findViewById(R.id.rootLayout);
        }
    }

}
