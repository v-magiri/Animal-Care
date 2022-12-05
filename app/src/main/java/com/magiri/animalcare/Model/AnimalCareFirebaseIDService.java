package com.magiri.animalcare.Model;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.magiri.animalcare.Session.Prevalent;

public class AnimalCareFirebaseIDService extends FirebaseMessagingService {
    String refreshToken;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        String FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isComplete()){
                    refreshToken=task.getResult();
                    if(FarmerID!=null){
                        FirebaseDatabase.getInstance().getReference("Tokens").child(FarmerID).child("token").setValue(refreshToken);
                    }else{
                        Log.e(TAG, "onComplete: User id is null");
                    }
                }
            }
        });
    }
}
