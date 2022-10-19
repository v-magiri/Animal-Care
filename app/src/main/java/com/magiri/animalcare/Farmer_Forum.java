package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.Post_Adapter;
import com.magiri.animalcare.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class Farmer_Forum extends AppCompatActivity {
    private static final String TAG = "Farmer_Forum";
    private MaterialToolbar materialToolbar;
    private RecyclerView postRecyclerView;
    Post_Adapter forum_messageAdapter;
    List<Post> postList;
    private FloatingActionButton fab;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_forum);
        materialToolbar=findViewById(R.id.animalCareMaterialToolbar);
        postRecyclerView=findViewById(R.id.postRecyclerView);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab=findViewById(R.id.postFloatingActionBar);
        postList=new ArrayList<>();
        mRef= FirebaseDatabase.getInstance().getReference("Post");
        forum_messageAdapter=new Post_Adapter(Farmer_Forum.this,postList);
        postRecyclerView.setAdapter(forum_messageAdapter);

        fetchPost();
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Home.class));
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PostActivity.class));
                finish();
            }
        });
    }

    private void fetchPost() {
        // fetch all chats
        postList.clear();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post=dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                forum_messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                displayMessage("An Error Occurred Please Contact the Support Team");
                Log.e(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void displayMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}