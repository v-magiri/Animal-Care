package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.magiri.animalcare.CommentActivity;
import com.magiri.animalcare.Model.Farmer;
import com.magiri.animalcare.Model.Post;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Session;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.MyViewHolder> {

    private static final String TAG = "Post Adapter";
    Context context;
    List<Post> postList;
    DatabaseReference ref;
    String userID;

    public Post_Adapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        ref= FirebaseDatabase.getInstance().getReference();
        userID= Session.getInstance(context).getFarmerID();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post=postList.get(position);
        holder.topicTextView.setText(post.getTopic());
        holder.posterNameTextView.setText(post.getAskedBy());
        holder.dateTextView.setText(post.getDate());
        if(!post.getQuestionimage().equals("")){
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(post.getQuestionimage()).into(holder.postImageView);
        }else{
            holder.postImageView.setVisibility(View.GONE);
        }
        holder.postExpandableTextView.setText(post.getQuestion());
        setPublisherInformation(holder.profileImageView,post.getPublisher());

        isLiked(holder.likeImageView,post.getPostId());
        isDisliked(holder.dislikeImageView,post.getPostId());

        getLikes(holder.likeTxt,post.getPostId());
        getDislikes(holder.dislikeTxt,post.getPostId());


        //handle onclick
        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.likeImageView.getTag().equals("like") && holder.dislikeImageView.getTag().equals("dislike")){
                    ref.child("likes").child(post.getPostId()).child(userID).setValue(true);
                } else if (holder.likeImageView.getTag().equals("like") && holder.dislikeImageView.getTag().equals("disliked")){
                    ref.child("dislikes").child(post.getPostId()).child(userID).removeValue();
                    ref.child("likes").child(post.getPostId()).child(userID).setValue(true);
                } else {
                    ref.child("likes").child(post.getPostId()).child(userID).removeValue();
                }
            }
        });

        holder.dislikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.dislikeImageView.getTag().equals("dislike") && holder.likeImageView.getTag().equals("like")){
                    ref.child("dislikes").child(post.getPostId()).child(userID).setValue(true);
                } else if (holder.dislikeImageView.getTag().equals("dislike") && holder.likeImageView.getTag().equals("liked")){
                    ref.child("likes").child(post.getPostId()).child(userID).removeValue();
                    ref.child("dislikes").child(post.getPostId()).child(userID).setValue(true);
                } else {
                    ref.child("dislikes").child(post.getPostId()).child(userID).removeValue();
                }
            }
        });
        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open comment Activity
                Intent intent=new Intent(context, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("publisher",post.getPublisher());
                context.startActivity(intent);
            }
        });
    }

    private void getDislikes(TextView dislikeTxt, String postId) {
        DatabaseReference mRef=ref.child("Dislikes").child(postId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfLikes= (int) snapshot.getChildrenCount();
                String dislikeText=null;
                if(numberOfLikes>1 || numberOfLikes==0){
                    dislikeText="Dislikes";
                }else{
                    dislikeText="Dislike";
                }
                String Text=numberOfLikes+" "+dislikeText;
                dislikeTxt.setText(Text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void getLikes(TextView likeTxt, String postId) {
        DatabaseReference mRef=ref.child("Likes").child(postId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfLikes= (int) snapshot.getChildrenCount();
                String likeText=null;
                if(numberOfLikes>1 || numberOfLikes==0){
                    likeText="likes";
                }else{
                    likeText="like";
                }
                String Text=numberOfLikes+" "+likeText;
                likeTxt.setText(Text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void isDisliked(ImageView dislikeImageView, String postId) {
        DatabaseReference mRef=ref.child("DisLikes").child(postId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userID).exists()){
                    //todo change to liked image view
                    dislikeImageView.setImageResource(R.drawable.ic_disliked);
                    dislikeImageView.setTag("DisLiked");
                }
                else{
                    dislikeImageView.setImageResource(R.drawable.ic_dislike);
                    dislikeImageView.setTag("Dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void isLiked(ImageView likeImageView, String postId) {
        DatabaseReference mRef=ref.child("Likes").child(postId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userID).exists()){
                    likeImageView.setImageResource(R.drawable.ic_liked);
                    likeImageView.setTag("Liked");
                }
                else{
                    likeImageView.setImageResource(R.drawable.ic_like);
                    likeImageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void setPublisherInformation(ImageView profileImageView, String publisher) {
        ref.child("Farmers").child(publisher).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Farmer farmer=snapshot.getValue(Farmer.class);
                    if(!farmer.getProfilePicUrl().equals("")){
                        Glide.with(context).load(farmer.getProfilePicUrl()).into(profileImageView);
                    }else{
                        Glide.with(context).load(R.drawable.ic_farmer).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView,profileImageView;
        TextView topicTextView,dateTextView,posterNameTextView;
        ExpandableTextView postExpandableTextView;
        private final TextView likeTxt,dislikeTxt;
        private final ImageView likeImageView,dislikeImageView;
        private final LinearLayout likeLayout,dislikeLayout,commentLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView=itemView.findViewById(R.id.postImageView);
            profileImageView=itemView.findViewById(R.id.senderProfileImageView);
            topicTextView=itemView.findViewById(R.id.topicTextView);
            dateTextView=itemView.findViewById(R.id.dateTextView);
            posterNameTextView=itemView.findViewById(R.id.posterNameTextView);
            postExpandableTextView=itemView.findViewById(R.id.expand_text_view);
            likeTxt=itemView.findViewById(R.id.likeTxt);
            dislikeTxt=itemView.findViewById(R.id.dislikeTxt);
            likeImageView=itemView.findViewById(R.id.likeImageView);
            dislikeImageView=itemView.findViewById(R.id.dislikeImageView);
            likeLayout=itemView.findViewById(R.id.likeLayout);
            dislikeLayout=itemView.findViewById(R.id.dislikeLayout);
            commentLayout=itemView.findViewById(R.id.commentLayout);
        }
    }

}
