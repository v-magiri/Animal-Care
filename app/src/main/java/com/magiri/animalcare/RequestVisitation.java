package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.essam.simpleplacepicker.MapActivity;
import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magiri.animalcare.Model.VisitRequest;
import com.magiri.animalcare.Session.Prevalent;
import com.magiri.animalcare.UI.DropDown;
import com.magiri.animalcare.darajaApi.RestClient;
import com.magiri.animalcare.darajaApi.STKResponse;
import com.magiri.animalcare.darajaApi.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RequestVisitation extends AppCompatActivity {
    private static final String TAG = "RequestVisitation";
    private DropDown locationDropDown, servicesDropDown;
    private TextInputEditText diagnosisTxt;
    private Button diagnoseBtn,submitBtn;
    private ImageView locationPicker;
    private TextView locationTxt;
    private static String Country,Language,Api_Key;
    private static String Address,Latitude,Longitude;
    private static final String []mSupportedAreas={""};
    private DatabaseReference mRef;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_visitation);

        locationDropDown=findViewById(R.id.locationDropDown);
        servicesDropDown=findViewById(R.id.servicesDropDown);
        diagnosisTxt=findViewById(R.id.diagnosisTxt);
        diagnoseBtn=findViewById(R.id.diagnosisBtn);
        submitBtn=findViewById(R.id.SubmitBtn);
        locationPicker=findViewById(R.id.pickLocationImageView);
        locationTxt=findViewById(R.id.locationTxt);

        Country="Kenya";
        Language="English";
        Api_Key=getResources().getString(R.string.maps_ApiKey);
        mRef= FirebaseDatabase.getInstance().getReference("VisitRequest");

        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveVisitRequest();
            }
        });
        locationDropDown.setOptions(getResources().getStringArray(R.array.visitation_location_options));
        servicesDropDown.setOptions(getResources().getStringArray(R.array.service_options));
        locationPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(SimplePlacePicker.API_KEY,Api_Key);
                bundle.putString(SimplePlacePicker.COUNTRY,Country);
                bundle.putString(SimplePlacePicker.LANGUAGE,Language);
                bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,mSupportedAreas);
                intent.putExtras(bundle);
            }
        });

    }

    private void SaveVisitRequest() {

    }
    private void makeVisitationPayment(String visitationFee,String RegistrationNumber,String phoneNumber) {
        visitationFee="1";
        final DatabaseReference ref;
        String clientID= Prevalent.currentOnlineFarmer.getFarmerID();
//        ref=mRef.child(RegistrationNumber);
        String visitID=mRef.push().getKey();
        VisitRequest visitRequest=new VisitRequest(RegistrationNumber,Address,Latitude,Longitude,visitationFee,clientID,"Pending",visitID,false);
        assert visitID != null;
        mRef.child(visitID).setValue(visitRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"Request Recorded",Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "onComplete: Something Happened");
                }
                progressDialog.dismiss();
                bottomSheetDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                Toast.makeText(context,"Failed to Record your visit Request",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                bottomSheetDialog.dismiss();
            }
        });
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://ani-care.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();
        RestClient restClient=retrofit.create(RestClient.class);
        Call<STKResponse> call=restClient.pushStk(
                visitationFee,
                Utils.refactorPhoneNumber(phoneNumber),
                RegistrationNumber,
                visitID
        );
        call.enqueue(new Callback<STKResponse>() {
            @Override
            public void onResponse(Call<STKResponse> call, Response<STKResponse> response) {
                if(response.body().getResponseCode().equals("0")){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Visit Request Recorded",Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).i("Mpesa Worked: ");

                }else{
                    Toast.makeText(getApplicationContext(),"Something Wrong Happened Please Try Again",Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).i("Mpesa Failed: ");
                }
            }

            @Override
            public void onFailure(Call<STKResponse> call, Throwable t) {
                Timber.tag(TAG).d("onFailure: Something wrong Happened");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            String locationAddress=data.getStringExtra(SimplePlacePicker.SELECTED_ADDRESS);
            String latitude=String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA,-1));
            String longitude=String.valueOf(data.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA,-1));
            locationTxt.setText(locationAddress);
        }
    }

}