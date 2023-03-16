package com.example.cspart.api;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.*;

public class RetrofitClientJava {

    private static RetrofitClientJava instance = null;
    private ApiInterface myApi;
    private String BASE_URL = "http://14.232.152.154:8084/api/";

    private RetrofitClientJava() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(ApiInterface.class);
    }

    public static synchronized RetrofitClientJava getInstance() {
        if (instance == null) {
            instance = new RetrofitClientJava();
        }
        return instance;
    }

    public ApiInterface getMyApi() {
        return myApi;
    }
}