package com.magiri.animalcare.darajaApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Constants {
    public static final int CONNECT_TIMEOUT = 60 * 1000;

    public static final int VISIT_COST_PER_5_KILOMETRE = 50;
    public static final int VISIT_COST_PER_10_KILOMETRE=75;
    public static final int VISIT_COST_PER_ABOVE_10_KILOMETRE=100;
    public static final double EARTH_RADIUS = 3958.75;
    public static final int READ_TIMEOUT = 60 * 1000;

    public static final int WRITE_TIMEOUT = 60 * 1000;

    public static final String BASE_URL = "https://sandbox.safaricom.co.ke/";

    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    public static final String TRANSACTION_TYPE = "CustomerPayBillOnline";
    public static final String PARTYB = "174379"; //same as business shortcode above
    public static final String CALLBACKURL = "https://eo5r5urv9tgcxa7.m.pipedream.net";
    public static final String CONSUMER_KEY = "HG770gAkVPooIbBW84MPkMvUjiAgVYVJ";
    public static final String CONSUMER_SECRET = "KbUaA1CART8Hd3fk";
}
