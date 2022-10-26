package com.magiri.animalcare.darajaApi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestClient {
    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPUSH> sendPush(@Body STKPUSH stkPush);

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken();
}
