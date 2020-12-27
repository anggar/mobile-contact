package com.example.contactsmobile.api;

import com.example.contactsmobile.model.Picture;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiInterface {

    @Multipart
    @POST("/upload")
    Call<Picture> uploadImage(@Part MultipartBody.Part file);

    @GET("/pics")
    Call<List<String>> listImage();
}
