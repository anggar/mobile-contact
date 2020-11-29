package com.example.contactsmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContactFormActivity extends AppCompatActivity
    implements OnMapReadyCallback, GoogleMap.OnMapClickListener
{
    private GoogleMap mGmap;
    private Marker mapMarker;
    private LatLng mapLatLng;

    private Long contactId;
    private Contact contact;

    private static final double DEFAULT_LAT = 0.7893;
    private static final double DEFAULT_LNG = 113.9213;
    private Button btnAdd;
    private Uri uriPhoto;

    private Contact getContact() {
        int[] tetViewId = { R.id.tetName, R.id.tetPhone, R.id.tetAddress };
        ArrayList<String> formValues = new ArrayList<>();

        for (int viewId: tetViewId) {
            Editable et = ((TextInputEditText) findViewById(viewId)).getText();
            if (et == null) return null;

            String value = et.toString();

            if (value.isEmpty()) {
                Toast.makeText(this, "Insufficient data", Toast.LENGTH_SHORT).show();
                return null;
            } else {
                formValues.add(et.toString());
            }
        }

        String latitude = "NaN", longitude = "Nan";
        if (mapLatLng != null) {
            latitude = String.valueOf(mapLatLng.latitude);
            longitude = String.valueOf(mapLatLng.longitude);
        }

        return new Contact(
                0, // unset
                formValues.get(0), formValues.get(1), formValues.get(2),
                latitude, longitude, uriPhoto != null ? uriPhoto.toString() : null
        );
    }

    private void pickPhoto() {
        Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
        picker.setType("image/*");
        startActivityForResult(picker, 100);
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
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void searchLocation() {
        Geocoder geocoder = new Geocoder(ContactFormActivity.this);
        Editable et = ((TextInputEditText) findViewById(R.id.tetAddress)).getText();
        List<Address> addrResult;

        if (et != null && et.toString().isEmpty()) {
            Toast.makeText(ContactFormActivity.this, "Provide a location",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            assert et != null;
            addrResult = geocoder.getFromLocationName(et.toString(), 1);

            if (addrResult.isEmpty()) {
                Toast.makeText(ContactFormActivity.this, "Location not found",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Address address = addrResult.get(0);
            Double lat = address.getLatitude(),
                   lng = address.getLongitude();
            setPointOnMap(lat, lng);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onEditIntent() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Edit contact");
        }

        btnAdd.setText("Save contact");

        TextInputEditText tetName = findViewById(R.id.tetName);
        TextInputEditText tetPhone = findViewById(R.id.tetPhone);
        TextInputEditText tetAddress = findViewById(R.id.tetAddress);
        ImageView ivPhoto = findViewById(R.id.ivPhoto);

        tetName.setText(contact.getName());
        tetPhone.setText(contact.getPhone());
        tetAddress.setText(contact.getAddress());

        String photo = contact.getPhoto();
        if (photo != null) {
            ivPhoto.setImageURI(Uri.parse(photo));
        }
    }

    private void onEditSave() {
        Contact contact = getContact();
        Intent intent = new Intent();

        ContactDbHelper.updateOne(contactId, contact);
        setResult(2, intent);
        finish();
    }

    private void onAddContact() {
        Contact contact = getContact();
        Intent intent = new Intent();

        if (contact != null) {
            ContactDbHelper.insertOne(contact);
            setResult(1, intent);
            finish();
        }
    }

    private void setPointOnMap(Double lat, Double lng) {
        LatLng latLng = new LatLng(lat, lng);

        if (mapMarker != null) mapMarker.remove();

        MarkerOptions markerOpt = new MarkerOptions()
                .position(latLng)
                .title("Selected Location");
        mGmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        mapMarker = mGmap.addMarker(markerOpt);
        mapLatLng = latLng;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private boolean locationProvided() {
        return (!contact.getLatitude().equals("Nan") &&
                !contact.getLongitude().equals("Nan"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);
        initMap();

        Bundle extras = getIntent().getExtras();

        TextInputLayout tlAddress = findViewById(R.id.tlAddress);
        btnAdd = findViewById(R.id.btnAddContact);
        MaterialButton btnPickPhoto = findViewById(R.id.btnPickPhoto);

        tlAddress.setEndIconOnClickListener(view -> searchLocation());

        if (extras != null) {
            contactId = (Long) extras.get("id");
            contact = ContactDbHelper.getId(contactId);
            onEditIntent();
            btnAdd.setOnClickListener(view -> onEditSave());
        } else {
            btnAdd.setOnClickListener(view -> onAddContact());
        }

        btnPickPhoto.setOnClickListener(view -> pickPhoto());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGmap = googleMap;
        LatLng locs;

        if (contact != null && locationProvided()) {
            double lat = Double.parseDouble(contact.getLatitude());
            double lng = Double.parseDouble(contact.getLongitude());

            mapLatLng = new LatLng(lat, lng);
            setPointOnMap(lat, lng);
        }
        else {
            locs = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
            mGmap.moveCamera(CameraUpdateFactory.newLatLngZoom(locs, 2));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        setPointOnMap(latLng.latitude, latLng.longitude);
    }
}