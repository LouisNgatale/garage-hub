package com.louisngatale.garagehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.io.Serializable;

public class ViewGarage extends AppCompatActivity {
    private final String TAG = "MAPS";
    private FirebaseFirestore mDb;
    TextView company_name,opening_hour,closing_hour,phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_garage);

        company_name = findViewById(R.id.company_name);
        opening_hour = findViewById(R.id.opening_hour);
        closing_hour = findViewById(R.id.closing_hour);
        phone_number = findViewById(R.id.phone_number);

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

                    }
                });
    }
}