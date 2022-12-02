package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.magiri.animalcare.Model.DiseaseTreatment;
import com.magiri.animalcare.R;

import java.util.List;

public class AnimalHealthRecordAdapter extends
        RecyclerView.Adapter<AnimalHealthRecordAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<DiseaseTreatment> diseaseTreatmentList;

    public AnimalHealthRecordAdapter(Context context, List<DiseaseTreatment> diseaseTreatmentList) {
        this.context = context;
        this.diseaseTreatmentList = diseaseTreatmentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.treatment_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DiseaseTreatment diseaseTreatment=diseaseTreatmentList.get(position);
        holder.animalNameTxt.setText(diseaseTreatment.getAnimalID());
        holder.treatmentTxt.setText(diseaseTreatment.getTreatment());
        holder.treatmentDateTxt.setText(diseaseTreatment.getTreatmentDate());
        holder.costTxt.setText(diseaseTreatment.getCost());
    }

    @Override
    public int getItemCount() {
        return diseaseTreatmentList.size();
    }
    @Override
    public Filter getFilter() {
        return treatmentFilter;
    }
    private final Filter treatmentFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    };
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView animalNameTxt,costTxt,treatmentTxt,treatmentDateTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTxt=itemView.findViewById(R.id.animalNameTxt);
            costTxt=itemView.findViewById(R.id.costTextView);
            treatmentTxt=itemView.findViewById(R.id.treatmentTextView);
            treatmentDateTxt=itemView.findViewById(R.id.treatmentDateTextView);
        }
    }
}
