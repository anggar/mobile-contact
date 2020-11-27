package com.example.contactsmobile;

import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class ContactFormActivity extends AppCompatActivity {

    private ContactDbHelper dbHelper;

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

        return new Contact(
                0, // unset
                formValues.get(0), formValues.get(1), formValues.get(2),
                "0.0", "0.0"
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);

        dbHelper = new ContactDbHelper(this);

        TextInputLayout tlAddress = findViewById(R.id.tlAddress);
        MaterialButton btnAdd = findViewById(R.id.btnAddContact);

        tlAddress.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: change to geocode latlng
                Toast.makeText(ContactFormActivity.this, "Change to geocode", Toast.LENGTH_SHORT).show();
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
}