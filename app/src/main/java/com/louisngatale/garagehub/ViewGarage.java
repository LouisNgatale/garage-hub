package com.louisngatale.garagehub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.louisngatale.garagehub.adapters.ServicesAdapter;
import com.louisngatale.garagehub.data.Services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewGarage extends AppCompatActivity {
    private final String TAG = "MAPS";
    private FirebaseFirestore mDb;
    TextView company_name,opening_hour,closing_hour,phone_number;
    RecyclerView services_rec;
    ServicesAdapter services_adapter;
    private ArrayList<Services> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_garage);
        items = new ArrayList<Services>();

        company_name = findViewById(R.id.company_name);
        opening_hour = findViewById(R.id.opening_hour);
        closing_hour = findViewById(R.id.closing_hour);
        phone_number = findViewById(R.id.phone_number);

        services_rec = findViewById(R.id.services_list);
        mDb = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        String obj = intent.getStringExtra("id");

        String documentPath = obj;

        mDb.collection("companies")
                .document(documentPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot result = task.getResult();

                        company_name.setText((CharSequence) result.get("company"));
                        phone_number.setText((CharSequence) result.get("phone"));

                        try {
                            HashMap<Object,Object> services = (HashMap<Object,Object>) result.get("services");

                            services.forEach((k,v) -> {
                                items.add(new Services(k,v));
                            });

                            services_adapter = new ServicesAdapter(this, items);

                            services_rec.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                            services_rec.setNestedScrollingEnabled(false);
                            services_rec.setAdapter(services_adapter);

                        }catch (Exception e){
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
    }
}