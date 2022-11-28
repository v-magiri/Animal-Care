package com.magiri.animalcare.Model;

public class DiseaseTreatment {
    String AnimalID,VetID,Diagnosis,TreatmentDate,Treatment,Cost,TreatmentID;

    public DiseaseTreatment() {
    }

    public DiseaseTreatment(String animalID, String vetID, String diagnosis,
                            String treatmentDate, String treatment, String cost, String treatmentID) {
        this.AnimalID = animalID;
        this.VetID = vetID;
        this.Diagnosis = diagnosis;
        this.TreatmentDate = treatmentDate;
        this.Treatment = treatment;
        this.Cost = cost;
        this.TreatmentID=treatmentID;
    }

    public String getAnimalID() {
        return AnimalID;
    }

    public void setAnimalID(String animalID) {
        AnimalID = animalID;
    }

    public String getVetID() {
        return VetID;
    }

    public void setVetID(String vetID) {
        VetID = vetID;
    }

    public String getDiagnosis() {
        return Diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        Diagnosis = diagnosis;
    }

    public String getTreatmentDate() {
        return TreatmentDate;
    }

    public void setTreatmentDate(String treatmentDate) {
        TreatmentDate = treatmentDate;
    }

    public String getTreatment() {
        return Treatment;
    }

    public void setTreatment(String treatment) {
        Treatment = treatment;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getTreatmentID() {
        return TreatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        TreatmentID = treatmentID;
    }
}
