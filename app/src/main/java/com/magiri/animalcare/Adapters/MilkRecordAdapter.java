package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.magiri.animalcare.Model.MilkRecord;
import com.magiri.animalcare.R;

import java.util.List;

public class MilkRecordAdapter extends RecyclerView.Adapter<MilkRecordAdapter.MyViewHolder> {
    Context context;
    List<MilkRecord> milkRecordList;

    public MilkRecordAdapter(Context context, List<MilkRecord> milkRecordList) {
        this.context = context;
        this.milkRecordList = milkRecordList;
    }

    @NonNull
    @Override
    public MilkRecordAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.milk_record_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MilkRecordAdapter.MyViewHolder holder, int position) {
        MilkRecord milkRecord=milkRecordList.get(position);
        holder.QuantityTxt.setText(milkRecord.getQuantity());
        holder.milkTimeTxt.setText(milkRecord.getMilkTime());
        holder.milkDateTxt.setText(milkRecord.getDate());
    }

    @Override
    public int getItemCount() {
        return milkRecordList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView milkDateTxt,milkTimeTxt,QuantityTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            milkDateTxt=itemView.findViewById(R.id.milkDateTxt);
            milkTimeTxt=itemView.findViewById(R.id.milkTimeTxt);
            QuantityTxt=itemView.findViewById(R.id.quantityTxt);
        }
    }

}
