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

public class UpdateItemActivity extends AppCompatActivity {

    private static final String TAG = "UpdateNewItem";
    EditText nameEditText, descEditText, priceEditText, imageurlEditText;
    Button updateItemButton, backButton;

    // progress dialog
    ProgressDialog pd;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Update item");

        // változókba xml
        nameEditText = findViewById(R.id.updateNameEditText);
        descEditText = findViewById(R.id.updateDescEditText);
        priceEditText = findViewById(R.id.updatePriceEditText);
        imageurlEditText = findViewById(R.id.updateImageurlEditText);
        updateItemButton = findViewById(R.id.updateItemButton);
        backButton = findViewById(R.id.backButton);


        // Put original values in EditTexts
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            nameEditText.setText(extra.getString("name"));
            descEditText.setText(extra.getString("desc"));
            priceEditText.setText(extra.getString("price"));
            imageurlEditText.setText(extra.getString("image"));
            // VAN ID IS!
        }

        // progress dialog
        pd = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();

        updateItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String desc = descEditText.getText().toString().trim();
                Integer price = Integer.valueOf(priceEditText.getText().toString().trim());
                String imageurl = imageurlEditText.getText().toString().trim();
                String id = extra.getString("id");
                updateItem(id, name, desc, price, imageurl);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateItemActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    private void updateItem(String id, String name, String desc, Integer price, String imageurl) {
        pd.setTitle("Updating item");
//        pd.show(); // valamiért elkezdett nem meghívodni az OnSuccessListener

        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("description", desc);
        item.put("price", price);
        item.put("image", imageurl);
        item.put("id", id);

        db.collection("items").document(id).update(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //                        Log.d(TAG, "Item updated with ID: " + documentReference.getId());
                pd.hide();
                Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
                pd.hide();
                Toast.makeText(getApplicationContext(), "Item couldn't be updated!", Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(getApplicationContext(), "Item updated!", Toast.LENGTH_SHORT).show();
    }
}