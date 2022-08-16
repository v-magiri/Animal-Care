package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Farmer;

public class FarmerRegistration extends AppCompatActivity {
    private static final String TAG = "Error in Registration";
    private EditText FarmerNameEditTxt,FarmerPhoneNumberEditTxt,FarmerPasswordEditTxt;
    private Button registerBtn;
    private TextView signInTxt;
    String Name,FarmerPhoneNumber,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);
        initViews();
    }

    private void initViews() {
        FarmerNameEditTxt=findViewById(R.id.fullNameEditTxt);
        FarmerPhoneNumberEditTxt=findViewById(R.id.phoneNumberEditTxt);
        FarmerPasswordEditTxt=findViewById(R.id.passwordEditTxt);
        registerBtn=findViewById(R.id.registerBtn);
        signInTxt=findViewById(R.id.signInTextView);
        
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
        Name=FarmerNameEditTxt.getText().toString().trim();
        FarmerPhoneNumber=FarmerPhoneNumberEditTxt.getText().toString().trim();
        password=FarmerPasswordEditTxt.getText().toString().trim();


        if(TextUtils.isEmpty(Name)){
            FarmerNameEditTxt.setError("Name is required");
            FarmerNameEditTxt.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(FarmerPhoneNumber)){
            FarmerPhoneNumberEditTxt.setError("PhoneNumber is required");
            FarmerPhoneNumberEditTxt.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            FarmerPasswordEditTxt.setError("Password is required");
            FarmerPasswordEditTxt.requestFocus();
            return;
        }
        if(!Patterns.PHONE.matcher(FarmerPhoneNumber).matches()){
            FarmerPhoneNumberEditTxt.setError("Invalid PhoneNumber");
            FarmerPhoneNumberEditTxt.requestFocus();
            return;
        }
        if(Name.length() < 20){
            FarmerNameEditTxt.setError("Full Name is required");
            FarmerNameEditTxt.requestFocus();
            return;
        }
//                register Farmer
        String FarmerID= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String FarmerLocation="";
        registerFarmer(FarmerID,Name,FarmerPhoneNumber,FarmerLocation,password);

    }

    private void registerFarmer(String farmerID, String name, String farmersPhoneNumber, String farmerLocation, String password) {
        DatabaseReference mRef;
        mRef= FirebaseDatabase.getInstance().getReference("Farmers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Farmer newFarmer=new Farmer(farmerID,name,farmersPhoneNumber,farmerLocation,password);
                mRef.child(farmerID).setValue(newFarmer).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            //redirect user to login Activity
                            Toast toast= Toast.makeText(getApplicationContext(),"Welcome "+name+"account successfully created",Toast.LENGTH_SHORT);
                            View view=toast.getView();
                            view.getBackground().setColorFilter(Color.parseColor("#20a7db"), PorterDuff.Mode.SRC_IN);
                            TextView textView=view.findViewById(android.R.id.message);
                            textView.setTextColor(Color.parseColor("#FFFFFF"));
                            textView.setTextSize(16);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            startActivity(new Intent(getApplicationContext(),FarmerLogin.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String Message=e.getMessage();
                        Log.d(TAG, "onFailure: "+Message);
                        Toast toast= Toast.makeText(getApplicationContext(),"Something wrong happened try again later",Toast.LENGTH_SHORT);
                        View view=toast.getView();
                        view.getBackground().setColorFilter(Color.parseColor("#20a7db"), PorterDuff.Mode.SRC_IN);
                        TextView textView=view.findViewById(android.R.id.message);
                        textView.setTextColor(Color.parseColor("#FFFFFF"));
                        textView.setTextSize(16);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String Message=error.getMessage();
                Log.d(TAG, "onCancelled: "+Message);
                Toast toast= Toast.makeText(getApplicationContext(),"Something wrong happened try again later",Toast.LENGTH_SHORT);
                View view=toast.getView();
                view.getBackground().setColorFilter(Color.parseColor("#20a7db"), PorterDuff.Mode.SRC_IN);
                TextView textView=view.findViewById(android.R.id.message);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setTextSize(16);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }
}