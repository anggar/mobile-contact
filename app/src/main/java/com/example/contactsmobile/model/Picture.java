package com.example.contactsmobile.model;

import androidx.annotation.Nullable;
import com.squareup.moshi.Json;

public class Picture {
    @Json(name="path")
    private String path;

    @Nullable
    @Json(name="timestamp")
    private String timestamp;

    @Json(name="status")
    private String status;


    public Picture(String path) {
        this.path = path;
    }
}
