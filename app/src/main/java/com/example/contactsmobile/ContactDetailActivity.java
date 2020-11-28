package com.example.contactsmobile;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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


    private static final double DEFAULT_LAT = -7.219;
    private static final double DEFAULT_LNG = 112.78;
    private GoogleMap map;
    private Contact contact;
    private Marker mapMarker;
    private LatLng mapLatLng;

    private void setPointOnMap(Double lat, Double lng) {
        LatLng latLng = new LatLng(lat, lng);

        mapMarker.remove();

        MarkerOptions markerOpt = new MarkerOptions()
                .position(latLng)
                .title("Selected Location");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        mapMarker = map.addMarker(markerOpt);
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
        setContentView(R.layout.activity_contact_detail);

        Bundle extras = getIntent().getExtras();
        long contactId = (long) extras.get("id");
        contact = ContactDbHelper.getId(contactId);

        updateValue();

        ExtendedFloatingActionButton btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(view -> {
            Intent intent = new Intent(this, ContactFormActivity.class);
            intent.putExtra("id", contactId);
            startActivityForResult(intent, 2); // edit
        });

        initMap();
//        setPointOnMap(
//                Double.parseDouble(contact.getLatitude()),
//                Double.parseDouble(contact.getLongitude())
//        );
    }

    private void updateValue() {
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvAddress = findViewById(R.id.tvAddress);

        tvName.setText(contact.getName());
        tvPhone.setText(contact.getPhone());
        tvAddress.setText(contact.getAddress());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 2) {
            contact = ContactDbHelper.getId(contact.getId());
            updateValue();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng defaultLocation = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2));
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
                ContactDbHelper.deleteOne(contact.getId());
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}