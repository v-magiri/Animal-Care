package com.magiri.animalcare.Model;

public class Notification {
    public Data data;
    public String ReceiverToken;

    public Notification(Data data, String receiverToken) {
        this.data = data;
        this.ReceiverToken = receiverToken;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getReceiverToken() {
        return ReceiverToken;
    }

    public void setReceiverToken(String receiverToken) {
        ReceiverToken = receiverToken;
    }
}
