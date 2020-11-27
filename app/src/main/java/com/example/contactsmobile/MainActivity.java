package com.example.contactsmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    ContactDbHelper dbHelper;
    ListView listView;
    ContactAdapter adapter;
    HashMap<String, String> map;

    ArrayList<Contact> contacts;

    private void bindList() {
//        ArrayList<HashMap<String,String>> list = new ArrayList<>();
//        for (Contact contact: contacts){
//            map = new HashMap<>();
//            map.put("Nama", contact.getName());
//            map.put("Nomor", contact.getPhone());
//            map.put("Gambar", Integer.toString(R.drawable.gambar));
//            list.add(map);
//        }

//        adapter = new SimpleAdapter(
//                this, list, R.layout.list_item,
//                new String[] { "Nama", "Nomor", "Gambar" },
//                new int[] { R.id.tvName, R.id.tvPhone, R.id.ivPhoto}
//        );
        adapter = new ContactAdapter(MainActivity.this, 12, contacts);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExtendedFloatingActionButton btnAdd = findViewById(R.id.button_tambah);

        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, ContactFormActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        dbHelper = new ContactDbHelper(this);
        contacts = dbHelper.getAll();

        listView = findViewById(R.id.list_view);
        bindList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Contact latest = dbHelper.getLastOne();

        if (resultCode == 1) {
            adapter.add(latest);
        }
    }
}
