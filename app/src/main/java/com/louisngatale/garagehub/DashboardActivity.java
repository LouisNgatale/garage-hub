package com.louisngatale.garagehub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    Button editProfile;
    ImageButton logout;
    RecyclerView requests;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        requests = findViewById(R.id.requests);
        editProfile = findViewById(R.id.editLocation);
        logout = findViewById(R.id.logout);

        logout.setOnClickListener(v -> {
            mAuth.signOut();

        });

        editProfile.setOnClickListener(v -> {
            Intent editProfile = new Intent(DashboardActivity.this, EditLocationActivity.class);
            startActivity(editProfile);
        });

        // Get the new updated location from the activity and store to database

//        updateLocation();
    }

    private void updateLocation() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users")
                .document(uid)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot result = task.getResult();
                        String companyId = result.get("companyId").toString();
                        HashMap<String ,Object> latLng = new HashMap<>();
                        latLng.put("Latitude", "Latitude");
                        latLng.put("Longitude", "Longitude");
                        HashMap<String,HashMap<String,Object>> location = new HashMap<>();

                        location.put("Address",latLng);

                        // Update user's database
                        db.collection("companies")
                                .document(uid)
                                .set(location);

                    }
        });


    }


}