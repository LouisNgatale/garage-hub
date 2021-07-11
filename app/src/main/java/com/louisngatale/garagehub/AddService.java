package com.louisngatale.garagehub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddService extends AppCompatActivity {
    Button save;
    EditText service_name, service_price;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        service_name = findViewById(R.id.service_name_value);
        service_price = findViewById(R.id.service_price_value);
        save = findViewById(R.id.save);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        save.setOnClickListener(v -> {
            db.collection("users")
                    .document(Objects.requireNonNull(mAuth.getUid()))
                    .get()
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String companyId = Objects.requireNonNull(task.getResult().get("companyId"))
                            .toString();

//                    Map<String,Map<String,Object>> services = new HashMap<>();
                    HashMap<String,String> serv = new HashMap<>();
                    serv.put("service",service_name.getText().toString());
                    serv.put("price",service_price.getText().toString());
//                    services.put("services",serv);

                    db.collection("companies")
                            .document(companyId)
                            .collection("services")
                            .add(serv).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show();
                            service_name.setText("");
                            service_price.setText("");
                        }
                    });
                }
            });
        });


    }
}