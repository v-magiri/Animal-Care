package com.magiri.animalcare.Model;

public class Chat {
    String Message, TimeStamp, FarmerID, VetID;
    Boolean IsFarmer, IsVet;

    public Chat(String message, String timeStamp, String farmerID, String vetID, Boolean isFarmer, Boolean isVet) {
        this.Message = message;
        this.TimeStamp = timeStamp;
        this.FarmerID = farmerID;
        this.VetID = vetID;
        this.IsFarmer = isFarmer;
        this.IsVet = isVet;
    }

    public Chat() {
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getFarmerID() {
        return FarmerID;
    }

    public void setFarmerID(String farmerID) {
        FarmerID = farmerID;
    }

    public String getVetID() {
        return VetID;
    }

    public void setVetID(String vetID) {
        VetID = vetID;
    }

    public Boolean getFarmer() {
        return IsFarmer;
    }

    public void setFarmer(Boolean farmer) {
        IsFarmer = farmer;
    }

    public Boolean getVet() {
        return IsVet;
    }

    public void setVet(Boolean vet) {
        IsVet = vet;
    }
}
