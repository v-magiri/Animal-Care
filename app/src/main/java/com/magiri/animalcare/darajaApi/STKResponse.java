package com.magiri.animalcare.darajaApi;

public class STKResponse {
    String MerchantRequestID,CheckoutRequestID,ResponseCode,ResponseDescription,CustomerMessage;

    public STKResponse(String merchantRequestID, String checkoutRequestID, String responseCode, String responseDescription, String customerMessage) {
        this.MerchantRequestID = merchantRequestID;
        this.CheckoutRequestID = checkoutRequestID;
        this.ResponseCode = responseCode;
        this.ResponseDescription = responseDescription;
        this.CustomerMessage = customerMessage;
    }

    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        MerchantRequestID = merchantRequestID;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public void setCheckoutRequestID(String checkoutRequestID) {
        CheckoutRequestID = checkoutRequestID;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        ResponseDescription = responseDescription;
    }

    public String getCustomerMessage() {
        return CustomerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        CustomerMessage = customerMessage;
    }
}

