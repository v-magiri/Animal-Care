package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.CommentAdapter;
import com.magiri.animalcare.Model.Comment;
import com.magiri.animalcare.Model.Post;
import com.magiri.animalcare.Session.Session;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private RecyclerView commentsRecyclerView;
    private MaterialToolbar commentMaterialToolBar;
    private ImageView attachImageView;
    private EditText messageEditTxt;
    private FloatingActionButton sendFab;
    List<Comment> commentList;
    CommentAdapter commentsAdapter;
    DatabaseReference mRef;
    private String postID,publisherID;
    ProgressDialog progressDialog;
    TextView posterNameTxt,topicTxt,dateTxt;
    ExpandableTextView postExpandableTxt;
    ImageView postImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //binding the View
        commentMaterialToolBar=findViewById(R.id.commentMaterialToolBar);
        commentsRecyclerView=findViewById(R.id.commentsRecyclerView);
        attachImageView=findViewById(R.id.iv_image);
        messageEditTxt=findViewById(R.id.et_message);
        sendFab=findViewById(R.id.btn_send);
        posterNameTxt=findViewById(R.id.posterNameTextView);
        topicTxt=findViewById(R.id.topicTextView);
        dateTxt=findViewById(R.id.dateTextView);
        postExpandableTxt=findViewById(R.id.expand_text_view);
        postImageView=findViewById(R.id.postImageView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        commentsRecyclerView.setLayoutManager(linearLayoutManager);
        commentsRecyclerView.setHasFixedSize(true);

        commentList=new ArrayList<>();
        commentsAdapter=new CommentAdapter(CommentActivity.this,commentList);
        commentsRecyclerView.setAdapter(commentsAdapter);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Adding Comment");
        progressDialog.setCanceledOnTouchOutside(false);

        //fetch the selected Post
        Intent intent=getIntent();
        postID=intent.getStringExtra("postId");
        publisherID=intent.getStringExtra("publisher");
        mRef= FirebaseDatabase.getInstance().getReference();

        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateComment();
            }
        });

        commentMaterialToolBar.setNavigationOnClickListener(v -> finish());

        fetchPost();
        fetchComments();

    }

    private void fetchPost() {
        mRef.child("Post").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   Post selectPost=snapshot.getValue(Post.class);
                   posterNameTxt.setText(selectPost.getAskedBy());
                   postExpandableTxt.setText(selectPost.getQuestion());
                   topicTxt.setText(selectPost.getTopic());
                   dateTxt.setText(selectPost.getDate());
                   if(!selectPost.getQuestionimage().equals("")){
                       postImageView.setVisibility(View.VISIBLE);
                       Glide.with(CommentActivity.this).load(selectPost.getQuestionimage()).into(postImageView);
                   }else{
                       postImageView.setVisibility(View.GONE);
                   }
               }else{
                   Toast.makeText(getApplicationContext(),"Something wrong Happened.Please try Again Later",Toast.LENGTH_SHORT).show();
                   finish();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong Happened.Please try Again Later",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void validateComment() {
        String commentText=messageEditTxt.getText().toString().trim();

        if(commentText.isEmpty()){
            return;
        }else{
            progressDialog.show();
            messageEditTxt.setText("");
            addComment(commentText);
        }
    }

    private void addComment(String commentText) {
        DatabaseReference databaseReference=mRef.child("Comments").child(postID);
        String commentID=databaseReference.push().getKey();
        Calendar cal=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");
        String date=sdf.format(cal.getTime());
        String userID= Session.getInstance(CommentActivity.this).getFarmerID();
        Comment newComment=new Comment(commentText,date,postID,userID);
        databaseReference.child(commentID).setValue(newComment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Comment Added",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Something wrong Happened.Please try Again Later",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Something wrong Happened.Please contact Support Team",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchComments() {
        mRef.child("Comments").child(postID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Comment comment=dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
                Toast.makeText(getApplicationContext(),"Something Wrong Happened.Please Try Again Later",Toast.LENGTH_SHORT).show();
            }
        });
    }
}