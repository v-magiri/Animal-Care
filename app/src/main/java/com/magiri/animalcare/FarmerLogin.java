package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Farmer;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.Session.Session;

public class FarmerLogin extends AppCompatActivity {
    private EditText phoneNumberEditText,passwordEditTxt;
    private Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_login);
        initView();
    }

    private void initView() {
        phoneNumberEditText=findViewById(R.id.farmerPhoneEditTxt);
        passwordEditTxt=findViewById(R.id.farmerPassWordEditTxt);
        loginBtn=findViewById(R.id.farmerLoginBtn);
        String phoneNumber=phoneNumberEditText.getText().toString().trim();
        String password=passwordEditTxt.getText().toString().trim();

        if(TextUtils.isEmpty(phoneNumber)){
            phoneNumberEditText.setError("Phone Number is Required");
            phoneNumberEditText.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordEditTxt.setError("Password is required");
            passwordEditTxt.requestFocus();
            return;
        }
        if(phoneNumber.contains(" ")){
            phoneNumberEditText.setError("Phone Number can not contain Spaces");
            phoneNumberEditText.requestFocus();
            return;
        }
        loginFarmer(phoneNumber,password);
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
                            Toast toast= Toast.makeText(getApplicationContext(),"Welcome "+farmerData.getFarmerName(),Toast.LENGTH_SHORT);
                            View view=toast.getView();
                            view.getBackground().setColorFilter(Color.parseColor("#20a7db"), PorterDuff.Mode.SRC_IN);
                            TextView textView=view.findViewById(android.R.id.message);
                            textView.setTextColor(Color.parseColor("#FFFFFF"));
                            textView.setTextSize(16);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();

                            //redirect user to login activity
                            Intent intent=new Intent(getApplicationContext(),FarmerHome.class);
                            Session.getInstance(FarmerLogin.this).FarmerLogin(FarmerID,farmerData.getFarmerName());
                            Prevalent.currentOnlineFarmer=farmerData;
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}