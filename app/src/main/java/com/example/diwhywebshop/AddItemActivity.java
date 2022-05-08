package com.example.diwhywebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {

    private static final String TAG = "AddNewItem";
    EditText nameEditText, descEditText, priceEditText, imageurlEditText;
    Button newItemButton, backButton;

    // progress dialog
    ProgressDialog pd;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Add item");

        // változókba xml
        nameEditText = findViewById(R.id.nameEditText);
        descEditText = findViewById(R.id.descEditText);
        priceEditText = findViewById(R.id.priceEditText);
        imageurlEditText = findViewById(R.id.imageurlEditText);
        newItemButton = findViewById(R.id.newItemButton);
        backButton = findViewById(R.id.backButton);

        // progress dialog
        pd = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();

        newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String desc = descEditText.getText().toString().trim();
                Integer price = Integer.valueOf(priceEditText.getText().toString().trim());
                String imageurl = imageurlEditText.getText().toString().trim();
                addNewItem(name, desc, price, imageurl);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddItemActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    private void addNewItem(String name, String desc, Integer price, String imageurl) {
        pd.setTitle("Adding new item");
//        pd.show(); // valamiért elkezdett nem meghívodni az OnSuccessListener

        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("description", desc);
        item.put("price", price);
        item.put("image", imageurl);
        item.put("id", "");

        db.collection("items")
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        pd.hide();
                        Toast.makeText(getApplicationContext(), "Item added successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        pd.hide();
                        Toast.makeText(getApplicationContext(), "Item couldn't be added!", Toast.LENGTH_SHORT).show();
                    }
                });

        Toast.makeText(getApplicationContext(), "New item added!", Toast.LENGTH_SHORT).show();

    }
}