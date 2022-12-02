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
import com.magiri.animalcare.Model.MilkRecord;
import com.magiri.animalcare.R;

import java.util.ArrayList;
import java.util.List;

public class ProductionAdapter extends RecyclerView.Adapter<ProductionAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<MilkRecord> milkRecordList;
    List<MilkRecord> prodList;

    public ProductionAdapter(Context context, List<MilkRecord> milkRecordList) {
        this.context = context;
        this.milkRecordList = milkRecordList;
        prodList=new ArrayList<>(milkRecordList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.production_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MilkRecord milkRecord=milkRecordList.get(position);
        holder.quantityTxt.setText(String.valueOf(milkRecord.getQuantity()));
        holder.milkingTimeTxt.setText(milkRecord.getMilkTime());
        holder.animalNameTxt.setText(milkRecord.getAnimalName());
        holder.dateTxt.setText(milkRecord.getDate());
    }

    @Override
    public int getItemCount() {
        return milkRecordList.size();
    }

    @Override
    public Filter getFilter() {
        return productionFilter;
    }
    private final Filter productionFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MilkRecord> filterProduction=new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                filterProduction.addAll(prodList);
            }else{
                String searchText=constraint.toString().toLowerCase();
                for(MilkRecord record:prodList){
                    if(record.getAnimalName().toLowerCase().contains(searchText)){
                        filterProduction.add(record);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filterProduction;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            milkRecordList.clear();
            milkRecordList.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView animalNameTxt,dateTxt,milkingTimeTxt,quantityTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTxt=itemView.findViewById(R.id.animalNameTxt);
            dateTxt=itemView.findViewById(R.id.milkDateTextView);
            milkingTimeTxt=itemView.findViewById(R.id.milkingTimeTextView);
            quantityTxt=itemView.findViewById(R.id.quantityTextView);
        }
    }
}
