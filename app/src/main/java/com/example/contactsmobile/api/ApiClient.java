package com.example.contactsmobile.api;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.71:8001")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        return retrofit;
    }
}
