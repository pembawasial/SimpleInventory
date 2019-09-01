package com.dintaaditya.simpleinventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dintaaditya.simpleinventory.Model.Item;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ItemActivity extends AppCompatActivity {
    FloatingActionButton fabAdd;
    ItemAdapter itemAdapter;
    CollectionReference itemRef;
    RecyclerView itemRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        fabAdd = findViewById(R.id.fab_add);
        itemRef = FirebaseFirestore.getInstance().collection("Item");
        itemRecycler = findViewById(R.id.item_recycler);

        Query query = itemRef.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();
        itemAdapter = new ItemAdapter(options);
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemAdapter.startListening();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ItemActivity.this, InputActivity.class));
            }
        });

        itemRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        itemRecycler.setAdapter(itemAdapter);
        itemRecycler.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemAdapter.stopListening();
    }
}
