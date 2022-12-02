package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Animal;
import com.magiri.animalcare.Model.Breeding;
import com.magiri.animalcare.Model.DiseaseTreatment;
import com.magiri.animalcare.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnimalHealthRecordAdapter extends
        RecyclerView.Adapter<AnimalHealthRecordAdapter.MyViewHolder> implements Filterable {

    private static final String TAG = "AnimalHealthAdapter";
    Context context;
    List<DiseaseTreatment> diseaseTreatmentList;
    private DatabaseReference mRef;
    List<DiseaseTreatment> treatmentList;

    public AnimalHealthRecordAdapter(Context context, List<DiseaseTreatment> diseaseTreatmentList) {
        this.context = context;
        this.diseaseTreatmentList = diseaseTreatmentList;
        mRef= FirebaseDatabase.getInstance().getReference("Animals");
        treatmentList=new ArrayList<>(diseaseTreatmentList);
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
        getAnimalProfile(diseaseTreatment.getAnimalID(),holder.animalImageView);
    }

    private void getAnimalProfile(String animalID, CircleImageView animalImageView) {
        mRef.child(animalID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Animal animal=snapshot.getValue(Animal.class);
                String animalProfileImgUrl=animal.getAnimalImageUrl();

                if(TextUtils.isEmpty(animalProfileImgUrl)){
                    Glide.with(context).load(R.drawable.ic_cow).into(animalImageView);
                }else{
                    Glide.with(context).load(animalProfileImgUrl).into(animalImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
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
            List<DiseaseTreatment> filterTreatment=new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                filterTreatment.addAll(treatmentList);
            }else{
                String searchText=constraint.toString().toLowerCase();
                for(DiseaseTreatment treatment:treatmentList){
                    if(treatment.getAnimalID().toLowerCase().contains(searchText)){
                        filterTreatment.add(treatment);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=treatmentFilter;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            diseaseTreatmentList.clear();
            diseaseTreatmentList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView animalNameTxt,costTxt,treatmentTxt,treatmentDateTxt;
        CircleImageView animalImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTxt=itemView.findViewById(R.id.animalNameTxt);
            costTxt=itemView.findViewById(R.id.costTextView);
            treatmentTxt=itemView.findViewById(R.id.treatmentTextView);
            treatmentDateTxt=itemView.findViewById(R.id.treatmentDateTextView);
            animalImageView=itemView.findViewById(R.id.animalImageView);
        }
    }
}
