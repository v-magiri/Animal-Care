package com.magiri.animalcare.Model;

public class Chat {
    String Message,TimeStamp,FarmerName,VetName;
    Boolean IsFarmer,IsVet;

    public Chat(String message, String timeStamp, String farmerName, String vetName, Boolean isFarmer, Boolean isVet) {
        this.Message = message;
        this.TimeStamp = timeStamp;
        this.FarmerName = farmerName;
        this.VetName = vetName;
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

    public String getFarmerName() {
        return FarmerName;
    }

    public void setFarmerName(String farmerName) {
        FarmerName = farmerName;
    }

    public String getVetName() {
        return VetName;
    }

    public void setVetName(String vetName) {
        VetName = vetName;
    }
}
