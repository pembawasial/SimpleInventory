package com.dintaaditya.simpleinventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    String SKU;
    ImageView imgItem;
    EditText edtSKU, edtName, edtStock;
    Button btnShowLog, btnCancel, btnUpdate;
    ImageButton btnPlus, btnMinus;
    DocumentReference itemDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        SKU = getIntent().getStringExtra("SKU");

        imgItem = findViewById(R.id.img_item);
        edtSKU = findViewById(R.id.edt_SKU);
        edtName = findViewById(R.id.edt_name);
        edtStock = findViewById(R.id.edt_stock);

        btnShowLog = findViewById(R.id.btn_log_item);
        btnCancel = findViewById(R.id.btn_cancel);
        btnUpdate = findViewById(R.id.btn_update);
        btnPlus = findViewById(R.id.btn_plus);
        btnMinus = findViewById(R.id.btn_minus);

        btnShowLog.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);

        formState(false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        showItemDetail();

    }

    private void showItemDetail() {
        itemDetail = FirebaseFirestore.getInstance().document("Item/" + SKU);
        itemDetail.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String name = documentSnapshot.getString("name");
                String image = documentSnapshot.getString("image");
                Integer stock = documentSnapshot.getLong("stock").intValue();

                edtSKU.setText(SKU);
                edtName.setText(name);
                edtStock.setText(String.valueOf(stock));
                Glide.with(DetailActivity.this).load(image).apply(RequestOptions.circleCropTransform()).into(imgItem);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_log_item:
                startActivity(new Intent(DetailActivity.this, ItemLogActivity.class).putExtra("SKU", SKU));
                break;
            case R.id.btn_cancel:
                showItemDetail();
                formState(false);
                break;
            case R.id.btn_update:
//                updateData();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


    //give an action based on selected item id
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_edit:
                formState(true);
//                startActivity(new Intent(getContext(), EditProfileActivity.class).putExtra("uid", uid));
                break;
            case R.id.detail_delete:
                showDeleteDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Data")
                .setMessage("Do you want to delete this data?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        itemDetail.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(DetailActivity.this, "Successfully deleted data", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), ItemActivity.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DetailActivity.this, "Failed to delete data, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .setNegativeButton("NO", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void formState(boolean status) {
        edtName.setEnabled(status);
        edtStock.setEnabled(status);
        btnPlus.setEnabled(status);
        btnMinus.setEnabled(status);
        if (status) {
            btnCancel.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);
            btnShowLog.setVisibility(View.GONE);

        } else {
            btnCancel.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.GONE);
            btnShowLog.setVisibility(View.VISIBLE);
        }
    }
}
