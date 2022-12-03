package com.magiri.animalcare;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.MilkRecordAdapter;
import com.magiri.animalcare.Model.MilkRecord;
import com.magiri.animalcare.Session.Prevalent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Production extends Fragment {
    private static final String TAG = "MilkProduction";
    RecyclerView productionRecyclerView;
    private DatabaseReference mRef,ref,databaseReference;
    private String AnimalID,FarmerID;
    List<MilkRecord> milkRecordList;
    private MilkRecordAdapter milkRecordAdapter;
    private RadioGroup milkFilterRadioGroup;
    private RadioButton todayRadioBtn,monthRadioBtn,allRadioBtn;
    private TextView totalProductionTxt;
    SimpleDateFormat sdf;
    String str;
    private FloatingActionButton addMilkRecordFAB;
    private AlertDialog milkAlertDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_production, container, false);
        todayRadioBtn=view.findViewById(R.id.currentDayRadioBtn);
        monthRadioBtn=view.findViewById(R.id.currentMonthRadioBtn);
        allRadioBtn=view.findViewById(R.id.totalRadioBtn);
        milkFilterRadioGroup=view.findViewById(R.id.milkRecordFilter);
        totalProductionTxt=view.findViewById(R.id.totalMilkTxt);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        addMilkRecordFAB=view.findViewById(R.id.addMilkRecordFAB);
        Calendar obj = Calendar.getInstance();
        str = sdf.format(obj.getTime());
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        mRef= FirebaseDatabase.getInstance().getReference("MilkProduction").child(FarmerID);
        milkRecordList=new ArrayList<>();
        milkRecordAdapter=new MilkRecordAdapter(getActivity(),milkRecordList);
        if(getArguments()!=null){
            AnimalID=getArguments().getString("AnimalID");
        }
        milkFilterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.currentDayRadioBtn:
                        getMilkProductionProduction("Today");
                        break;
                    case R.id.currentMonthRadioBtn:
                        getMilkProductionProduction("This Month");
                        break;
                    case R.id.totalRadioBtn:
                        getAnimalMilkProduction();
                        break;
                }
            }
        });
        getAnimalMilkProduction();
        addMilkRecordFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDailogBuilder=new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view=layoutInflater.inflate(R.layout.add_milk_production,null);
                alertDailogBuilder.setView(view);
                alertDailogBuilder.setCancelable(false);
                TextView animalNameTxt=view.findViewById(R.id.animalNameTxt);
                TextInputEditText dateEditText=view.findViewById(R.id.dateText);
                TextInputEditText quantityEditText=view.findViewById(R.id.milkQuantityTxt);
                Button AddMilkRecord=view.findViewById(R.id.addMilkRecord);
                RadioButton morningRadio=view.findViewById(R.id.morningBtn);
                RadioButton noonRadio=view.findViewById(R.id.noonBtn);
                RadioButton eveningRadio=view.findViewById(R.id.eveningBtn);
                RadioButton allDayRadio=view.findViewById(R.id.allDayBtn);
                ImageView closeImageView=view.findViewById(R.id.closeBtn);

                closeImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        milkAlertDialog.dismiss();
                    }
                });
                animalNameTxt.setText(AnimalID);

                //set Current Date to the DateEditText
                dateEditText.setText(str);

                dateEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
                        builder.setTitleText("Milk Date");
                        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
                        builder.setCalendarConstraints(calendarConstraintBuilder.build());

                        final MaterialDatePicker<Long> materialDatePicker = builder.build();
                        materialDatePicker.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(),materialDatePicker.toString());
                        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                calendar.setTimeInMillis(selection);
                                dateEditText.setText(sdf.format(calendar.getTime()));
                            }
                        });
                    }
                });

                AddMilkRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                String animalName=animalNameTxt.getText().toString();
                        String date=dateEditText.getText().toString();
                        int quantity= 0;
                        quantity=Integer.parseInt(quantityEditText.getText().toString());
                        String milkTime="";
                        if(morningRadio.isChecked()){
                            milkTime=morningRadio.getText().toString();
                        }else if(noonRadio.isChecked()){
                            milkTime=noonRadio.getText().toString();
                        }else if(eveningRadio.isChecked()){
                            milkTime=eveningRadio.getText().toString();
                        }else if(allDayRadio.isChecked()){
                            milkTime=allDayRadio.getText().toString();
                        }
//                if(TextUtils.isEmpty(animalName)){
//                    animalNameTxt.setError("Required");
//                    return;

//                }
                        if(TextUtils.isEmpty(date)){
                            dateEditText.setError("Required");
                            return;
                        }
                        if(quantity==0){
                            quantityEditText.setError("Required");
                            return;
                        }
                        if(milkTime.equals("")){
                            Toast.makeText(getActivity(),"Please Choose Milking Time",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        recordMilkProduction(AnimalID,date,milkTime,quantity);
                    }
                });

                milkAlertDialog=alertDailogBuilder.create();
                milkAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                milkAlertDialog.show();

            }
        });
        return view;
    }

    private void recordMilkProduction(String animalID, String date, String milkTime, int quantity) {
        String recordID=mRef.push().getKey();
        MilkRecord milkRecord=new MilkRecord(animalID,milkTime,date,quantity,recordID);
        mRef.child(recordID).setValue(milkRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    Toast.makeText(getActivity(),"Production Successfully Recorded",Toast.LENGTH_SHORT).show();
                    milkAlertDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Something wrong Happened Please Try Again Later",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+e.getMessage());
                milkAlertDialog.dismiss();
            }
        });
    }

    private void getMilkProductionProduction(String constraint) {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalMilkProduction=0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MilkRecord milkRecord=dataSnapshot.getValue(MilkRecord.class);
                    if(milkRecord.getAnimalName().equals(AnimalID)){
                        milkRecordList.add(milkRecord);
                        String milkingDate=milkRecord.getDate();
                        if(constraint.equals("Today")){
                            try {
                                Date date1=sdf.parse(milkingDate);
                                Date date2=sdf.parse(str);
                                int result=date2.compareTo(date1);
                                if(result==0){
                                    int currentDayProduction=milkRecord.getQuantity();
                                    totalMilkProduction+=currentDayProduction;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else if(constraint.equals("This Month")){
                            Calendar cal1=Calendar.getInstance();
                            Calendar cal2=Calendar.getInstance();
                            try {
                                Date date1=sdf.parse(milkingDate);
                                Date date2=sdf.parse(str);
                                cal1.setTime(date1);
                                cal2.setTime(date2);
                                if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                                    if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                                        // the date falls in current month
                                        int currentDayProduction=milkRecord.getQuantity();
                                        totalMilkProduction+=currentDayProduction;
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                totalProductionTxt.setText(totalMilkProduction+" L");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void getAnimalMilkProduction() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalMilkProduction=0;
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MilkRecord milkRecord=dataSnapshot.getValue(MilkRecord.class);
                    if(milkRecord.getAnimalName().equals(AnimalID)){
                        milkRecordList.add(milkRecord);
                        int currentDayProduction=milkRecord.getQuantity();
                        totalMilkProduction+=currentDayProduction;
                    }
                }
                totalProductionTxt.setText(totalMilkProduction+" L");
                milkRecordAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }


}