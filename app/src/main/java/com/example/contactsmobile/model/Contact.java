package com.example.contactsmobile.model;

import android.provider.BaseColumns;
import androidx.annotation.Nullable;

public class Contact implements BaseColumns
{
    public static final String TABLE_NAME = "contact";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PHOTO_PATH = "photo";

    private String name, address, phone, latitude, longitude;
    private @Nullable String photo;
    private long id;

    public Contact(long id, String name, String phone, String address,
                   String latitude, String longitude, @Nullable String photo) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
    }

    public String getShareable() {
        return name +
                "\n Phone: " + phone +
                "\n Address: " + address;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", photo='" + photo + '\'' +
                ", id=" + id +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(@Nullable String photo) {
        this.photo = photo;
    }
}
