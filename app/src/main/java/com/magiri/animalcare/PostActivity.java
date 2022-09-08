package com.magiri.animalcare;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.magiri.animalcare.Model.Post;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";
    private EditText messageEditTxt;
    private Spinner spinnerTxt;
    private ImageView postImageView,imageAttachImg,videoAttachImg,audioAttachImg,cancelImageView;
    private Button postBtn;
    private MaterialToolbar materialToolbar;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    private static final String ANIMALCARE_PREF="com.magiri.animalcare";
    private static final String User_ID="User_ID";
    private static final String Name="VetName";
    private static final int IMAGE_REQUEST_CODE=123;
    StorageTask storageTask;
    StorageReference storageReference;
    private static final int image_Pick_Code=2;
    private Uri imageViewUri=null;
    String userID,FarmerName;
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
        FarmerName=sharedPreferences.getString(Name,"null");
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference("Post");
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Adding your Post to Forum");
        progressDialog.setCanceledOnTouchOutside(false);

        materialToolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),Farmer_Forum.class));
            finish();
        });
        cancelImageView.setOnClickListener(v -> finish());
        imageAttachImg.setOnClickListener(v -> {
            checkReadPermission();
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
                String date=getCurrentTimeStamp();
            }
        });
    }

    private void checkReadPermission() {
        if(ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            SelectPostImage();
        }else{
            ActivityCompat.requestPermissions(PostActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},IMAGE_REQUEST_CODE);
        }
    }

    private void SelectPostImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Photo to Post"),image_Pick_Code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==IMAGE_REQUEST_CODE && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            SelectPostImage();
        }else{
            showAlertWarning();
        }

    }

    private void showAlertWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("AnimalCare App Require Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            openSettings();
            dialog.cancel();
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == image_Pick_Code && resultCode== RESULT_OK && data !=null){
            imageViewUri=data.getData();
            try{
                if(imageViewUri!=null){
                    Bitmap postImageBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageViewUri);
                    postImageView.setImageBitmap(postImageBitmap);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void validatePost() {
        if(getPostMessage().isEmpty()){
            messageEditTxt.setError("Question Can not be Empty");
        }else if(getSelectedTopic().equals("Select Topic ▼")){
            Toast.makeText(getApplicationContext(),"Please Select Topic",Toast.LENGTH_SHORT).show();
        } else if (!getPostMessage().isEmpty() && !getSelectedTopic().equals("") && imageViewUri == null){
            uploadQuestionWithoutImage();
            progressDialog.show();
        } else if (!getPostMessage().isEmpty() && !getSelectedTopic().equals("") && imageViewUri != null){
            uploadQuestionWithImage();
            progressDialog.show();
        }
    }

    private void uploadQuestionWithImage() {
        final StorageReference storeRef;
        storeRef=storageReference.child("Post_Images").child(System.currentTimeMillis() +"."+ getFileExtension(imageViewUri));
        storageTask=storeRef.putFile(imageViewUri);
        storageTask.continueWithTask(task -> {
            if(!task.isComplete()){
                throw task.getException();
            }
            return storeRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri downloadUri= (Uri) task.getResult();
                String postImageUri=downloadUri.toString();
                String postId=databaseReference.push().getKey();
                Post createdPost=new Post(FarmerName,getCurrentTimeStamp(), postId,userID,getPostMessage(),postImageUri,getSelectedTopic());;
                databaseReference.child(postId).setValue(createdPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            displayMessage("Message Posted");
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),Farmer_Forum.class));
                            finish();
                        }else{
                            progressDialog.dismiss();
                            displayMessage("Could Not Post Message");
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            displayMessage("Failed to Upload Question");
            Log.d(TAG, "onFailure: "+e.getMessage());
        });
    }

    private String getFileExtension(Uri imageViewUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageViewUri));
    }

    private void uploadQuestionWithoutImage() {
        String postID=databaseReference.push().getKey();

        Post createdPost=new Post(FarmerName,getCurrentTimeStamp(),postID,userID,getPostMessage(),"",getSelectedTopic());

        databaseReference.child(postID).
                setValue(createdPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    displayMessage("Message Posted");
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),Farmer_Forum.class));
                }else{
                    progressDialog.dismiss();
                    displayMessage("Could Not Post Message");
                }
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        displayMessage("Failed to Upload Question");
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
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

}