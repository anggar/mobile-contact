package com.example.contactsmobile;

import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

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

import java.util.ArrayList;

public class ContactFormActivity extends AppCompatActivity
    implements OnMapReadyCallback, GoogleMap.OnMapClickListener
{
    private ContactDbHelper dbHelper;
    private GoogleMap mGmap;
    private SupportMapFragment mapFragment;
    private Marker mapMarker;
    private LatLng mapLatLng;
    private boolean mapVisible = false;

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

    private void onAddContact() {
        Contact contact = getContact();

        if (contact != null) {
            dbHelper.insertOne(contact);
            Intent intent = new Intent();
            setResult(1, intent);
            finish();
        }
    }

    private void initMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
    }

    private void resetMap() {
        if (mapMarker != null) mapMarker.remove();
        mapLatLng = null;
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
                if (mapVisible) {
                    mapFragment.getView().setVisibility(View.GONE);
                    resetMap();
                }
                else {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    Toast.makeText(ContactFormActivity.this, "Choose Location", Toast.LENGTH_SHORT).show();
                }
                mapVisible = !mapVisible;
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
        mGmap.setOnMapClickListener(this);

        LatLng defaultLocation = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        mGmap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mapMarker != null) mapMarker.remove();

        MarkerOptions markerOpt = new MarkerOptions()
                .position(latLng)
                .title("Selected Location");
        mapMarker = mGmap.addMarker(markerOpt);
        mapLatLng = latLng;
    }
}