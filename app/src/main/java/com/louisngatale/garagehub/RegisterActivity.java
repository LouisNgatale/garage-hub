package com.louisngatale.garagehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    Button register,login;
    EditText fullName,company,email,password;
    FirebaseAuth mAuth;
    FirebaseFirestore mDb;
    String fNameVal, companyVal, emailVal, pwdVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.full_name_value);
        company = findViewById(R.id.company_value);
        email = findViewById(R.id.email_value);
        password = findViewById(R.id.password_value);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        login.setOnClickListener(v -> {
            Intent login = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(login);
            finish();
        });

        register.setOnClickListener(v -> {
            fNameVal = fullName.getText().toString();
            companyVal = company.getText().toString();
            emailVal = email.getText().toString();
            pwdVal = password.getText().toString();

            mAuth.createUserWithEmailAndPassword(emailVal,pwdVal)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            String uid = task.getResult().getUser().getUid();

                            createProfile(uid, emailVal);
                        }
                    });
        });

    }

    private void createProfile(String uid, String email) {
        HashMap<String,String> companies = new HashMap<>();
        companies.put("owner",uid);
        companies.put("company",companyVal);

        mDb.collection("companies")
                .add(companies)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        String id = task.getResult().getId();
                        HashMap<String,String> user = new HashMap<>();
                        user.put("email",email);
                        user.put("uid",uid);
                        user.put("company",companyVal);
                        user.put("companyId",id);
                        user.put("Full Name",fNameVal);

                        mDb.collection("users")
                                .document(uid)
                                .set(user);
                    }
                });

        Intent dashboard = new Intent(RegisterActivity.this,DashboardActivity.class);
        startActivity(dashboard);
        finish();
    }
}