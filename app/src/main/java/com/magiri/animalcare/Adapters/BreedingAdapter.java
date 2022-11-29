package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.BreedingFragment;
import com.magiri.animalcare.Model.Breeding;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.R;

import java.util.List;

public class BreedingAdapter extends RecyclerView.Adapter<BreedingAdapter.MyViewHolder> {

    private static final String TAG = "BreedingAdapter";
    Context context;
    List<Breeding> breedingList;
    private DatabaseReference ref;

    public BreedingAdapter(Context context, List<Breeding> breedingList) {
        this.context = context;
        this.breedingList = breedingList;
        ref= FirebaseDatabase.getInstance().getReference("Veterinarian");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.breeding_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Breeding breeding = breedingList.get(position);
        holder.DateTxt.setText(breeding.getBreedingDate());
        holder.costTxt.setText(breeding.getCost());
        holder.bullNameTxt.setText(breeding.getBullName());
        setVetDetails(holder.vetNameTxt,breeding.getVetID());
    }

    private void setVetDetails(TextView vetNameTxt, String vetID) {
        ref.child(vetID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veterinarian veterinarian=snapshot.getValue(Veterinarian.class);
                vetNameTxt.setText(veterinarian.getName());
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

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView bullNameTxt,vetNameTxt,costTxt,DateTxt;
        private CardView healthCard;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bullNameTxt=itemView.findViewById(R.id.bullNameTextView);
            vetNameTxt=itemView.findViewById(R.id.vetNameTextView);
            costTxt=itemView.findViewById(R.id.costTextView);
            DateTxt=itemView.findViewById(R.id.dateTextView);
        }
    }

}
