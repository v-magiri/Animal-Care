package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Farmer;
import com.magiri.animalcare.Model.Post;
import com.magiri.animalcare.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Forum_MessageAdapter extends RecyclerView.Adapter<Forum_MessageAdapter.ViewHolder> {
    private static final String TAG = "Forum_MessageAdapter";
    Context context;
    List<Post> postList;

    public Forum_MessageAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public Forum_MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_message_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Forum_MessageAdapter.ViewHolder holder, int position) {
        Post post=postList.get(position);
        if(post.getQuestionimage()==null){
            holder.PostImg.setVisibility(View.GONE);
        }
        holder.topicTxt.setText(post.getTopic());
        holder.postMessageTxt.setText(post.getQuestion());
        holder.dateTxt.setText(post.getDate());

        holder.PostImg.setVisibility(View.VISIBLE);
//        Picasso.get().load(product.getProductImageUri()).into(holder.productImage);
        Glide.with(context).load(post.getQuestionimage()).into(holder.PostImg);

//        set the publisher information
        setSenderInformation(holder.senderProfilePic,holder.senderNameTxt,post.getPublisher());
    }

    private void setSenderInformation(CircleImageView senderProfilePic, TextView senderNameTxt, String publisherID) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Farmers").child(publisherID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Farmer farmer=snapshot.getValue(Farmer.class);
                    if(farmer.getProfilePicUrl()==null){
                        Glide.with(context).load(R.drawable.ic_farmer).into(senderProfilePic);
                    }else{
                        Glide.with(context).load(farmer.getProfilePicUrl()).into(senderProfilePic);
                    }
                    senderNameTxt.setText(farmer.getFarmerName());
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
        return postList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView PostImg;
        private CircleImageView senderProfilePic;
        private TextView senderNameTxt,topicTxt,dateTxt;
        private LinearLayout likeLayout,dislikeLayout,commentLayout;
        private ExpandableTextView postMessageTxt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderNameTxt=itemView.findViewById(R.id.senderNameTxt);
            senderProfilePic=itemView.findViewById(R.id.messenger_ProfilePic);
            PostImg=itemView.findViewById(R.id.messageImage);
            topicTxt=itemView.findViewById(R.id.topicTxt);
            dateTxt=itemView.findViewById(R.id.asked_on_textview);
            postMessageTxt=itemView.findViewById(R.id.postMessageTxt);

            likeLayout=itemView.findViewById(R.id.likeLayout);
            dislikeLayout=itemView.findViewById(R.id.dislikeLayout);
            commentLayout=itemView.findViewById(R.id.commentLayout);

        }
    }

}
