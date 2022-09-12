package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Veterinarian;

import java.util.ArrayList;

public class ViewVet extends AppCompatActivity {
    private static final String TAG = "View Vet";
    private ListView skillListView;
    private RelativeLayout forwardRelativeLayout;
    private ImageView profilePic;
    private TextView nameTextView,locationTextView,visitationFeeTextView;
    private Button consultBtn,requestBtn;
    String RegNum;
    String[] skillList;
    ArrayList<String> SkillList;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vet);

        skillListView=findViewById(R.id.qualificationListView);
        forwardRelativeLayout=findViewById(R.id.forwardLayout);
        profilePic=findViewById(R.id.vetProfilePic);
        nameTextView=findViewById(R.id.vetNameTxt);
        locationTextView=findViewById(R.id.locationTxt);
        visitationFeeTextView=findViewById(R.id.visitationFeeTextView);
        consultBtn=findViewById(R.id.consultBtn);
        requestBtn=findViewById(R.id.visitBtn);

        RegNum=getIntent().getStringExtra("Vet_RegNum");
        fetchData(RegNum);

        consultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start chat
                Intent intent=new Intent(getApplicationContext(), FarmerVet_Chat.class);
                intent.putExtra("Vet_REGNUM",RegNum);
                startActivity(intent);
            }
        });
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make a visit Request

            }
        });

        forwardRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // let the user view the vet on  maps

            }
        });
    }

    private void fetchData(String regNum) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Veterinarian");
        ref.child(regNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veterinarian vet=snapshot.getValue(Veterinarian.class);
                visitationFeeTextView.setText(vet.getVisitationFee());
                String location=vet.getWard()+","+vet.getConstituency();
                locationTextView.setText(location);
                nameTextView.setText(vet.getName());
                if(!vet.getProfilePicUrl().equals("")){
                    Glide.with(ViewVet.this).load(vet.getProfilePicUrl()).into(profilePic);
                }else{
                    Glide.with(ViewVet.this).load(R.drawable.ic_vet).into(profilePic);
                }
                skillList= vet.getSkill().split(",");
                arrayAdapter=new ArrayAdapter(ViewVet.this, android.R.layout.simple_list_item_1,skillList);
                skillListView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something wrong happened.Please contect support team",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}