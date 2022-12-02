package com.magiri.animalcare;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.TimeZone;

public class Animal_Profile extends Fragment {
    private static final String TAG = "Animal_Profile";
    private TextView animalNameTxt,animalAgeTxt,animalBreedTxt,animalStatusTxt;
    private Button editProfileBtn;
    private String AnimalID,FarmerID;
    private DatabaseReference ref;
    String str;
    DateFormat formatter;
    AlertDialog alertDialog;
    SimpleDateFormat sdf;
    ArrayAdapter groupsAdapter,statusAdapter,breedAdapter;
    String selectedStatus,selectedBreed,animalGroup;
    ProgressDialog progressDialog;
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
        formatter = new SimpleDateFormat("dd/MM/yy");
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar obj = Calendar.getInstance();
        str = formatter.format(obj.getTime());
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }
        ref= FirebaseDatabase.getInstance().getReference("Animals").child(FarmerID).child(AnimalID);
        getAnimalProfile();
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating Animal Details");
        progressDialog.setCanceledOnTouchOutside(false);
        groupsAdapter=new ArrayAdapter(getActivity(),R.layout.item,getResources().getStringArray(R.array.age_group));
        breedAdapter=new ArrayAdapter(getActivity(),R.layout.item,getResources().getStringArray(R.array.breed));
        statusAdapter=new ArrayAdapter(getActivity(),R.layout.item,getResources().getStringArray(R.array.animal_status));
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open Alert Dailog where the farmer can edit the animal Details
                AlertDialog.Builder alertDailogBuilder=new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view=layoutInflater.inflate(R.layout.add_animal_layout,null);
                alertDailogBuilder.setView(view);
                alertDailogBuilder.setCancelable(false);
                TextInputEditText animalTextView=view.findViewById(R.id.animalNameTxt);
                TextInputEditText dateText=view.findViewById(R.id.dobText);
                TextInputLayout statusLayout=view.findViewById(R.id.statusLayout);
                TextView titleTxt=view.findViewById(R.id.titleTxt);
                TextInputLayout groupLayout=view.findViewById(R.id.animalGroupLayout);
                AutoCompleteTextView groupDropDown=view.findViewById(R.id.animalGroup);
                AutoCompleteTextView breedDropDown=view.findViewById(R.id.breedSpinner);
                AutoCompleteTextView statusDropDown=view.findViewById(R.id.statusDropDown);
                ImageView closeAlertDialogImageView=view.findViewById(R.id.closeBtn);
                Button addBtn=view.findViewById(R.id.addAnimalBtn);
                ImageButton animalImageBtn=view.findViewById(R.id.animalImageUpload);
                animalImageBtn.setVisibility(View.GONE);
                String currentAnimalName=animalNameTxt.getText().toString();
                animalTextView.setEnabled(false);
                animalTextView.setText(currentAnimalName);
                addBtn.setText("Update");
                titleTxt.setText("Edit Animal Details");
                groupLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.VISIBLE);


                closeAlertDialogImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                dateText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
                        builder.setTitleText("Animal Date of Birth");
                        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
                        builder.setCalendarConstraints(calendarConstraintBuilder.build());

                        final MaterialDatePicker<Long> materialDatePicker = builder.build();
                        materialDatePicker.show(getActivity().getSupportFragmentManager(),materialDatePicker.toString());
                        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                calendar.setTimeInMillis(selection);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                dateText.setText(sdf.format(calendar.getTime()));
                            }
                        });
                    }
                });
                groupDropDown.setAdapter(groupsAdapter);
                breedDropDown.setAdapter(breedAdapter);
                statusDropDown.setAdapter(statusAdapter);

                groupDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        animalGroup=parent.getItemAtPosition(position).toString();
                        if(animalGroup.equals("Above 18 months")){
                            statusLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
                breedDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedBreed=parent.getItemAtPosition(position).toString();
                    }
                });
                statusDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedStatus=parent.getItemAtPosition(position).toString();
                    }
                });
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String DOB=dateText.getText().toString();
                        if(TextUtils.isEmpty(selectedBreed)){
                            breedDropDown.setError("Required");
                            return;
                        }
                        if(TextUtils.isEmpty(DOB)){
                            dateText.setError("Date Required");
                            return;
                        }
                        if(TextUtils.isEmpty(selectedStatus) && !TextUtils.isEmpty(animalGroup)){
                            selectedStatus="Heifer";
                        }
                        progressDialog.show();
                        UpdateAnimalDetails(currentAnimalName,DOB,selectedBreed,selectedStatus);
                    }
                });
                alertDialog=alertDailogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.show();

            }
        });
        return view;
    }

    private void UpdateAnimalDetails(String currentAnimalName, String dob, String selectedBreed, String selectedStatus) {
        ref.child("dob").setValue(dob).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    ref.child("breed").setValue(selectedBreed).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                ref.child("status").setValue(selectedStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete()){
                                            Toast.makeText(getActivity(),"Updated SuccessFully",Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void getAnimalProfile() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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