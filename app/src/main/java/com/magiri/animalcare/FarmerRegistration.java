package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Farmer;

import java.util.regex.Pattern;

public class FarmerRegistration extends AppCompatActivity {
    private static final String TAG = "FarmerRegistration";
    private Button registerBtn;
    private TextView signInTxt;
    private TextInputLayout phoneNumberLayout,NameLayout,passwordLayout;
    String Name,FarmerPhoneNumber,password;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);
        initViews();
    }

    private void initViews() {
        NameLayout=findViewById(R.id.fullNameEditTxt);
        phoneNumberLayout=findViewById(R.id.phoneNumberEditTxt);
        passwordLayout=findViewById(R.id.passwordEditTxt);
        registerBtn=findViewById(R.id.registerBtn);
        signInTxt=findViewById(R.id.signInTextView);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("AnimalCare");
        progressDialog.setMessage("Creating Account");
        progressDialog.setCanceledOnTouchOutside(false);
        
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                validate Fields
                validateFields();
            }
        });
        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FarmerLogin.class));
                finish();
            }
        });
        
    }

    private void validateFields() {
        Name=NameLayout.getEditText().getText().toString().trim();
        FarmerPhoneNumber=phoneNumberLayout.getEditText().getText().toString().trim();
        password=passwordLayout.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(Name)){
            NameLayout.getEditText().setError("Name is required");
            NameLayout.getEditText().requestFocus();
            return;
        }
        if(TextUtils.isEmpty(FarmerPhoneNumber)){
            phoneNumberLayout.getEditText().setError("PhoneNumber is required");
            phoneNumberLayout.getEditText().requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordLayout.getEditText().setError("Password is required");
            passwordLayout.getEditText().requestFocus();
            return;
        }
        //Todo Find a better phone number validation method
        if(!Patterns.PHONE.matcher(FarmerPhoneNumber).matches()){
            phoneNumberLayout.getEditText().setError("Invalid PhoneNumber");
            phoneNumberLayout.getEditText().requestFocus();
            return;
        }
//                register Farmer
        String FarmerID= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String FarmerLocation="";
        progressDialog.show();
        registerFarmer(FarmerID,Name,FarmerPhoneNumber,FarmerLocation,password);

    }

    private void registerFarmer(String farmerID, String name, String farmersPhoneNumber, String farmerLocation, String password) {
        DatabaseReference mRef;
        mRef= FirebaseDatabase.getInstance().getReference("Farmers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Farmer newFarmer=new Farmer(farmerID,name,farmersPhoneNumber,farmerLocation,password,"");
                mRef.child(farmerID).setValue(newFarmer).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            //redirect user to login Activity
                            Toast.makeText(getApplicationContext(),"Welcome "+name+"account successfully created",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),FarmerLogin.class));
                            finish();
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String Message=e.getMessage();
                        Log.d(TAG, "onFailure: "+Message);
                        Toast.makeText(getApplicationContext(),"Something wrong happened try again later",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String Message=error.getMessage();
                Log.d(TAG, "onCancelled: "+Message);
                Toast.makeText(getApplicationContext(),"Something wrong happened.Please contact Support Team",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}