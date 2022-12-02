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
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Prevalent;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnimalBreedingAdapter extends RecyclerView.Adapter<AnimalBreedingAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "AnimalBreedingAdapter";
    Context context;
    List<Breeding> breedingList;
    private final DatabaseReference ref;
    List<Breeding> breedList;
    private final DatabaseReference databaseReference;
    private String FarmerID;

    public AnimalBreedingAdapter(Context context, List<Breeding> breedingList) {
        this.context = context;
        this.breedingList = breedingList;
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        ref= FirebaseDatabase.getInstance().getReference("Veterinarian");
        databaseReference=FirebaseDatabase.getInstance().getReference("Animals").child(FarmerID);
        breedList=new ArrayList<>(breedingList);
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
        String VETID=breeding.getVetID();
        ref.child(VETID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veterinarian vet=snapshot.getValue(Veterinarian.class);
                holder.vetNameTxt.setText(vet.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
        getAnimalProfile(breeding.getAnimalID(),holder.animalImageView);
    }

    private void getAnimalProfile(String animalID, CircleImageView animalImageView) {
        databaseReference.child(animalID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Animal animal=snapshot.getValue(Animal.class);
                assert animal != null;
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
        return breedingList.size();
    }
    @Override
    public Filter getFilter() {
        return diseaseAdapter;
    }
    private final Filter diseaseAdapter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Breeding> filterBreeding=new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                filterBreeding.addAll(breedList);
            }else{
                String searchText=constraint.toString().toLowerCase();
                for(Breeding breeding:breedList){
                    if(breeding.getAnimalID().toLowerCase().contains(searchText)){
                        filterBreeding.add(breeding);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filterBreeding;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            breedingList.clear();
            breedingList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView animalNameTxt,bullNameTxt,costTxt,breedingDateTxt,vetNameTxt;
        CircleImageView animalImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTxt=itemView.findViewById(R.id.animalNameTxt);
            bullNameTxt=itemView.findViewById(R.id.bullNameTextView);
            costTxt=itemView.findViewById(R.id.costTextView);
            breedingDateTxt=itemView.findViewById(R.id.breedingDateTextView);
            vetNameTxt=itemView.findViewById(R.id.vetNameTextView);
            animalImageView=itemView.findViewById(R.id.animalImageView);
        }
    }
}
