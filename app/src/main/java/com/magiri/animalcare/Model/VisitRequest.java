package com.magiri.animalcare.Model;

public class VisitRequest {
    String VetID,FarmerAddress,LocationLatitude,
            LocationLongitude,VisitationFee,FarmerID,Status,VisitID;
    Boolean isPaid;

    public VisitRequest(String vetID, String farmerAddress,
                        String locationLatitude, String locationLongitude, String visitationFee, String farmerID, String status, String visitID, Boolean isPaid) {
        this.VetID = vetID;
        this.FarmerAddress = farmerAddress;
        this.LocationLatitude = locationLatitude;
        this.LocationLongitude = locationLongitude;
        this.VisitationFee = visitationFee;
        this.FarmerID = farmerID;
        this.Status = status;
        this.VisitID = visitID;
        this.isPaid = isPaid;
    }

    public VisitRequest() {
    }

    public String getVetID() {
        return VetID;
    }

    public void setVetID(String vetID) {
        VetID = vetID;
    }

    public String getFarmerAddress() {
        return FarmerAddress;
    }

    public void setFarmerAddress(String farmerAddress) {
        FarmerAddress = farmerAddress;
    }

    public String getLocationLatitude() {
        return LocationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        LocationLatitude = locationLatitude;
    }

    public String getLocationLongitude() {
        return LocationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        LocationLongitude = locationLongitude;
    }

    public String getVisitationFee() {
        return VisitationFee;
    }

    public void setVisitationFee(String visitationFee) {
        VisitationFee = visitationFee;
    }

    public String getFarmerID() {
        return FarmerID;
    }

    public void setFarmerID(String farmerID) {
        FarmerID = farmerID;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getVisitID() {
        return VisitID;
    }

    public void setVisitID(String visitID) {
        VisitID = visitID;
    }
}
