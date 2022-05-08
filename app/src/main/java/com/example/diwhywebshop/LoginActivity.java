package com.example.diwhywebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    TextView goToRegisterText;
    Button loginButton;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.logEmailEditText);
        passwordEditText = findViewById(R.id.logPasswordEditText);

        goToRegisterText = findViewById(R.id.goToRegisterText);

        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.logProgressBar);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Logging in
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Something went wrong!\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

        goToRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        // "authguard"
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        super.onResume();
    }
}