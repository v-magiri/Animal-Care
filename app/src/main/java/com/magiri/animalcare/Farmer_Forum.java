package com.magiri.animalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.magiri.animalcare.Adapters.Forum_MessageAdapter;
import com.magiri.animalcare.Model.Post;

import java.util.List;

public class Farmer_Forum extends AppCompatActivity {
    private MaterialToolbar materialToolbar;
    private RecyclerView postRecyclerView;
    Forum_MessageAdapter forum_messageAdapter;
    List<Post> postList;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_forum);
        materialToolbar=findViewById(R.id.animalCareMaterialToolbar);
        postRecyclerView=findViewById(R.id.postRecyclerView);
        forum_messageAdapter=new Forum_MessageAdapter(Farmer_Forum.this,postList);
        fab=findViewById(R.id.postFloatingActionBar);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FarmerHome.class));
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo implement the posting of forum messages
                startActivity(new Intent(getApplicationContext(),PostActivity.class));
                finish();
            }
        });
    }
}