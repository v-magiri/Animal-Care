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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Farmer;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.Session.Session;

public class FarmerLogin extends AppCompatActivity {
    private static final String TAG = "FarmerLogin";
    private Button loginBtn;
    private TextView signUpTxt;
    private TextInputLayout phoneNumberLayout,passwordLayout;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_login);
        initView();
    }

    private void initView() {
        phoneNumberLayout=findViewById(R.id.farmerPhoneEditTxt);
        passwordLayout=findViewById(R.id.farmerPassWordEditTxt);
        loginBtn=findViewById(R.id.farmerLoginBtn);
        signUpTxt=findViewById(R.id.signUpTextView);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Signing to AnimalCare");
        progressDialog.setCanceledOnTouchOutside(false);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber=phoneNumberLayout.getEditText().getText().toString().trim();
                String password=passwordLayout.getEditText().getText().toString().trim();

                if(TextUtils.isEmpty(phoneNumber)){
                    phoneNumberLayout.setError("Phone Number is Required");
                    phoneNumberLayout.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordLayout.setError("Password is required");
                    passwordLayout.requestFocus();
                    return;
                }
                if(!Patterns.PHONE.matcher(password).matches()){
                    phoneNumberLayout.setError("Invalid Phone Number");
                    phoneNumberLayout.requestFocus();
                    return;
                }
                if(phoneNumber.contains(" ")){
                    phoneNumberLayout.setError("Phone Number can not contain Spaces");
                    phoneNumberLayout.requestFocus();
                    return;
                }
                progressDialog.show();
                loginFarmer(phoneNumber,password);
            }
        });
        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FarmerRegistration.class));
                finish();
            }
        });
    }

    private void loginFarmer(String phoneNumber, String password) {
        DatabaseReference mRef;
        mRef= FirebaseDatabase.getInstance().getReference("Farmers");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String FarmerID= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                if(snapshot.child(FarmerID).exists()){
                    Farmer farmerData=snapshot.child(FarmerID).getValue(Farmer.class);
                    if(farmerData.getPhoneNumber().equals(phoneNumber)){
                        if(farmerData.getFarmerPassword().equals(password)){
                            Toast.makeText(getApplicationContext(),"Welcome "+farmerData.getFarmerName(),Toast.LENGTH_SHORT).show();

                            //redirect user to login activity
                            Intent intent=new Intent(getApplicationContext(),FarmerHome.class);
                            Session.getInstance(FarmerLogin.this).FarmerLogin(FarmerID,farmerData.getFarmerName());
                            Prevalent.currentOnlineFarmer=farmerData;
                            startActivity(intent);
                            progressDialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Something wrong Happened.Please contact Support Team",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}