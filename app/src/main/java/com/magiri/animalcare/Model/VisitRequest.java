package com.magiri.animalcare.Model;

public class VisitRequest {
    String VetID,FarmerAddress,FarmerID,Status,VisitID,ServiceType;
    int VisitationFee;
    Boolean isPaid;
    double LocationLatitude,LocationLongitude;

    public VisitRequest(String vetID, String farmerAddress,
                        double locationLatitude, double locationLongitude, int visitationFee, String farmerID, String status, String visitID, Boolean isPaid,String serviceType) {
        this.VetID = vetID;
        this.FarmerAddress = farmerAddress;
        this.LocationLatitude = locationLatitude;
        this.LocationLongitude = locationLongitude;
        this.VisitationFee = visitationFee;
        this.FarmerID = farmerID;
        this.Status = status;
        this.VisitID = visitID;
        this.isPaid = isPaid;
        this.ServiceType=serviceType;
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

    public double getLocationLatitude() {
        return LocationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        LocationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return LocationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        LocationLongitude = locationLongitude;
    }

    public int getVisitationFee() {
        return VisitationFee;
    }

    public void setVisitationFee(int visitationFee) {
        VisitationFee = visitationFee;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public void setServiceType(String serviceType) {
        ServiceType = serviceType;
    }
}
