package com.magiri.animalcare.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magiri.animalcare.Model.MilkRecord;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProductionAdapter extends RecyclerView.Adapter<ProductionAdapter.MyViewHolder> implements Filterable {

    Context context;
    List<MilkRecord> milkRecordList;
    List<MilkRecord> prodList;
    String[] milkRecordCrud;
    AlertDialog optionAlert;
    private DatabaseReference mRef;
    String FarmerID;
    AlertDialog milkAlertDialog;
    String str;
    SimpleDateFormat sdf;
    ArrayAdapter milkingTimeAdapter;
    String updateMilkTime="";
    ProgressDialog progressDialog;

    public ProductionAdapter(Context context, List<MilkRecord> milkRecordList) {
        this.context = context;
        this.milkRecordList = milkRecordList;
        prodList=new ArrayList<>(milkRecordList);
        milkRecordCrud=context.getResources().getStringArray(R.array.Actions);
        milkingTimeAdapter=new ArrayAdapter(context,R.layout.item,context.getResources().getStringArray(R.array.milkingTime));
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar obj = Calendar.getInstance();
        str = sdf.format(obj.getTime());
        mRef= FirebaseDatabase.getInstance().getReference("MilkProduction").child(FarmerID);
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Updating Milk Record");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.production_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MilkRecord milkRecord=milkRecordList.get(position);
        holder.quantityTxt.setText(String.valueOf(milkRecord.getQuantity()));
        holder.milkingTimeTxt.setText(milkRecord.getMilkTime());
        holder.animalNameTxt.setText(milkRecord.getAnimalName());
        holder.dateTxt.setText(milkRecord.getDate());
        holder.actionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Please Select Action");
                int checkedItem=1;
                alertDialogBuilder.setSingleChoiceItems(milkRecordCrud, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                UpdateMilkRecord(milkRecord.getMilkRecordID(),milkRecord.getAnimalName(),milkRecord.getQuantity(),milkRecord.getDate(),milkRecord.getMilkTime());
                                break;
                            case 1:
                                mRef.child(milkRecord.getMilkRecordID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete()){
                                            milkRecordList.remove(position);
                                            notifyItemRemoved(position);
                                            Toast.makeText(context,"Successfully Deleted Record",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;
                            default:
                                Toast.makeText(context,"Please Choose Option",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        optionAlert.dismiss();
                    }
                });
                optionAlert = alertDialogBuilder.create();
//                optionAlert.setCanceledOnTouchOutside(false);
                optionAlert.show();
            }
        });
    }

    private void UpdateMilkRecord(String milkRecordID, String animalName, int quantity, String date, String milkTime) {
        AlertDialog.Builder alertDailogBuilder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.update_milk_record_layout,null);
        alertDailogBuilder.setView(view);
//        alertDailogBuilder.setCancelable(false);
        TextView animalNameTxt=view.findViewById(R.id.animalNameTxt);
        TextInputEditText dateEditText=view.findViewById(R.id.dateText);
        TextInputEditText quantityEditText=view.findViewById(R.id.milkQuantityTxt);
        Button AddMilkRecord=view.findViewById(R.id.addMilkRecord);
        AutoCompleteTextView milkingTime=view.findViewById(R.id.milkingTimeTxt);
        ImageView closeImageView=view.findViewById(R.id.closeBtn);

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                milkAlertDialog.dismiss();
            }
        });
        animalNameTxt.setText(animalName);

        //set Current Date to the DateEditText
        dateEditText.setText(date);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
                builder.setTitleText("Milk Date");
                calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
                builder.setCalendarConstraints(calendarConstraintBuilder.build());

                final MaterialDatePicker<Long> materialDatePicker = builder.build();
                materialDatePicker.show(((AppCompatActivity) context).getSupportFragmentManager(),materialDatePicker.toString());
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

        milkingTime.setAdapter(milkingTimeAdapter);
        milkingTime.setText(milkTime);
        quantityEditText.setText(""+quantity);

//        milkingTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                milkingTime.setText("");
//            }
//        });
        milkingTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateMilkTime=parent.getItemAtPosition(position).toString();
            }
        });

        AddMilkRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String animalName=animalNameTxt.getText().toString();
                String updateDate=dateEditText.getText().toString();
                int updateQuantity= 0;
                updateQuantity=Integer.parseInt(quantityEditText.getText().toString());
//                String updateMilkTime=milkingTime.getText().toString();

                if(TextUtils.isEmpty(updateDate)){
//                    dateEditText.setError("Required");
                    updateDate=date;
                }
                if(updateQuantity==0){
//                    quantityEditText.setError("Required");
                    updateQuantity=quantity;
                }
                if(updateMilkTime.equals("")){
                    updateMilkTime=milkTime;
                }
                progressDialog.show();
                updateMilkRecordDetails(updateDate,updateMilkTime,updateQuantity,milkRecordID);
            }
        });

        milkAlertDialog=alertDailogBuilder.create();
        milkAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        milkAlertDialog.show();
    }

    private void updateMilkRecordDetails(String updateDate, String updateMilkTime,
                                         int updateQuantity, String milkRecordID) {
        mRef.child(milkRecordID).child("date").setValue(updateDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    mRef.child(milkRecordID).child("quantity").setValue(updateQuantity).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                mRef.child(milkRecordID).child("milkTime").setValue(updateMilkTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete()){
                                            Toast.makeText(context,"Successfully Update Record",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            milkAlertDialog.dismiss();
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

    @Override
    public int getItemCount() {
        return milkRecordList.size();
    }

    @Override
    public Filter getFilter() {
        return productionFilter;
    }
    private final Filter productionFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MilkRecord> filterProduction=new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                filterProduction.addAll(prodList);
            }else{
                String searchText=constraint.toString().toLowerCase();
                for(MilkRecord record:prodList){
                    if(record.getAnimalName().toLowerCase().contains(searchText)){
                        filterProduction.add(record);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filterProduction;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            milkRecordList.clear();
            milkRecordList.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView animalNameTxt,dateTxt,milkingTimeTxt,quantityTxt;
        private ImageButton actionImageButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTxt=itemView.findViewById(R.id.animalNameTxt);
            dateTxt=itemView.findViewById(R.id.milkDateTextView);
            milkingTimeTxt=itemView.findViewById(R.id.milkingTimeTextView);
            quantityTxt=itemView.findViewById(R.id.quantityTextView);
            actionImageButton=itemView.findViewById(R.id.actionsBtn);
        }
    }
}
