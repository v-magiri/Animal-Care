package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Comment;
import com.magiri.animalcare.Model.Farmer;
import com.magiri.animalcare.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{
    private static final String TAG = "CommentAdapter";
    Context context;
    List<Comment> commentList;
    DatabaseReference databaseReference;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        Comment postComment=commentList.get(position);
        holder.dateTxt.setText(postComment.getDate());
        holder.commentTxt.setText(postComment.getComment());

        setPublisherDetails(holder.publisherImageView,holder.publisherNameTxt,postComment.getPublisher());
    }

    private void setPublisherDetails(ImageView publisherImageView, TextView publisherNameTxt, String publisher) {
        databaseReference.child("Farmers").child(publisher).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Farmer farmer=snapshot.getValue(Farmer.class);
                publisherNameTxt.setText(farmer.getFarmerName());
                if(!farmer.getProfilePicUrl().equals("")){
                    Glide.with(context).load(farmer.getProfilePicUrl()).into(publisherImageView);
                }else{
                    Glide.with(context).load(R.drawable.ic_farmer).into(publisherImageView);
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
        return commentList.size();
    }
    protected class MyViewHolder extends RecyclerView.ViewHolder{
        private final ImageView publisherImageView;
        private final TextView publisherNameTxt,commentTxt,dateTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            publisherImageView=itemView.findViewById(R.id.publisherProfilePic);
            publisherNameTxt=itemView.findViewById(R.id.publisherNameTxt);
            commentTxt=itemView.findViewById(R.id.commentTxt);
            dateTxt=itemView.findViewById(R.id.commentDateTxt);
        }
    }

}
