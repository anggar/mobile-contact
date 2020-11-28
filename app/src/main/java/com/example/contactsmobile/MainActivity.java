package com.example.contactsmobile;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

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
    protected void onResume() {
        contacts = ContactDbHelper.getAll();
        bindList();
        super.onResume();
    }
}
