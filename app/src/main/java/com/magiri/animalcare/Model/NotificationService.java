package com.magiri.animalcare.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    String title, notificationMessage, typePage;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        title=message.getData().get("Title");
        notificationMessage=message.getData().get("Message");
        typePage=message.getData().get("Typepage");

        ShowNotification.showNotification(getApplicationContext(),title,notificationMessage,typePage);
    }
}
