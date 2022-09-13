package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Farmer;
import com.magiri.animalcare.Session.Prevalent;

import de.hdodenhof.circleimageview.CircleImageView;

public class Farmer_Account extends AppCompatActivity {
    private static final String TAG = "Farmer Account";
    TextInputLayout nameInputLayout,phoneInputLayout,passwordInputLayout;
    Button editProfileBtn,SaveBtn;
    CircleImageView profileImageView;
    TextView nameTextView;
    DatabaseReference databaseReference;
    String name,phoneNumber,password;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_account);
        
        nameInputLayout=findViewById(R.id.clientNameInputLayout);
        phoneInputLayout=findViewById(R.id.phoneInputLayout);
        passwordInputLayout=findViewById(R.id.passwordInputLayout);
        editProfileBtn=findViewById(R.id.editProfileBtn);
        SaveBtn=findViewById(R.id.updateProfileBtn);
        profileImageView=findViewById(R.id.farmerProfilePic);
        nameTextView=findViewById(R.id.clientNameTxt);
        progressDialog=new ProgressDialog(this);
        databaseReference= FirebaseDatabase.getInstance().getReference("Farmers").child(Prevalent.currentOnlineFarmer.getFarmerID());

        name=nameInputLayout.getEditText().getText().toString().trim();
        phoneNumber=phoneInputLayout.getEditText().getText().toString().trim();
        password=passwordInputLayout.getEditText().getText().toString().trim();
        progressDialog.setTitle("Updating Account");
        progressDialog.setMessage("Please wait, while we update Profile");
        progressDialog.setCanceledOnTouchOutside(false);



        getProfile();
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameInputLayout.getEditText().setEnabled(true);
                nameInputLayout.getEditText().setSelection(name.length());

                phoneInputLayout.getEditText().setEnabled(true);
                phoneInputLayout.getEditText().setSelection(phoneNumber.length());

                passwordInputLayout.getEditText().setEnabled(true);
                passwordInputLayout.getEditText().setSelection(password.length());

                SaveBtn.setVisibility(View.VISIBLE);
                editProfileBtn.setVisibility(View.GONE);

            }
        });

        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
                progressDialog.show();
                UpdateProfile();
                finish();
            }
        });
    }

    private void UpdateProfile() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Farmer farmer=new Farmer(Prevalent.currentOnlineFarmer.getFarmerID(),name,phoneNumber,"location",password,"");
                databaseReference.setValue(farmer).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Congratulations "+name+", your account has been Updated.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong happened.Please try Again Later",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void validateFields() {
        if(TextUtils.isEmpty(name)){
            nameInputLayout.getEditText().setError("Name Is Required..");
            return;
        }if(TextUtils.isEmpty(phoneNumber)){
            phoneInputLayout.getEditText().setError("Phone Number Required");
            return;
        }if(TextUtils.isEmpty(password)){
            passwordInputLayout.getEditText().setError("Password Required");
            return;
        }
        if(password.length()<6){
            passwordInputLayout.getEditText().setError("Password is too short");
            return;
        }
    }

    private void getProfile() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Farmer farmer=snapshot.getValue(Farmer.class);
                nameTextView.setText(farmer.getFarmerName());
                passwordInputLayout.getEditText().setText(farmer.getFarmerPassword());
                phoneInputLayout.getEditText().setText(farmer.getPhoneNumber());
                nameInputLayout.getEditText().setText(farmer.getFarmerName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong happened.Please try Again Later",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}