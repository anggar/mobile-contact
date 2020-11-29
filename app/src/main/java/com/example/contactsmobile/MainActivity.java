package com.example.contactsmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    ContactDbHelper dbHelper;
    ListView listView;
    ContactAdapter adapter;

    ArrayList<Contact> contacts;

    private void bindList() {
        adapter = new ContactAdapter(MainActivity.this, 12, contacts);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new ContactDbHelper(this);

        ExtendedFloatingActionButton btnAdd = findViewById(R.id.button_tambah);

        btnAdd.setOnClickListener(arg0 -> {
            Intent intent = new Intent(MainActivity.this, ContactFormActivity.class);
            startActivityForResult(intent, 0);
        });

        listView = findViewById(R.id.list_view);

        contacts = ContactDbHelper.getAll();
        bindList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != 1 && resultCode != 2) {
            return;
        }

        RelativeLayout layout = findViewById(R.id.main_layout);
        String message = (resultCode == 1) ? "Contact saved" : "Contact deleted";
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        contacts = ContactDbHelper.getAll();
        bindList();
        super.onResume();
    }
}
