package com.louisngatale.garagehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerRegistration extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText full_name,email,password,phone;
    FirebaseFirestore mDb;
    ProgressBar loading;
    Button register,login;
    String fNameVal,emailVal,pwdVal,phoneVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        full_name = findViewById(R.id.full_name_value);
        email = findViewById(R.id.email_value);
        password = findViewById(R.id.password_value);
        phone = findViewById(R.id.customer_phone_value);
        loading = findViewById(R.id.loading);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(v -> {
            Intent login = new Intent(CustomerRegistration.this,LoginActivity.class);
            startActivity(login);
            finish();
        });

        register.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            fNameVal = full_name.getText().toString();
            emailVal = email.getText().toString();
            pwdVal = password.getText().toString();
            phoneVal = phone.getText().toString();

            mAuth.createUserWithEmailAndPassword(emailVal,pwdVal)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String uid = task.getResult().getUser().getUid();
                        createProfile(uid, emailVal);
                    }else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(this, "Failed registering", Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }

    private void createProfile(String uid, String email) {
        HashMap<String,String> user = new HashMap<>();
        user.put("email",email);
        user.put("uid",uid);
        user.put("phone",phoneVal);
        user.put("role","Customer");
        user.put("Full Name",fNameVal);

        // Create a users document to store user data for quick retrieval
        mDb.collection("users")
            .document(uid)
            .set(user)
            .addOnCompleteListener(task1 -> {
                loading.setVisibility(View.GONE);
                if (task1.isSuccessful()){
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}