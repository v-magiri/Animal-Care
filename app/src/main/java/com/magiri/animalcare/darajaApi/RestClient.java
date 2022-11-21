package com.magiri.animalcare.darajaApi;

import com.magiri.animalcare.Model.VisitPayment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestClient {
    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPUSH> sendPush(@Body STKPUSH stkPush);

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken();

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("stk/push")
    Call<STKResponse> pushStk(
            @Query("amount") String Amount,
            @Query("phoneNumber") String PhoneNumber,
            @Query("acc_ref") String Account_Reference,
            @Query("ID") String visitID
            );
}
