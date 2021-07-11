package com.louisngatale.garagehub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.louisngatale.garagehub.adapters.CommentsAdapter;

import java.util.ArrayList;

public class ViewComments extends AppCompatActivity {
    String companyId;
    RecyclerView rec;
    CommentsAdapter adapter;
    ArrayList<String> comments = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        rec = findViewById(R.id.comments_rec);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent == null){
            Toast.makeText(this, "Company id is null", Toast.LENGTH_SHORT).show();
            finish();
        }
        assert intent != null;
        companyId = intent.getStringExtra("companyId");
        db.collection("companies")
                .document(companyId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> results = (ArrayList<String>) task.getResult().get("comments");
                assert results != null;
                comments.addAll(results);
                adapter = new CommentsAdapter(this,comments);
                rec.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                rec.setAdapter(adapter);
                rec.setNestedScrollingEnabled(false);
            }
        });


    }
}