package com.example.contactsmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.contactsmobile.api.ApiClient;
import com.example.contactsmobile.api.ApiInterface;
import com.example.contactsmobile.model.Contact;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    ContactDbHelper dbHelper;
    ListView listView;
    ImageAdapter adapter;

    ArrayList<Contact> contacts;
    ArrayList<String> photos;
    Call<List<String>> call;

    ApiInterface api;
    private Callback<List<String>> callback;

    private void bindList() {
        adapter = new ImageAdapter(MainActivity.this, 12, photos);
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

        api = ApiClient.getClient().create(ApiInterface.class);

        call = api.listImage();
        callback = new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                photos = (ArrayList<String>) response.body();
                bindList();
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        };

        photos = new ArrayList<>();
        call.enqueue(callback);

        contacts = ContactDbHelper.getAll();
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
//        call.enqueue(callback);
        super.onResume();
    }
}
