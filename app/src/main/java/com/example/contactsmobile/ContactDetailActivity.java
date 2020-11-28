package com.example.contactsmobile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class ContactDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final double DEFAULT_LAT = -7.219;
    private static final double DEFAULT_LNG = 112.78;

    private GoogleMap map;
    private Contact contact;
    private Marker mapMarker;
    private LatLng mapLatLng;
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private TextView tvDistance;

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double lat = Double.parseDouble(contact.getLatitude());
            double lng = Double.parseDouble(contact.getLongitude());

            Location dest = new Location("Source");
            dest.setLatitude(lat);
            dest.setLongitude(lng);

            float dist = location.distanceTo(dest) / 1000; // in Km
            dist = (float) Math.round(dist * 100.0f)/ 100.0f;
            tvDistance.setText("Distance to " + contact.getName() + " is " + dist + " Km");
        }
    }

    private void calcDistance() {
        if (!locationPermissionGranted) {
            getLocationPermission();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 2, locationListener);
    }

    private boolean locationProvided() {
        return (!contact.getLatitude().equals("Nan") &&
                !contact.getLongitude().equals("Nan"));
    }

    private void setUserLocation() {
        if (!locationProvided()) {
            tvDistance.setText("Location is unavailable");
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(DEFAULT_LAT, DEFAULT_LNG), 2f));
            return;
        }

        Double lat = Double.parseDouble(contact.getLatitude());
        Double lng = Double.parseDouble(contact.getLongitude());
        setPointOnMap(lat, lng, 15f);
    }

    private void setPointOnMap(Double lat, Double lng, Float zoom) {
        LatLng latLng = new LatLng(lat, lng);

        if (mapMarker != null) mapMarker.remove();

        MarkerOptions markerOpt = new MarkerOptions()
                .position(latLng)
                .title("Selected Location");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mapMarker = map.addMarker(markerOpt);
        mapLatLng = latLng;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateValue() {
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvAddress = findViewById(R.id.tvAddress);

        tvName.setText(contact.getName());
        tvPhone.setText(contact.getPhone());
        tvAddress.setText(contact.getAddress());
    }

    private void deleteConfirmation() {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirmation, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setView(view);

        dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContactDbHelper.deleteOne(contact.getId());
                finish();
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        // stop requesting location when this activity is no longer active
        locationManager.removeUpdates(locationListener);
        if (mapMarker != null) mapMarker.remove();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        initMap();

        // init location manager
        locationListener = new MyLocationListener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Bundle extras = getIntent().getExtras();
        long contactId = (long) extras.get("id");
        contact = ContactDbHelper.getId(contactId);

        // set title to be the name of the contact
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(contact.getName());
        }

        updateValue();

        ExtendedFloatingActionButton btnEdit = findViewById(R.id.btnEdit);
        ExtendedFloatingActionButton btnDistance = findViewById(R.id.btnDistance);
        tvDistance = (TextView) findViewById(R.id.tvDistance);

        if (!locationProvided()) btnDistance.hide();

        btnEdit.setOnClickListener(view -> {
            Intent intent = new Intent(this, ContactFormActivity.class);
            intent.putExtra("id", contactId);
            startActivityForResult(intent, 2); // edit
        });

        btnDistance.setOnClickListener(view -> {
            getLocationPermission();
            calcDistance();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 2) {
            contact = ContactDbHelper.getId(contact.getId());
            updateValue();
            setUserLocation();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng defaultLocation = new LatLng(DEFAULT_LAT, DEFAULT_LNG);

        setUserLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnShare:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, contact.getName());
                intent.putExtra(Intent.EXTRA_TEXT, contact.getShareable());
                startActivity(intent);
                break;
            case R.id.mnDelete:
                deleteConfirmation();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}