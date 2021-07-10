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

public class CustomerLogin extends AppCompatActivity {
    EditText email,password;
    Button login,register;
    FirebaseAuth mAuth;
    ProgressBar loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        email = findViewById(R.id.email_value);
        password = findViewById(R.id.password_value);
        login = findViewById(R.id.login_page_login);
        register = findViewById(R.id.login_page_register);
        mAuth = FirebaseAuth.getInstance();
        loading = findViewById(R.id.loading);

        register.setOnClickListener(v -> {
            Intent register_intent = new Intent(CustomerLogin.this, CustomerRegistration.class);
            startActivity(register_intent);
            finish();
        });

        login.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loading.setVisibility(View.GONE);
                        Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(this, "Failed! Check your credentials", Toast.LENGTH_SHORT).show();
                    }
                });
        });

    }
}