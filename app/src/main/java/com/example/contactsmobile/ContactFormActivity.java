package com.example.contactsmobile;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private MaterialButton btnAdd;

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

        tetName.setText(contact.getName());
        tetPhone.setText(contact.getPhone());
        tetAddress.setText(contact.getAddress());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);
        initMap();

        Bundle extras = getIntent().getExtras();

        TextInputLayout tlAddress = findViewById(R.id.tlAddress);
        btnAdd = findViewById(R.id.btnAddContact);

        tlAddress.setEndIconOnClickListener(view -> searchLocation());

        if (extras != null) {
            contactId = (Long) extras.get("id");
            contact = ContactDbHelper.getId(contactId);
            onEditIntent();
            btnAdd.setOnClickListener(view -> onEditSave());
        } else {
            btnAdd.setOnClickListener(view -> onAddContact());
        }
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