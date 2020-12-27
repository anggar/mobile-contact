package com.example.contactsmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.contactsmobile.api.ApiClient;
import com.example.contactsmobile.api.ApiInterface;
import com.example.contactsmobile.model.Contact;
import com.example.contactsmobile.model.Picture;
import com.google.android.material.button.MaterialButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ContactFormActivity extends AppCompatActivity
{
    private Long contactId;
    private Contact contact;
    private ApiInterface api;
    private File imgFile;

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap bitmap;
        FileOutputStream fos;
        File picFile = new File(getApplicationContext().getCacheDir(), (new Date()).toString() + ".jpg");

        if (resultCode == Activity.RESULT_OK) {
            ImageView imageView = findViewById(R.id.ivPhoto);

            try {
                picFile.createNewFile();
                picFile.setWritable(true);
                fos = new FileOutputStream(picFile);

                if (requestCode == 221) {
                    if (data != null) {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        imageView.setImageBitmap(bitmap);

                        uriPhoto = Uri.fromFile(picFile);
                    }
                } else if (requestCode == 222) {
                    bitmap = (Bitmap) (data != null ? data.getExtras().get("data") : null);
                    imageView.setImageBitmap(bitmap);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                }

                imgFile = picFile;
            } catch (IOException e) {
                e.printStackTrace();
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

        api = ApiClient.getClient().create(ApiInterface.class);

        Bundle extras = getIntent().getExtras();

        MaterialButton btnPickPhoto = findViewById(R.id.btnPickPhoto);
        MaterialButton btnTakePhoto = findViewById(R.id.btnTakePic);
        MaterialButton btnUpload = findViewById(R.id.btnUpload);

        if (extras != null) {
            contactId = (Long) extras.get("id");
            contact = ContactDbHelper.getId(contactId);
            onEditIntent();
        }

        btnPickPhoto.setOnClickListener(view -> pickPhoto());
        btnTakePhoto.setOnClickListener(view -> takePhoto());
        btnUpload.setOnClickListener(view -> uploadPhoto());
    }

    private void uploadPhoto() {
        File picFile = new File(imgFile.getPath());

        Call<Picture> call = api.uploadImage(
            MultipartBody.Part.createFormData(
                    "file",
                    imgFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), picFile)
            )
        );

        call.enqueue(new Callback<Picture>() {
            @Override
            public void onResponse(Call<Picture> call, Response<Picture> response) {
                Toast.makeText(getApplicationContext(), "Image successfully uploaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Picture> call, Throwable t) {
                Log.e("HAHA", t.getMessage());
                Toast.makeText(getApplicationContext(), "Image can't be uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }
}