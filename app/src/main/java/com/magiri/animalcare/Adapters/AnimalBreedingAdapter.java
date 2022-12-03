package com.magiri.animalcare.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Animal;
import com.magiri.animalcare.Model.Breeding;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnimalBreedingAdapter extends RecyclerView.Adapter<AnimalBreedingAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "AnimalBreedingAdapter";
    Context context;
    List<Breeding> breedingList;
    private final DatabaseReference ref;
    List<Breeding> breedList;
    private final DatabaseReference databaseReference,mRef;
    private String FarmerID;
    ArrayAdapter breedingAdapter;
    String breed;
    ProgressDialog progressDialog;
    String str;
    SimpleDateFormat sdf;
    String[] breedingRecordActions;
    private AlertDialog optionAlert,breedingAlertDialog;

    public AnimalBreedingAdapter(Context context, List<Breeding> breedingList) {
        this.context = context;
        this.breedingList = breedingList;
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
        ref= FirebaseDatabase.getInstance().getReference("Veterinarian");
        databaseReference=FirebaseDatabase.getInstance().getReference("Animals").child(FarmerID);
        breedList=new ArrayList<>(breedingList);
        mRef= FirebaseDatabase.getInstance().getReference("Breeding").child(FarmerID);
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Updating Breeding Record");
        progressDialog.setCanceledOnTouchOutside(false);
        breedingRecordActions=context.getResources().getStringArray(R.array.Actions);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar obj = Calendar.getInstance();
        str = sdf.format(obj.getTime());
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.breed_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Breeding breeding=breedingList.get(position);
        holder.breedingDateTxt.setText(breeding.getBreedingDate());
        holder.costTxt.setText(breeding.getCost());
        holder.animalNameTxt.setText(breeding.getAnimalID());
        holder.bullNameTxt.setText(breeding.getBullName());
        String VETID=breeding.getVetID();
        ref.child(VETID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veterinarian vet=snapshot.getValue(Veterinarian.class);
                holder.vetNameTxt.setText(vet.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
        holder.actionImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Please Select Action");
                int checkedItem=1;
                alertDialogBuilder.setSingleChoiceItems(breedingRecordActions, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                UpdateBredingRecord(breeding);
                                break;
                            case 1:
                                mRef.child(breeding.getBreedingID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete()){
                                            breedingList.remove(position);
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
        getAnimalProfile(breeding.getAnimalID(),holder.animalImageView);
    }

    private void UpdateBredingRecord(Breeding breeding) {
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.add_breeding_record,null);
        alertDialogBuilder.setView(view);
//        alertDialogBuilder.setCancelable(false);
        TextView animalNameTxt=view.findViewById(R.id.animalNameTxt);
        TextInputEditText dateEditTxt=view.findViewById(R.id.dateText);
        AutoCompleteTextView breedTextView=view.findViewById(R.id.breedAutoCompleteTxt);
        EditText bullNameTxt=view.findViewById(R.id.bullNameTxt);
        EditText bullCodeTxt=view.findViewById(R.id.bullCodeTxt);
        EditText strawNumberTxt=view.findViewById(R.id.strawNumberTxt);
        EditText costEditTxt=view.findViewById(R.id.costTxt);
        Button saveBtn=view.findViewById(R.id.saveBreedingRecord);
        ImageView closeImg=view.findViewById(R.id.closeBtn);
        saveBtn.setText("Update Details");

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breedingAlertDialog.dismiss();
            }
        });


        String[] breeds=context.getResources().getStringArray(R.array.breed);
        ArrayAdapter breedAdapter=new ArrayAdapter(context,R.layout.item,breeds);
        breedTextView.setAdapter(breedAdapter);
        breedTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                breed=parent.getItemAtPosition(position).toString();
            }
        });

        dateEditTxt.setOnClickListener(new View.OnClickListener() {
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
                        dateEditTxt.setText(sdf.format(calendar.getTime()));
                    }
                });
            }
        });

        animalNameTxt.setText(breeding.getAnimalID());
        dateEditTxt.setText(breeding.getBreedingDate());
        breedTextView.setText(breeding.getBreedType());
        bullCodeTxt.setText(breeding.getBullCode());
        bullNameTxt.setText(breeding.getBullName());
        strawNumberTxt.setText(breeding.getStrawNumber());
        costEditTxt.setText(breeding.getCost());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bullName=bullNameTxt.getText().toString();
                String bullCode=bullCodeTxt.getText().toString();
                String strawNumber=strawNumberTxt.getText().toString();
                String cost=costEditTxt.getText().toString();
                String date=dateEditTxt.getText().toString();

                if(TextUtils.isEmpty(bullName)){
                    bullName=breeding.getBullName();
                }
                if(TextUtils.isEmpty(date)){
                    date=breeding.getBreedingDate();
                }
                if(TextUtils.isEmpty(bullCode)){
                    bullCode=breeding.getBullCode();
                }
                if(TextUtils.isEmpty(strawNumber)){
                    strawNumber=breeding.getStrawNumber();
                }
                if(TextUtils.isEmpty(breed)){
                    breed=breeding.getBreedType();
                }
                if(TextUtils.isEmpty(cost)){
                    cost=breeding.getCost();
                }
                progressDialog.show();
                updateBreedingRecord(breeding.getBreedingID(),bullName,bullCode,date,cost,strawNumber,breed);
            }
        });

        breedingAlertDialog=alertDialogBuilder.create();
        breedingAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        breedingAlertDialog.show();
    }

    private void updateBreedingRecord(String breedingID, String bullName, String bullCode, String date, String cost, String strawNumber, String breed) {
        mRef.child(breedingID).child("bullName").setValue(bullName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    mRef.child(breedingID).child("bullCode").setValue(bullCode)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                mRef.child(breedingID).child("breedingDate").setValue(date)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete()){
                                            mRef.child(breedingID).child("cost").setValue(cost)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isComplete()){
                                                        mRef.child(breedingID).child("strawNumber").setValue(strawNumber)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isComplete()){
                                                                    mRef.child(breedingID).child("breedType").setValue(breed)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isComplete()){
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(context,"SuccessFully Updated Breeding Record",Toast.LENGTH_SHORT).show();
                                                                                breedingAlertDialog.dismiss();
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
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void getAnimalProfile(String animalID, CircleImageView animalImageView) {
        databaseReference.child(animalID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Animal animal=snapshot.getValue(Animal.class);
                assert animal != null;
                String animalProfileImgUrl=animal.getAnimalImageUrl();

                if(TextUtils.isEmpty(animalProfileImgUrl)){
                    Glide.with(context).load(R.drawable.ic_cow).into(animalImageView);
                }else{
                    Glide.with(context).load(animalProfileImgUrl).into(animalImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
    @Override
    public int getItemCount() {
        return breedingList.size();
    }
    @Override
    public Filter getFilter() {
        return diseaseAdapter;
    }
    private final Filter diseaseAdapter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Breeding> filterBreeding=new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                filterBreeding.addAll(breedList);
            }else{
                String searchText=constraint.toString().toLowerCase();
                for(Breeding breeding:breedList){
                    if(breeding.getAnimalID().toLowerCase().contains(searchText)){
                        filterBreeding.add(breeding);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filterBreeding;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            breedingList.clear();
            breedingList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView animalNameTxt,bullNameTxt,costTxt,breedingDateTxt,vetNameTxt;
        CircleImageView animalImageView;
        ImageButton actionImageBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animalNameTxt=itemView.findViewById(R.id.animalNameTxt);
            bullNameTxt=itemView.findViewById(R.id.bullNameTextView);
            costTxt=itemView.findViewById(R.id.costTextView);
            breedingDateTxt=itemView.findViewById(R.id.breedingDateTextView);
            vetNameTxt=itemView.findViewById(R.id.vetNameTextView);
            animalImageView=itemView.findViewById(R.id.animalImageView);
            actionImageBtn=itemView.findViewById(R.id.actionsBtn);
        }
    }
}
