package com.example.diwhywebshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recView;
    ArrayList<ItemModel> itemArrayList;
    ItemAdapter itemAdapter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ProgressDialog pd;
    ImageView imgBtn;

    FloatingActionButton ab;

    Boolean notFirst = false;

    Spinner spinner;
    String[] spinnerChoices = {"Cheap to expensive", "Expensive to cheap", "Reverse alphabetic", "Alphabetic"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ImageButton
        imgBtn = findViewById(R.id.buttonishImageView);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "FOAM!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), FoamActivity.class));
            }
        });

        // ProgressDialog
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Loading items");
        pd.show();

        // Setting up RecyclerView
        recView = findViewById(R.id.itemsRecView);
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(this));

        itemArrayList = new ArrayList<ItemModel>();
        itemAdapter = new ItemAdapter(HomeActivity.this, itemArrayList, "home");

        // Spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerChoices);
        spinnerAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // Get items from firestore
        getItemsFromFirestore("price", Query.Direction.ASCENDING);
        recView.setAdapter(itemAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerChoices[i] == "Alphabetic") {
                    Toast.makeText(getApplicationContext(), "New order:\n" + spinnerChoices[i], Toast.LENGTH_SHORT).show();
                    itemAdapter.clearData();
                    getItemsFromFirestore("name", Query.Direction.ASCENDING);
                    recView.setAdapter(itemAdapter);
                } else if (spinnerChoices[i] == "Reverse alphabetic") {
                    Toast.makeText(getApplicationContext(), "New order:\n" + spinnerChoices[i], Toast.LENGTH_SHORT).show();
                    itemAdapter.clearData();
                    getItemsFromFirestore("name", Query.Direction.DESCENDING);
                    recView.setAdapter(itemAdapter);
                } else if (spinnerChoices[i] == "Cheap to expensive" && notFirst) {
                    Toast.makeText(getApplicationContext(), "New order:\n" + spinnerChoices[i], Toast.LENGTH_SHORT).show();
                    itemAdapter.clearData();
                    getItemsFromFirestore("price", Query.Direction.ASCENDING);
                    recView.setAdapter(itemAdapter);
                } else if (spinnerChoices[i] == "Expensive to cheap") {
                    Toast.makeText(getApplicationContext(), "New order:\n" + spinnerChoices[i], Toast.LENGTH_SHORT).show();
                    itemAdapter.clearData();
                    getItemsFromFirestore("price", Query.Direction.DESCENDING);
                    recView.setAdapter(itemAdapter);
                }
                notFirst = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ab = findViewById(R.id.addFloatButton);
        ab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddItemActivity.class));
            }
        });

        addIdsToItems();
    }

    public void deleteItem(ItemModel item) {
//        Toast.makeText(getApplicationContext(), "Delete!\n" + item.getID(), Toast.LENGTH_SHORT).show();
        firestore.collection("items").document(item.getID()).delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Item succesfully deleted!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Couldn't delete item!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(getApplicationContext(), "Item deleted!", Toast.LENGTH_SHORT).show(); // Mert itt sem fut már le az OnSuccessListener, gondolom a mostani Firestore update miatt vagy idk
        finish();
        startActivity(getIntent());
    }

    public void updateItem(ItemModel item) {
//        Toast.makeText(getApplicationContext(), "Update:\n" + item.getID(), Toast.LENGTH_LONG).show();

        // Add data to UpdateItemActivity
        Intent i = new Intent(getApplicationContext(), UpdateItemActivity.class);
        i.putExtra("id", item.getID());
        i.putExtra("name", item.getName());
        i.putExtra("price", String.valueOf(item.getPrice()));
        i.putExtra("desc", item.getDescription());
        i.putExtra("image", item.getImage());
        startActivity(i);
    }

    public void addIdsToItems() {
//        Toast.makeText(getApplicationContext(), "IDS ADDED!\n", Toast.LENGTH_LONG).show();

        // Adding IDs to items
        firestore.collection("items").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                ItemModel item = doc.toObject(ItemModel.class);
                item.setID(doc.getId());

                ArrayMap itemMap = new ArrayMap<>();
                itemMap.put("name", item.getName());
                itemMap.put("description", item.getDescription());
                itemMap.put("price", item.getPrice());
                itemMap.put("id", item.getID());
                itemMap.put("image", item.getImage());

                this.firestore.collection("items").document(doc.getId()).update(itemMap);
            }
        });
    }

    private void getItemsFromFirestore(String field, Query.Direction direction) {
        addIdsToItems();

        firestore.collection("items").orderBy(field, direction)
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    Log.d("FIRE", error.getMessage());
                    return;
                }
                for (DocumentChange dc: value.getDocumentChanges()) {

                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        itemArrayList.add(dc.getDocument().toObject(ItemModel.class));
                    }
                    itemAdapter.notifyDataSetChanged();
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }

            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        Toast.makeText(getApplicationContext(), "Bye!", Toast.LENGTH_SHORT).show();
        finish();
    }
}