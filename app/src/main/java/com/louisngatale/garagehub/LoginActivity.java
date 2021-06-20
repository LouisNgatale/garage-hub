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

public class LoginActivity extends AppCompatActivity {
    Button login, register;
    EditText email, password;
    String emailVal, pwdVal;
    FirebaseAuth mAuth;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        loading = findViewById(R.id.loading);

        register = findViewById(R.id.register);

        register.setOnClickListener(v -> {
            Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(register);
            finish();
        });

        email = findViewById(R.id.email_value);
        password = findViewById(R.id.password_value);

        login.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            emailVal = email.getText().toString();
            pwdVal = password.getText().toString();

            mAuth.signInWithEmailAndPassword(emailVal, pwdVal)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loading.setVisibility(View.INVISIBLE);
                        Intent dasboard = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(dasboard);
                        finish();
                    }else {
                        loading.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Failed logging in!", Toast.LENGTH_SHORT).show();
                    }
                });
        });

    }
}