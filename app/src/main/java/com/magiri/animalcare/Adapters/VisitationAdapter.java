package com.magiri.animalcare.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Model.Farmer;
import com.magiri.animalcare.Model.Veterinarian;
import com.magiri.animalcare.Model.VisitRequest;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisitationAdapter extends RecyclerView.Adapter<VisitationAdapter.MyViewHolder> {
    private static final String TAG = "VisitationAdapter";
    Context context;
    List<VisitRequest> visitationList;
    DatabaseReference ref;
    ProgressDialog progressDialog;
    String currentDate,currentTime;
    SimpleDateFormat sdf;
    AlertDialog crudAlertDialog;
    private DatabaseReference mRef,databaseReference;
    String[] actions={"Update Record","Delete Record"};
    public VisitationAdapter(Context context, List<VisitRequest> visitationList) {
        this.context = context;
        this.visitationList = visitationList;
        ref= FirebaseDatabase.getInstance().getReference();
        progressDialog=new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Saving Request Status");
        progressDialog.setMessage("Please Wait, while we updated request Status");
//        VetID= Prevalent.currentOnlineVeterinarian.getRegistrationNumber();
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        mRef=FirebaseDatabase.getInstance().getReference("Veterinarian");
        databaseReference=FirebaseDatabase.getInstance().getReference("VisitRequest");
        Calendar obj = Calendar.getInstance();
        currentDate = sdf.format(obj.getTime());
        currentTime=new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.visit_request_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        VisitRequest visit=visitationList.get(position);
        holder.statusTxt.setText(visit.getStatus());
        holder.serviceTypeTxt.setText(visit.getServiceType());
        holder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //crud operations
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Please Select Action");
                int checkedItem=1;
                alertDialogBuilder.setSingleChoiceItems(actions, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                updateRecord(visit.getVisitID());
                                break;
                            case 1:
                                databaseReference.child(visit.getVisitID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isComplete()){
                                            visitationList.remove(position);
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
                        crudAlertDialog.dismiss();
                    }
                });
                crudAlertDialog = alertDialogBuilder.create();
//                optionAlert.setCanceledOnTouchOutside(false);
                crudAlertDialog.show();
            }
        });
        getVetDetails(visit.getVetID(),holder.vetImageView,holder.vetNameTxt);
    }

    private void updateRecord(String visitID) {
        //todo add code to update visit Request
    }

    private void getVetDetails(String vetID, CircleImageView vetImageView, TextView vetNameTxt) {
        mRef.child(vetID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Veterinarian vet=snapshot.getValue(Veterinarian.class);
                String vetProfilePic=vet.getProfilePicUrl();

                if(TextUtils.isEmpty(vetProfilePic)){
                    Glide.with(context).load(R.drawable.ic_vet).into(vetImageView);
                }else{
                    Glide.with(context).load(vetProfilePic).into(vetImageView);
                }
                vetNameTxt.setText(vet.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return visitationList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageButton actionBtn;
        private CircleImageView vetImageView;
        private TextView vetNameTxt,dateTxt,serviceTypeTxt,statusTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            actionBtn=itemView.findViewById(R.id.actionsBtn);
            vetImageView=itemView.findViewById(R.id.requestVetImageView);
            vetNameTxt=itemView.findViewById(R.id.vetNameTextView);
            dateTxt=itemView.findViewById(R.id.requestDateTxt);
            serviceTypeTxt=itemView.findViewById(R.id.serviceTypeTxt);
            statusTxt=itemView.findViewById(R.id.requestStatusTextView);

        }
    }

}
