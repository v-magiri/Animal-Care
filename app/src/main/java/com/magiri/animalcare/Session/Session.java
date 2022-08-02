package com.magiri.animalcare.Session;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private static Session sessionInstance;
    private static Context mContext;

    private static final String ANIMALCARE_PREF="com.magiri.animalcare";
    private static final String User_ID="User_ID";
    private static final String Name="VetName";

    private Session(Context context){
        mContext=context;
    }
    public static synchronized  Session getInstance(Context context){
        if(sessionInstance==null){
            sessionInstance=new Session(context);
        }
        return sessionInstance;
    }
    public boolean FarmerLogin(String vetId,String VetName){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(ANIMALCARE_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(User_ID,vetId);
        editor.putString(Name,VetName);

        editor.apply();

        return true;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ANIMALCARE_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User_ID, null) != null;
    }
    public boolean logout(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ANIMALCARE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }
    public String getFarmerID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ANIMALCARE_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User_ID, null);
    }

    public String getFarmerName(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ANIMALCARE_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Name, null);
    }

}
