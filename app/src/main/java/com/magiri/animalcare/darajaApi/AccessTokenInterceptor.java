package com.magiri.animalcare.darajaApi;

import static com.magiri.animalcare.darajaApi.Constants.CONSUMER_KEY;
import static com.magiri.animalcare.darajaApi.Constants.CONSUMER_SECRET;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AccessTokenInterceptor implements Interceptor {
    public AccessTokenInterceptor() {
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String keyAuth=CONSUMER_KEY+":"+CONSUMER_SECRET;
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Basic " + Base64.encodeToString(keyAuth.getBytes(), Base64.NO_WRAP))
                .build();
        return chain.proceed(request);
    }
}
