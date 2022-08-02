package com.magiri.animalcare.Model;

public class Farmer {
    String FarmerName,PhoneNumber,FarmerLocation,FarmerID,FarmerPassword;

    public Farmer() {
    }

    public Farmer(String farmerID, String farmerName, String phoneNumber, String farmerLocation, String farmerPassword) {
        this.FarmerName = farmerName;
        this.PhoneNumber = phoneNumber;
        this.FarmerLocation = farmerLocation;
        this.FarmerID = farmerID;
        this.FarmerPassword = farmerPassword;
    }

    public String getFarmerName() {
        return FarmerName;
    }

    public void setFarmerName(String farmerName) {
        FarmerName = farmerName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getFarmerLocation() {
        return FarmerLocation;
    }

    public void setFarmerLocation(String farmerLocation) {
        FarmerLocation = farmerLocation;
    }

    public String getFarmerID() {
        return FarmerID;
    }

    public void setFarmerID(String farmerID) {
        FarmerID = farmerID;
    }

    public String getFarmerPassword() {
        return FarmerPassword;
    }

    public void setFarmerPassword(String farmerPassword) {
        FarmerPassword = farmerPassword;
    }
}
