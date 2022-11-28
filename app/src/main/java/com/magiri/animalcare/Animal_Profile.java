package com.magiri.animalcare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Animal;
import com.magiri.animalcare.Session.Prevalent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Animal_Profile extends Fragment {
    private static final String TAG = "Animal_Profile";
    private TextView animalNameTxt,animalAgeTxt,animalBreedTxt,animalStatusTxt;
    private Button editProfileBtn;
    private String AnimalID,FarmerID;
    private DatabaseReference ref;
    String str;
    DateFormat formatter;
    SimpleDateFormat sdf;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_animal__profile, container, false);
        animalAgeTxt=view.findViewById(R.id.animalAgeTextView);
        animalNameTxt=view.findViewById(R.id.animalNameTextView);
        animalBreedTxt=view.findViewById(R.id.animalBreedTextView);
        animalStatusTxt=view.findViewById(R.id.animalStatusTextView);
        editProfileBtn=view.findViewById(R.id.editAnimalProfileBtn);
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        ref= FirebaseDatabase.getInstance().getReference("Animals").child(FarmerID);
        formatter = new SimpleDateFormat("dd/MM/yy");
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar obj = Calendar.getInstance();
        str = formatter.format(obj.getTime());
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }

        getAnimalProfile();
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    private void getAnimalProfile() {
        ref.child(AnimalID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Animal myAnimal=snapshot.getValue(Animal.class);
                animalNameTxt.setText(myAnimal.getAnimalName());
                animalBreedTxt.setText(myAnimal.getBreed());
                animalStatusTxt.setText(myAnimal.getStatus());
                String DOB=myAnimal.getDOB();
                try {
                    Date date1=formatter.parse(DOB);
                    Date date2=formatter.parse(str);
                    Calendar m_calendar=Calendar.getInstance();
                    m_calendar.setTime(date1);
                    int nMonth1=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
                    m_calendar.setTime(date2);
                    int nMonth2=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
                    animalAgeTxt.setText(java.lang.Math.abs(nMonth2-nMonth1)+" months");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}