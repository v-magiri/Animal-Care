package com.magiri.animalcare.darajaApi;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String getTimeStamp(){
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }
    public static String refactorPhoneNumber(String phoneNumber){
        if (phoneNumber.length() < 11 & phoneNumber.startsWith("0")) {
            String refactoredPhoneNumber = phoneNumber.replaceFirst("^0", "254");
            return refactoredPhoneNumber;
        }
        if (phoneNumber.length() == 13 && phoneNumber.startsWith("+")) {
            String refactoredPhoneNumber = phoneNumber.replaceFirst("^+", "");
            return refactoredPhoneNumber;
        }
        return phoneNumber;
    }
    public static String STKPUSHPassword(String businessCode,String passkey,String Timestamp){
        String str=businessCode+passkey+Timestamp;
        return Base64.encodeToString(str.getBytes(),Base64.NO_WRAP);
    }



}
