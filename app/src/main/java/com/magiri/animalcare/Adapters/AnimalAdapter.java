package com.magiri.animalcare.Adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magiri.animalcare.AnimalProfile;
import com.magiri.animalcare.Model.Animal;
import com.magiri.animalcare.Model.MilkRecord;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Prevalent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.MyViewHolder> {
    private static final String TAG = "AnimalAdapter";
    Context context;
    List<Animal> animalList;
    String[] actions;
    AlertDialog optionAlert;
    private DatabaseReference ref,databaseReference;
    String FarmerID;
    AlertDialog milkAlertDialog;
    String str;
    DateFormat formatter;
    SimpleDateFormat sdf;

    public AnimalAdapter(Context context, List<Animal> animalList) {
        this.context = context;
        this.animalList = animalList;
        actions=context.getResources().getStringArray(R.array.actions);
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        ref= FirebaseDatabase.getInstance().getReference("Animals").child(FarmerID);
        databaseReference=FirebaseDatabase.getInstance().getReference("MilkProduction");
        formatter = new SimpleDateFormat("dd/MM/yy");
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar obj = Calendar.getInstance();
        str = formatter.format(obj.getTime());
    }

    @NonNull
    @Override
    public AnimalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.animal_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalAdapter.MyViewHolder holder, int position) {
        Animal animal=animalList.get(position);
        holder.statusTextView.setText(animal.getStatus());
        holder.NameTextView.setText(animal.getAnimalName());
        String DOB=animal.getDOB();
//        Long Age;
        try {
            Date date1=formatter.parse(DOB);
            Date date2=formatter.parse(str);
            Calendar m_calendar=Calendar.getInstance();
            m_calendar.setTime(date1);
            int nMonth1=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
            m_calendar.setTime(date2);
            int nMonth2=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
                    holder.ageTextView.setText(java.lang.Math.abs(nMonth2-nMonth1)+" months");
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            LocalDate date1=LocalDate.parse(DOB);
//            LocalDate date2=LocalDate.parse(str);
//            holder.ageTextView.setText(""+ChronoUnit.MONTHS.between(date2,date1)+"months");
////            Age= ChronoUnit.MONTHS.between(date2,date1);
//        }
        String imageUrl=animal.getAnimalImageUrl();
        if(imageUrl!=null){
            Glide.with(context).load(imageUrl).into(holder.animalImageView);
        }else {
            Glide.with(context).load(R.drawable.ic_vet).into(holder.animalImageView);
        }
        holder.actionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Please Select Action");
                int checkedItem=1;
                alertDialogBuilder.setSingleChoiceItems(actions, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                showHealthDialog(animal.getAnimalID());
                                break;
                            case 1:
                                addMilkProductionRecord(animal.getAnimalName());
                                break;
                            case 2:
                                addBreedingRecord(animal.getAnimalID());
                                break;
                            default:
                                Toast.makeText(context,"Please Choose Option",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        optionAlert.dismiss();
                    }
                });
                optionAlert = alertDialogBuilder.create();
                optionAlert.setCanceledOnTouchOutside(false);
                optionAlert.show();
            }
        });

        holder.animalItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, AnimalProfile.class);
                Bundle bundle=new Bundle();
                bundle.putString("AnimalID",animal.getAnimalName());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private void addBreedingRecord(String animalID) {

    }

    private void addMilkProductionRecord(String animalName) {
        AlertDialog.Builder alertDailogBuilder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        animalNameTxt.setText(animalName);

        //set Current Date to the DateEditText
        dateEditText.setText(str);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                int year= calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateEditText.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, year,month,day);
                datePickerDialog.show();
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
                    Toast.makeText(context,"Please Choose Milking Time",Toast.LENGTH_SHORT).show();
                    return;
                }
                recordMilkProduction(animalName,date,milkTime,quantity);
            }
        });

        milkAlertDialog=alertDailogBuilder.create();
        milkAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        milkAlertDialog.show();


    }

    private void recordMilkProduction(String animalName, String date, String milkTime, int quantity) {
        final DatabaseReference mRef;
        mRef=databaseReference.child(FarmerID);
        String recordID=mRef.push().getKey();
        MilkRecord milkRecord=new MilkRecord(animalName,milkTime,date,quantity);
        mRef.child(recordID).setValue(milkRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    Toast.makeText(context,"Production Successfully Recorded",Toast.LENGTH_SHORT).show();
                    milkAlertDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Something wrong Happened Please Try Again Later",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+e.getMessage());
                milkAlertDialog.dismiss();
            }
        });
    }

    private void showHealthDialog(String animalID) {
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }
    static class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView animalImageView;
        private TextView ageTextView,NameTextView,statusTextView;
        private ImageButton actionImageButton;
        private LinearLayout animalItemLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animalImageView=itemView.findViewById(R.id.animalImage);
            ageTextView=itemView.findViewById(R.id.animalAge);
            NameTextView=itemView.findViewById(R.id.animalNameTxt);
            statusTextView=itemView.findViewById(R.id.animalStatus);
            actionImageButton=itemView.findViewById(R.id.actionsBtn);
            animalItemLayout=itemView.findViewById(R.id.animalItemLayout);
        }
    }
}
