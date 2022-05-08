package com.example.diwhywebshop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FoamActivity extends AppCompatActivity {

    RecyclerView recView;
    ArrayList<ItemModel> itemArrayList;
    ItemAdapter itemAdapter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ProgressDialog pd;
    ImageView imgBtn;

    Button notificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foam);

        // Notification button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("shop", "shop", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        notificationBtn = findViewById(R.id.notificationBtn);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "See question in your notifications!", Toast.LENGTH_SHORT).show();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "shop");
                builder.setContentTitle("DiWHY Webshop");
                builder.setContentText("Do you like spray foam?");
                builder.setSmallIcon(R.drawable.diwhy);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(FoamActivity.this);
                managerCompat.notify(1, builder.build());

            }
        });

        // ImageButton
        imgBtn = findViewById(R.id.buttonishImageView);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "NO FOAM!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

        // ProgressDialog
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.show();

        // Setting up RecyclerView
        recView = findViewById(R.id.itemsRecView);
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(this));

//        Button deleteBtnHere = findViewById(R.id.itemDeleteButton);
//        Button updateBtnHere = findViewById(R.id.itemUpdateButton);
//        deleteBtnHere.setVisibility(View.INVISIBLE);
//        updateBtnHere.setVisibility(View.INVISIBLE);

        itemArrayList = new ArrayList<ItemModel>();
        itemAdapter = new ItemAdapter(FoamActivity.this, itemArrayList, "foam");

        // Get items from firestore
        getItemsFromFirestore("price", Query.Direction.ASCENDING);
        recView.setAdapter(itemAdapter);
    }

    private void getItemsFromFirestore(String field, Query.Direction direction) {

        ArrayList<String> list = new ArrayList();
        list.add("Spray foam mirror");
        list.add("Spray foam bedside tables");
        list.add("Spray foam coffee table");
        list.add("Soft cabinet");

        firestore.collection("items")
                .whereIn("name", list)
                .orderBy(field, direction)
                .limit(3)
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
}