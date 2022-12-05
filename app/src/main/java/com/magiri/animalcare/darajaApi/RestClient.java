package com.magiri.animalcare.darajaApi;

import com.magiri.animalcare.Model.DiseaseDiagnosis;
import com.magiri.animalcare.Model.MyResponse;
import com.magiri.animalcare.Model.Notification;
import com.magiri.animalcare.Model.VisitPayment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
            @Query("amount") int Amount,
            @Query("phoneNumber") String PhoneNumber,
            @Query("acc_ref") String Account_Reference,
            @Query("ID") String visitID
            );

    @FormUrlEncoded
    @POST("diagnose")
    Call<DiseaseDiagnosis> diagnoseDisease(@Field("Symptoms") String symptoms);

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAfxZa5XQ:APA91bEawPPe_7D5hLqIzjyaU22lJh-61YnHZu_a8GYYifoJHUbUyMHMe058OrrVIooaEDLJvbfOfvb8z2Xr9nsQjm5705nqBD519L5_491jKJKZlmk_rvosPN_wvxUSaweBPgufNsBz"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Notification body);

}
