package com.magiri.animalcare.Model;

public class Veterinarian {
    String Name,RegistrationNumber,PhoneNumber,Password,County,Constituency,Ward;
    String VisitationFee,ProfilePicUrl,Skill,license;


    public Veterinarian() {
    }

    public Veterinarian(String name, String registrationNumber, String phoneNumber,
                        String password, String county, String constituency, String ward,
                        String visitationFee, String profilePicUrl, String skill, String license) {
        this.Name = name;
        this.RegistrationNumber = registrationNumber;
        this.PhoneNumber = phoneNumber;
        this.Password = password;
        this.County = county;
        this.Constituency = constituency;
        this.Ward = ward;
        this.VisitationFee = visitationFee;
        this.ProfilePicUrl = profilePicUrl;
        this.Skill = skill;
        this.license = license;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRegistrationNumber() {
        return RegistrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        RegistrationNumber = registrationNumber;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCounty() {
        return County;
    }

    public void setCounty(String county) {
        County = county;
    }

    public String getConstituency() {
        return Constituency;
    }

    public void setConstituency(String constituency) {
        Constituency = constituency;
    }

    public String getWard() {
        return Ward;
    }

    public void setWard(String ward) {
        Ward = ward;
    }

    public String getVisitationFee() {
        return VisitationFee;
    }

    public void setVisitationFee(String visitationFee) {
        VisitationFee = visitationFee;
    }

    public String getSkill() {
        return Skill;
    }

    public void setSkill(String skill) {
        Skill = skill;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        ProfilePicUrl = profilePicUrl;
    }
}
