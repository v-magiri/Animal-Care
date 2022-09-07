package com.magiri.animalcare;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.magiri.animalcare.Model.Post;
import com.magiri.animalcare.Session.Prevalent;

import java.text.DateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";
    private EditText messageEditTxt;
    private Spinner spinnerTxt;
    private ImageView postImageView,imageAttachImg,videoAttachImg,audioAttachImg,cancelImageView;
    private Button postBtn;
    private MaterialToolbar materialToolbar;
    SharedPreferences sharedPreferences;
    private static final String ANIMALCARE_PREF="com.magiri.animalcare";
    private static final String User_ID="User_ID";
    private static final String Name="VetName";
    StorageReference storageReference;
    private static final int image_Pick_Code=2;
    private Uri imageViewUri;
    String postImageUri,userID,userName;
    private Boolean imageOption=false;
    DatabaseReference databaseReference;

    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //binding the views
        materialToolbar=findViewById(R.id.postToolBar);
        messageEditTxt=findViewById(R.id.messageTxt);
        spinnerTxt=findViewById(R.id.topicsSpinner);
        postImageView=findViewById(R.id.postImage);
        imageAttachImg=findViewById(R.id.imageAttachImg);
        videoAttachImg=findViewById(R.id.videoAttachImg);
        audioAttachImg=findViewById(R.id.audioAttachImg);
        cancelImageView=findViewById(R.id.cancelPostAction);
        postBtn=findViewById(R.id.postActionBtn);
        sharedPreferences=getSharedPreferences(ANIMALCARE_PREF, Context.MODE_PRIVATE);
        userID=sharedPreferences.getString(User_ID,"null");
        userName=sharedPreferences.getString(Name,"null");
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference("Post");

        materialToolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),Farmer_Forum.class));
            finish();
        });
        cancelImageView.setOnClickListener(v -> finish());
        imageAttachImg.setOnClickListener(v -> {
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,image_Pick_Code);
        });
        videoAttachImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to implement the video attachment

            }
        });
        audioAttachImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to implement the audio attachment
            }
        });
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.topic));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerTxt.setAdapter(adapter);

        spinnerTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerTxt.getSelectedItem().equals("select topic"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PostMessage=getPostMessage();
                String topic=getSelectedTopic();
                validatePost();
                String senderID= userID;
                String senderName=userName;
                String date=getCurrentTimeStamp();
                validateFields();
                if(imageOption){
                    uploadImage();
                }
                Post post=new Post(senderName,getCurrentTimeStamp(),getPostId(),userID,PostMessage,topic);
                postMessage(post);
            }
        });
    }

    private void uploadImage() {
        String fileName=getFileName();
        if(imageViewUri!=null){
            storageReference.child("PostImages")
                    .child(fileName).putFile(imageViewUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                storageReference.child("PostImages").child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Post createPost=new Post(userName,getCurrentTimeStamp(),getPostId(),userID,getPostMessage(),uri.toString(),getSelectedTopic());
                                        postMessage(createPost);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: "+e.getMessage());
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                        }
                    });
        }
    }

    private void postMessage(Post createPost) {
        String postID=createPost.getPostId();
        databaseReference.child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(postID).setValue(createPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            displayMessage("Message Posted");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        displayMessage("Something wrong happened.Please try again later");
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                displayMessage("An error occurred. Please contact System Admin");
                Log.d(TAG, "onCancelled: "+ error.getMessage());
            }
        });
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void validateFields() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== image_Pick_Code && requestCode== RESULT_OK && data !=null){
            imageViewUri=data.getData();
            postImageView.setImageURI(imageViewUri);
        }
    }

    private void validatePost() {
        if(getPostMessage().isEmpty()){
            messageEditTxt.setError("Question Can not be Empty");
        }else if(getSelectedTopic().equals("Select Topic ▼")){
            Toast.makeText(getApplicationContext(),"Please Select Topic",Toast.LENGTH_SHORT).show();
        } else if (!getPostMessage().isEmpty() && !getSelectedTopic().equals("") && imageViewUri == null){
            uploadQuestionWithoutImage();
        } else if (!getPostMessage().isEmpty() && !getSelectedTopic().equals("") && imageViewUri != null){
            uploadQuestionWithImage();
        }
    }

    private void uploadQuestionWithImage() {

    }

    private void uploadQuestionWithoutImage() {

    }

    private String getPostMessage() {
        return messageEditTxt.getText().toString().trim();
    }

    private String getSelectedTopic() {
        return spinnerTxt.getSelectedItem().toString();
    }
    private String getCurrentTimeStamp(){
        return DateFormat.getInstance().format(new Date());
    }

    private String getFileName(){
        return System.currentTimeMillis()+".jpg";
    }
    private String getPostId(){
        return String.valueOf(System.currentTimeMillis());
    }

}