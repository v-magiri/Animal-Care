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

import com.magiri.animalcare.Model.Breeding;
import com.magiri.animalcare.R;

import java.util.List;

public class AnimalBreedingAdapter extends RecyclerView.Adapter<AnimalBreedingAdapter.MyViewHolder> implements Filterable {
    Context context;
    List<Breeding> breedingList;

    public AnimalBreedingAdapter(Context context, List<Breeding> breedingList) {
        this.context = context;
        this.breedingList = breedingList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.breed_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Breeding breeding=breedingList.get(position);
        holder.breedingDateTxt.setText(breeding.getBreedingDate());
        holder.costTxt.setText(breeding.getCost());
        holder.animalNameTxt.setText(breeding.getAnimalID());
        holder.bullNameTxt.setText(breeding.getBullName());
    }

    @Override
    public int getItemCount() {
        return breedingList.size();
    }
    @Override
    public Filter getFilter() {
        return diseaseAdapter;
    }
    private final Filter diseaseAdapter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView animalNameTxt,bullNameTxt,costTxt,breedingDateTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTxt=itemView.findViewById(R.id.animalNameTxt);
            bullNameTxt=itemView.findViewById(R.id.bullNameTextView);
            costTxt=itemView.findViewById(R.id.costTextView);
            breedingDateTxt=itemView.findViewById(R.id.breedingDateTextView);
        }
    }
}
