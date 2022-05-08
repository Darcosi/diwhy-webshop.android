package com.example.diwhywebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "REG";
    EditText emailEditText, passwordEditText, nameEditText, addressEditText, phoneEditText;
    TextView goToLoginText;
    Button registerButton;
    ProgressBar progressBar;
    FirebaseAuth auth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.regEmailEditText);
        passwordEditText = findViewById(R.id.regPasswordEditText);
        nameEditText = findViewById(R.id.regFullNameEditTextText);
        addressEditText = findViewById(R.id.regAddressEditText);
        phoneEditText = findViewById(R.id.regPhoneEditText);

        goToLoginText = findViewById(R.id.goToLoginText);

        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.regProgressBar);

        auth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Registering
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registered successfully!", Toast.LENGTH_SHORT).show();
                            userID = auth.getCurrentUser().getUid();

                            //TODO: SERVICE-be

                            // Add user to Firestore
                            DocumentReference docRef = firestore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("name", name);
                            user.put("address", address);
                            user.put("phone", phone);
                            user.put("isAdmin", false);
                            docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, userID + " added to Firestore!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            });

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

        goToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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