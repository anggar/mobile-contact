package com.example.contactsmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    ContactDbHelper dbHelper;
    ListView listView;
    SimpleAdapter adapter;
    HashMap<String, String> map;
    ArrayList<HashMap<String, String>> mylist;
    String[] jdl; //deklarasi judul iem
    String[] ktr; //deklarasi keterangan item
    String[] img; //deklarasi image item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTambah=(Button)findViewById(R.id.button_tambah);

        btnTambah.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent inte = new Intent(MainActivity.this, CreateContactActivity.class);
                startActivity(inte);
            }
        });

        dbHelper = new ContactDbHelper(this);

        listView = (ListView)findViewById(R.id.list_view);
        jdl = new String[] {
                "Lutfi","Bayul","Anggar","Dia","Dan Dia"
        };
        ktr = new String[]{
                "0898391823912","87421734144","874874387434","76467427674212","2417612476142","21747612474812" //jumlahnya harus sama dengan jumlah judul
        };
        img = new String[]{
                Integer.toString(R.drawable.gambar),Integer.toString(R.drawable.gambar),Integer.toString(R.drawable.gambar),
                Integer.toString(R.drawable.gambar),Integer.toString(R.drawable.gambar)
        };
        mylist = new ArrayList<HashMap<String, String>>();

        for (int i=0; i<jdl.length; i++){
            map = new HashMap<String, String>();
            map.put("judul", jdl[i]);
            map.put("Keterangan", ktr[i]);
            map.put("Gambar", img[i]);
            mylist.add(map);
        }
        adapter = new SimpleAdapter(this, mylist, R.layout.list_item,
                new String[]{"judul", "Keterangan", "Gambar"}, new int[]{R.id.txt_judul,(R.id.txt_keterangan),(R.id.img)});
        listView.setAdapter(adapter);

    }


}
