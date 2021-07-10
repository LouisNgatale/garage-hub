package com.louisngatale.garagehub;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.louisngatale.garagehub.adapters.RequestsRecView;
import com.louisngatale.garagehub.data.Requests;

import java.util.HashMap;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "Dash";
    Button editProfile;
    ImageButton logout;
    RecyclerView requests;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    RequestsRecView adapter;

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
            finish();
        });

        db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                if (!result.contains("role")) {
                    getRequests();
                }else{
                    mAuth.signOut();
                    finish();
                }
            }
        });

        editProfile.setOnClickListener(v -> {
            Intent editProfile = new Intent(DashboardActivity.this, EditLocationActivity.class);
            startActivity(editProfile);
        });
    }

    public void getRequests(){
        db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String companyId = task.getResult().get("companyId").toString();
                // Create query
                Query query = db.collection("companies/"+companyId+"/Requests");

                // Configure the adapter options
                FirestoreRecyclerOptions<Requests> options =
                        new FirestoreRecyclerOptions.Builder<Requests>()
                                .setQuery(query,Requests.class)
                                .build();

                adapter = new RequestsRecView(options,this);

                //TODO: Load category list items from database
                requests.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                requests.setAdapter(adapter);
                requests.setNestedScrollingEnabled(false);
                adapter.startListening();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
//        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
//        adapter.stopListening();
    }
}