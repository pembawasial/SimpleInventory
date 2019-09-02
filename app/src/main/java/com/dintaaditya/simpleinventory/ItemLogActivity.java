package com.dintaaditya.simpleinventory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dintaaditya.simpleinventory.Model.ItemLog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ItemLogActivity extends AppCompatActivity {

    ItemLogAdapter itemLogAdapter;
    CollectionReference itemLogRef;
    RecyclerView itemLogRecycler;
    String SKU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_log);

        SKU = getIntent().getStringExtra("SKU");

        itemLogRef = FirebaseFirestore.getInstance().collection("Item/" + SKU + "/Log");
        itemLogRecycler = findViewById(R.id.item_log_recycler);
        Query query = itemLogRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ItemLog> options = new FirestoreRecyclerOptions.Builder<ItemLog>()
                .setQuery(query, ItemLog.class)
                .build();
        itemLogAdapter = new ItemLogAdapter(options);
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemLogAdapter.startListening();
        itemLogRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        itemLogRecycler.setAdapter(itemLogAdapter);
        itemLogRecycler.setNestedScrollingEnabled(false);

    }

    @Override
    protected void onStop() {
        super.onStop();
        itemLogAdapter.stopListening();
    }
}
