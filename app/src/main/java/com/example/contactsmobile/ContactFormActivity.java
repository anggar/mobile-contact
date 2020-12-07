package com.example.contactsmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ContactFormActivity extends AppCompatActivity
{
    private Long contactId;
    private Contact contact;

    private Uri uriPhoto;

    private void pickPhoto() {
        Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
        picker.setType("image/*");
        startActivityForResult(picker, 221);
    }

    private void takePhoto() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 222);
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.ENGLISH).format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap bitmap;
        FileOutputStream fos;
        File picFile = getOutputMediaFile();

        if (resultCode == Activity.RESULT_OK) {
            ImageView imageView = findViewById(R.id.ivPhoto);

            if (requestCode == 221) {
                if (data != null) {
                    try {
                        fos = new FileOutputStream(picFile);

                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        imageView.setImageBitmap(bitmap);

                        uriPhoto = Uri.fromFile(picFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == 222) {
                bitmap = (Bitmap) (data != null ? data.getExtras().get("data") : null);
                imageView.setImageBitmap(bitmap);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onEditIntent() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Edit contact");
        }

        ImageView ivPhoto = findViewById(R.id.ivPhoto);

        String photo = contact.getPhoto();
        if (photo != null) {
            ivPhoto.setImageURI(Uri.parse(photo));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);

        Bundle extras = getIntent().getExtras();

        MaterialButton btnPickPhoto = findViewById(R.id.btnPickPhoto);
        MaterialButton btnTakePhoto = findViewById(R.id.btnTakePic);

        if (extras != null) {
            contactId = (Long) extras.get("id");
            contact = ContactDbHelper.getId(contactId);
            onEditIntent();
        }

        btnPickPhoto.setOnClickListener(view -> pickPhoto());
        btnTakePhoto.setOnClickListener(view -> takePhoto());
    }
}