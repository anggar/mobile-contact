package com.example.contactsmobile;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactFormActivity extends AppCompatActivity
    implements OnMapReadyCallback, GoogleMap.OnMapClickListener
{
    private ContactDbHelper dbHelper;
    private GoogleMap mGmap;
    private SupportMapFragment mapFragment;
    private Marker mapMarker;
    private LatLng mapLatLng;

    private static final double DEFAULT_LAT = 0.7893;
    private static final double DEFAULT_LNG = 113.9213;

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

        String latitude = "-1", longitude = "-1";
        if (mapLatLng != null) {
            latitude = String.valueOf(mapLatLng.latitude);
            longitude = String.valueOf(mapLatLng.longitude);
        }

        return new Contact(
                0, // unset
                formValues.get(0), formValues.get(1), formValues.get(2),
                latitude, longitude
        );
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

    private void onAddContact() {
        Contact contact = getContact();

        if (contact != null) {
            dbHelper.insertOne(contact);
            Intent intent = new Intent();
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
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);
        initMap();

        dbHelper = new ContactDbHelper(this);

        TextInputLayout tlAddress = findViewById(R.id.tlAddress);
        MaterialButton btnAdd = findViewById(R.id.btnAddContact);

        tlAddress.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLocation();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddContact();
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGmap = googleMap;
        LatLng defaultLocation = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        mGmap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        setPointOnMap(latLng.latitude, latLng.longitude);
    }
}