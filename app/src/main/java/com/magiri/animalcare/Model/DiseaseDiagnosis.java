package com.magiri.animalcare.Model;

public class DiseaseDiagnosis {
    String Disease;
    int CertaintyFactor;

    public DiseaseDiagnosis(String disease, int certaintyFactor) {
        this.Disease = disease;
        this.CertaintyFactor = certaintyFactor;
    }

    public String getDisease() {
        return Disease;
    }

    public void setDisease(String disease) {
        Disease = disease;
    }

    public int getCertaintyFactor() {
        return CertaintyFactor;
    }

    public void setCertaintyFactor(int certaintyFactor) {
        CertaintyFactor = certaintyFactor;
    }
}
