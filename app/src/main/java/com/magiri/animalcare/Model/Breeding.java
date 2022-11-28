package com.magiri.animalcare.Model;

public class Breeding {
    String AnimalID,BreedingDate,BreedType,BullName,VetID,Cost,BullCode,StrawNumber,BreedingID;

    public Breeding() {
    }

    public Breeding(String animalID, String breedingDate, String breedType,
                    String bullName, String vetID, String cost, String bullCode,
                    String strawNumber, String breedingID) {
        this.AnimalID = animalID;
        this.BreedingDate = breedingDate;
        this.BreedingDate = breedType;
        this.BullName = bullName;
        this.VetID = vetID;
        this.Cost = cost;
        this.BullCode = bullCode;
        this.StrawNumber = strawNumber;
        this.BreedingID=breedingID;
    }

    public String getAnimalID() {
        return AnimalID;
    }

    public void setAnimalID(String animalID) {
        AnimalID = animalID;
    }

    public String getBreedingDate() {
        return BreedingDate;
    }

    public void setBreedingDate(String breedingDate) {
        BreedingDate = breedingDate;
    }

    public String getBreedType() {
        return BreedType;
    }

    public void setBreedType(String breedType) {
        BreedType = breedType;
    }

    public String getBullName() {
        return BullName;
    }

    public void setBullName(String bullName) {
        BullName = bullName;
    }

    public String getVetID() {
        return VetID;
    }

    public void setVetID(String vetID) {
        VetID = vetID;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getBullCode() {
        return BullCode;
    }

    public void setBullCode(String bullCode) {
        BullCode = bullCode;
    }

    public String getStrawNumber() {
        return StrawNumber;
    }

    public void setStrawNumber(String strawNumber) {
        StrawNumber = strawNumber;
    }

    public String getBreedingID() {
        return BreedingID;
    }

    public void setBreedingID(String breedingID) {
        BreedingID = breedingID;
    }
}
