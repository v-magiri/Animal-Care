package com.magiri.animalcare.Model;

public class Animal {
    String AnimalName,Breed,DOB,OwnerID,AnimalID,Status,AnimalImageUrl;

    public Animal(String animalName, String breed, String DOB, String ownerID,
                  String animalID,String status,String animalImageUrl) {
        this.AnimalName = animalName;
        this.Breed = breed;
        this.DOB = DOB;
        this.OwnerID = ownerID;
        this.AnimalID = animalID;
        this.Status=status;
        this.AnimalImageUrl=animalImageUrl;
//        this.Gender=gender;
    }


    public Animal() {
    }

    public String getAnimalName() {
        return AnimalName;
    }

    public void setAnimalName(String animalName) {
        AnimalName = animalName;
    }

    public String getBreed() {
        return Breed;
    }

    public void setBreed(String breed) {
        Breed = breed;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getOwnerID() {
        return OwnerID;
    }

    public void setOwnerID(String ownerID) {
        OwnerID = ownerID;
    }

    public String getAnimalID() {
        return AnimalID;
    }

    public void setAnimalID(String animalID) {
        AnimalID = animalID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getAnimalImageUrl() {
        return AnimalImageUrl;
    }

    public void setAnimalImageUrl(String animalImageUrl) {
        AnimalImageUrl = animalImageUrl;
    }

//    public String getGender() {
//        return Gender;
//    }
//
//    public void setGender(String gender) {
//        Gender = gender;
//    }
}
