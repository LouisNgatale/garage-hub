package com.louisngatale.garagehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.louisngatale.garagehub.adapters.ServicesAdapter;
import com.louisngatale.garagehub.data.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ViewGarage extends AppCompatActivity {
    private final String TAG = "MAPS";
    private FirebaseFirestore mDb;
    TextView company_name, opening_hour, closing_hour, phone_number;
    RecyclerView services_rec;
    ServicesAdapter services_adapter;
    private ArrayList<Services> items;
    Button request_service;
    FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_CODE = 100;
    String documentPath;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_garage);
        items = new ArrayList<Services>();
        mAuth = FirebaseAuth.getInstance();
        company_name = findViewById(R.id.company_name);
        opening_hour = findViewById(R.id.opening_hour);
        closing_hour = findViewById(R.id.closing_hour);
        phone_number = findViewById(R.id.phone_number);
        request_service = findViewById(R.id.emergency_request);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        services_rec = findViewById(R.id.services_list);
        mDb = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        String obj = intent.getStringExtra("id");

        documentPath = obj;

        request_service.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                mDb.collection("users")
                        .document(Objects.requireNonNull(mAuth.getUid()))
                        .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot result = task.getResult();
                        if (result.contains("role")) {
                            if (result.get("role").toString().equals("Customer")) {
                                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this),
                                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                                        == PackageManager.PERMISSION_DENIED) {
                                    // Request permission
                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            LOCATION_PERMISSION_CODE);
                                } else {
                                    register(result);
                                }
                            }
                        }else{
                            Toast.makeText(this, "This is not a customer account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                Intent login = new Intent(ViewGarage.this,CustomerLogin.class);
                startActivity(login);
            }
        });

        mDb.collection("companies")
            .document(documentPath)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    company_name.setText((CharSequence) result.get("company"));
                    phone_number.setText((CharSequence) result.get("phone"));
                    try {
                        HashMap<Object, Object> services = (HashMap<Object, Object>) result.get("services");

                        services.forEach((k, v) -> {
                            items.add(new Services(k, v));
                        });
                        services_adapter = new ServicesAdapter(this, items);

                        services_rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                        services_rec.setNestedScrollingEnabled(false);
                        services_rec.setAdapter(services_adapter);
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
                });
    }

    private void register(DocumentSnapshot result) {
        HashMap<String, String> request = new HashMap<>();
        request.put("Customer", result.get("Full Name").toString());
        request.put("phone", result.get("phone").toString());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    request.put("Latitude", String.valueOf(location.getLatitude()));
                    request.put("a", "s");
                    request.put("Longitude", String.valueOf(location.getLongitude()));
                }
            });
        mDb.collection("companies")
            .document(documentPath)
            .collection("Requests")
            .add(request)
            .addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()){
                    Toast.makeText(this, "Service requested successfully", Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     * Request permission to access different features
     * @Author: Eng. Louis Ngatale
     * **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(this,
                        "Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}